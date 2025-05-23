package com.securecollab.file;

import com.securecollab.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class StoredFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFilename;
    private String storedFilename;
    private String contentType;
    private Long workspaceId;
    private Instant uploadedAt;

    @ManyToOne(optional = false)
    private User uploader;
}
