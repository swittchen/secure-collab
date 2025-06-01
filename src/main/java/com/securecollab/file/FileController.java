package com.securecollab.file;

import com.securecollab.audit.Audit;
import com.securecollab.user.User;
import com.securecollab.workspace.WorkspaceSecurityService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileController {

    private final StoredFileRepository fileRepository;
    private final FileStorageService fileStorage;
    private final WorkspaceSecurityService workspaceSecurity;

    @PostMapping("/workspaces/{workspaceId}/files")
    @PreAuthorize("@workspaceSecurity.isMember(#workspaceId)")
    @Transactional
    @Audit(action = "UPLOAD_FILE")
    public ResponseEntity<?> upload(@PathVariable UUID workspaceId,
                                    @RequestParam("file") MultipartFile file,
                                    @AuthenticationPrincipal User user) throws IOException {
        String storedFilename = fileStorage.storeFile(file);

        StoredFile entity = new StoredFile();
        entity.setOriginalFilename(file.getOriginalFilename());
        entity.setStoredFilename(storedFilename);
        entity.setContentType(file.getContentType());
        entity.setWorkspaceId(workspaceId);
        entity.setUploader(user);
        entity.setUploadedAt(Instant.now());

        fileRepository.save(entity);

        return ResponseEntity.ok("File uploaded");
    }

    @GetMapping("/workspaces/{workspaces}/files")
    @PreAuthorize("@workspaceSecurity.isMember(#workspaceId)")
    @Audit(action = "LIST_FILES")
    public List<StoredFile> list(@PathVariable UUID workspaceId) {
        return fileRepository.findByWorkspaceId(workspaceId);
    }

    @GetMapping("/files/{fileId}/download")
    @PreAuthorize("@workspaceSecurity.isMember(@fileRepository.getWorkspaceId(#fileId))")
    @Audit(action = "DOWNLOAD_FILE")
    public ResponseEntity<Resource> download(@PathVariable UUID fileId) throws MalformedURLException {
        StoredFile file = fileRepository.findById(fileId).orElseThrow();
        Path path = fileStorage.load(file.getStoredFilename());
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalFilename() + "\"")
                .contentType(org.springframework.http.MediaType.parseMediaType(file.getContentType()))
                .body(resource);
    }

    @DeleteMapping("/files/{fileId}")
    @PreAuthorize("@workspaceSecurity.hasRole(@fileRepository.getWorkspaceId(#fileId), 'OWNER')")
    @Audit(action = "DELETE_FILE")
    public ResponseEntity<?> delete (@PathVariable UUID fileId) throws IOException{
        StoredFile file = fileRepository.findById(fileId).orElseThrow();
        fileStorage.delete(file.getStoredFilename());
        fileRepository.delete(file);
        return ResponseEntity.ok("Deleted");
    }
}
