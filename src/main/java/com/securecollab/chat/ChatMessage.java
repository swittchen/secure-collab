package com.securecollab.chat;

import com.securecollab.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ChatMessage {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID workspaceId;

    @ManyToOne(optional = false)
    private User sender;

    private String content;

    private Instant timestamp = Instant.now();
}
