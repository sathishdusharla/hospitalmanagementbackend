package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            // Path traversal protection
            if (filename == null || filename.isEmpty() || filename.contains("..")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Path path = Paths.get(uploadDir).resolve(filename);
            Resource resource = new UrlResource(new URI("file:" + path.toAbsolutePath()));

            if (!Files.exists(path)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
        } catch (URISyntaxException | java.io.IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
