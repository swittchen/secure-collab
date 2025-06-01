package com.securecollab.file;

import com.securecollab.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class StoredFile {

    @Id
    @GeneratedValue
    private UUID id;

    private String originalFilename;
    private String storedFilename;
    private String contentType;
    private UUID workspaceId;
    private Instant uploadedAt;

    @ManyToOne(optional = false)
    private User uploader;
}
