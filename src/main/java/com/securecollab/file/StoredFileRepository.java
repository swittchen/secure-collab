package com.securecollab.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface StoredFileRepository extends JpaRepository<StoredFile, UUID> {
    List<StoredFile> findByWorkspaceId(UUID workspaceId);

    @Query("select f.workspaceId from StoredFile f where f.id = :fileId")
    Long getWorkspaceId(UUID fieldId);
}
