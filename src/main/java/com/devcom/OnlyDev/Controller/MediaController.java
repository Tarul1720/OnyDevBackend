package com.devcom.OnlyDev.Controller;

import com.devcom.OnlyDev.Service.MediaStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaStorageService storageService;

    public MediaController(MediaStorageService storageService) {
        this.storageService = storageService;
    }
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // Returns email/username
    }
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
    @PostMapping("/upload/{postId}/{subpostId}")
    public ResponseEntity<String> upload(
            @PathVariable String postId,
            @PathVariable String subpostId,
            @RequestParam("file") MultipartFile file
    ) throws Exception {

        String userId = getCurrentUserId();

        String originalName = file.getOriginalFilename();
        String extension = getFileExtension(originalName);
        String contentType = file.getContentType(); // IMPORTANT

        String objectKey =
                "raw/posts/" + userId + "/" + postId + "/" + subpostId + "/" +
                        UUID.randomUUID() + extension;

        storageService.upload(
                "posts",
                objectKey,
                file.getInputStream(),
                file.getSize(),
                contentType
        );

        return ResponseEntity.ok(objectKey);
    }

}
