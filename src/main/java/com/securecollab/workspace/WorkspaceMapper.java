package com.securecollab.workspace;

import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class WorkspaceMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public WorkspaceDto toDto(Workspace entity) {
        return new WorkspaceDto(
                entity.getId(),
                entity.getName(),
                entity.getCreatedBy(),
                entity.getDescription(),
                entity.getCreatedAt() !=null?entity.getCreatedAt().toString(): null
        );
    }
}
