package com.securecollab.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {
    List<StoredFile> findByWorkspaceId(Long workspaceId);

    @Query("select f.workspaceId from StoredFile f where f.id = :fileId")
    Long getWorkspaceId(Long fieldId);
}
