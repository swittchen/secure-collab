package com.securecollab.chat;

import com.securecollab.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long workspaceId;

    @ManyToOne(optional = false)
    private User sender;

    private String content;

    private Instant timestamp = Instant.now();
}
