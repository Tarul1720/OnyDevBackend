package com.devcom.OnlyDev.Modal;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Document(collection = "userporfile")
public class UserProfile {
    @Id
    private String id;
    private String username;
    private String email;
    private String bio;
    private String picture;

    public UserProfile() {
          this.id = UUID.randomUUID().toString(); // Auto-generate UUID
    }

    public UserProfile(String username, String email, String bio, String picture) {
        this.id = UUID.randomUUID().toString(); // Auto-generate UUID
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
