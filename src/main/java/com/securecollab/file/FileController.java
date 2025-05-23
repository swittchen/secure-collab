package com.securecollab.file;

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
    public ResponseEntity<?> upload(@PathVariable Long workspaceId,
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
    public List<StoredFile> list(@PathVariable Long workspaceId) {
        return fileRepository.findByWorkspaceId(workspaceId);
    }

    @GetMapping("/files/{fileId}/download")
    @PreAuthorize("@workspaceSecurity.isMember(@fileRepository.getWorkspaceId(#fileId))")
    public ResponseEntity<Resource> download(@PathVariable Long fileId) throws MalformedURLException {
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
    public ResponseEntity<?> delete (@PathVariable Long fileId) throws IOException{
        StoredFile file = fileRepository.findById(fileId).orElseThrow();
        fileStorage.delete(file.getStoredFilename());
        fileRepository.delete(file);
        return ResponseEntity.ok("Deleted");
    }
}
