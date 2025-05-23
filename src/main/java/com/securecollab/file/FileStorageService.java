package com.securecollab.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) throws IOException {
        this.uploadDir = Paths.get(uploadDir);
        Files.createDirectories(this.uploadDir);
    }

    public String storeFile(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path target = uploadDir.resolve(filename);
        file.transferTo(target);
        return filename;
    }

    public Path load(String storedFilename) {
        return uploadDir.resolve(storedFilename);
    }

    public void delete(String storedFilename) throws IOException {
        Files.deleteIfExists(load(storedFilename));
    }

}
