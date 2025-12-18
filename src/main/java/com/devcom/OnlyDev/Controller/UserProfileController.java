package com.devcom.OnlyDev.Controller;

import com.devcom.OnlyDev.DTO.UserProfileRequest;
import com.devcom.OnlyDev.Modal.UserProfile;
import com.devcom.OnlyDev.Service.ImageService;
import com.devcom.OnlyDev.Service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final ImageService imageService;

    @Value("${image.upload.dir:uploads/images}")
    private String imageDirectory;

    // Create profile with image
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProfile(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "picture", required = false) MultipartFile picture) {
        try {
            UserProfileRequest request = new UserProfileRequest();
            request.setUsername(username);
            request.setEmail(email);
            request.setBio(bio);

            // Save image if provided
            if (picture != null && !picture.isEmpty()) {
                String imageName = imageService.saveImageToStorage(imageDirectory, picture);
                request.setPicture(imageName);
            }

            UserProfile profile = userProfileService.createProfile(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(profile);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Update profile picture only
    @PatchMapping("/{id}/picture")
    public ResponseEntity<?> updateProfilePicture(
            @PathVariable String id,
            @RequestParam("picture") MultipartFile picture) {
        try {
            // Get existing profile to delete old image
            UserProfile existingProfile = userProfileService.getProfileById(id)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));

            // Delete old image if exists
            if (existingProfile.getPicture() != null) {
                imageService.deleteImage(imageDirectory, existingProfile.getPicture());
            }

            // Save new image
            String imageName = imageService.saveImageToStorage(imageDirectory, picture);
            UserProfile updatedProfile = userProfileService.updatePicture(id, imageName);

            return ResponseEntity.ok(updatedProfile);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Update full profile with optional new image
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @PathVariable String id,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "picture", required = false) MultipartFile picture) {
        try {
            UserProfileRequest request = new UserProfileRequest();
            request.setUsername(username);
            request.setEmail(email);
            request.setBio(bio);

            // Handle image update if provided
            if (picture != null && !picture.isEmpty()) {
                // Get existing profile to delete old image
                UserProfile existingProfile = userProfileService.getProfileById(id)
                        .orElseThrow(() -> new RuntimeException("Profile not found"));

                // Delete old image if exists
                if (existingProfile.getPicture() != null) {
                    imageService.deleteImage(imageDirectory, existingProfile.getPicture());
                }

                // Save new image
                String imageName = imageService.saveImageToStorage(imageDirectory, picture);
                request.setPicture(imageName);
            }

            UserProfile updatedProfile = userProfileService.updateProfile(id, request);
            return ResponseEntity.ok(updatedProfile);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload image: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Get profile image
    @GetMapping("/{id}/picture")
    public ResponseEntity<?> getProfilePicture(@PathVariable String id) {
        try {
            UserProfile profile = userProfileService.getProfileById(id)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));

            if (profile.getPicture() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No picture found for this profile");
            }

            byte[] imageBytes = imageService.getImage(imageDirectory, profile.getPicture());

            if (imageBytes == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Image file not found");
            }

            // Determine content type based on file extension
            String contentType = determineContentType(profile.getPicture());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve image: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Get all profiles
    @GetMapping
    public ResponseEntity<List<UserProfile>> getAllProfiles() {
        return ResponseEntity.ok(userProfileService.getAllProfiles());
    }

    // Get profile by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfileById(@PathVariable String id) {
        return userProfileService.getProfileById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Get profile by email
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getProfileByEmail(@PathVariable String email) {
        return userProfileService.getProfileByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
    @PostMapping("/email")
    public ResponseEntity<?> getAllProfileByEmail(@RequestBody List<String> emails) {
        List<UserProfile> profiles = userProfileService.getAllProfiles(emails);

        if (profiles == null || profiles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(profiles);
    }

    // Get profile by username
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getProfileByUsername(@PathVariable String username) {
        return userProfileService.getProfileByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // Delete profile (also deletes associated image)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable String id) {
        try {
            // Get profile to delete associated image
            UserProfile profile = userProfileService.getProfileById(id)
                    .orElseThrow(() -> new RuntimeException("Profile not found"));

            // Delete image if exists
            if (profile.getPicture() != null) {
                imageService.deleteImage(imageDirectory, profile.getPicture());
            }

            userProfileService.deleteProfile(id);
            return ResponseEntity.ok("Profile deleted successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Profile deleted but failed to delete image: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Search profiles by username
    @GetMapping("/search")
    public ResponseEntity<List<UserProfile>> searchProfiles(@RequestParam String username) {
        return ResponseEntity.ok(userProfileService.searchByUsername(username));
    }

    // Helper method to determine content type
    private String determineContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }
}