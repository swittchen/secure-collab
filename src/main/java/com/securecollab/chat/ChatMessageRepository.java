package com.securecollab.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByWorkspaceIdOrderByTimestampAsc(Long workspaceId);
}
