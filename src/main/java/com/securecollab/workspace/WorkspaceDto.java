package com.securecollab.workspace;

import com.securecollab.user.User;

import java.util.UUID;

public record WorkspaceDto(
        UUID id,
        String name,
        User createdBy,
        String description,
        String createdAt
) {}
