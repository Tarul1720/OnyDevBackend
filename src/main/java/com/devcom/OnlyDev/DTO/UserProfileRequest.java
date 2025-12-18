package com.devcom.OnlyDev.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Request DTO for creating/updating profile
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequest {
    private String username;
    private String email;
    private String bio;
    private String picture;
}

// Response DTO
@Data
@AllArgsConstructor
@NoArgsConstructor
class UserProfileResponse {
    private String id;
    private String username;
    private String email;
    private String bio;
    private String picture;
}

// Update specific fields
@Data
@AllArgsConstructor
@NoArgsConstructor
class UpdateBioRequest {
    private String bio;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class UpdatePictureRequest {
    private String picture;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class UpdateUsernameRequest {
    private String username;
}