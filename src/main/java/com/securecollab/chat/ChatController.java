package com.securecollab.chat;

import com.securecollab.audit.Audit;
import com.securecollab.user.User;
import com.securecollab.workspace.WorkspaceSecurityService;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final WorkspaceSecurityService workspaceSecurity;

    @MessageMapping("/chat/{workspaceId}")
    @Audit(action = "SEND_MESSAGE")
    public void handleMessage(@Payload ChatInputMessage message,
                              @AuthenticationPrincipal User sender,
                              @PathVariable Long workspaceId) {
        if (!workspaceSecurity.isMember(workspaceId)) return;

        ChatMessage saved = new ChatMessage();
        saved.setContent(message.getContent());
        saved.setSender(sender);
        saved.setWorkspaceId(workspaceId);
        saved.setTimestamp(Instant.now());
        messageRepository.save(saved);

        messagingTemplate.convertAndSend("/topic/chat/" + workspaceId, new ChatOutputMessage(
                saved.getSender().getEmail(),
                saved.getContent(),
                saved.getTimestamp()
        ));
    }

    @GetMapping("/api/workspaces/{id}/chat")
    @PreAuthorize("@workspaceSecurity.isMember(#id)")
    public List<ChatOutputMessage> getChat(@PathVariable Long id) {
        return messageRepository.findByWorkspaceIdOrderByTimestampAsc(id).stream()
                .map(m -> new ChatOutputMessage(
                        m.getSender().getEmail(),
                        m.getContent(),
                        m.getTimestamp()
                ))
                .toList();
    }

    @Getter
    @Setter
    class ChatInputMessage {
        @NotBlank
        private String content;
    }

    record ChatOutputMessage(String sender, String content, Instant timestamp) {
    }
}
