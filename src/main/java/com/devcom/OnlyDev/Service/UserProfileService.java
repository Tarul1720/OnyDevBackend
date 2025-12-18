package com.devcom.OnlyDev.Service;

import com.devcom.OnlyDev.DTO.UserProfileRequest;
import com.devcom.OnlyDev.Modal.UserProfile;
import com.devcom.OnlyDev.Repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository repository;

    // Create a new profile
    public UserProfile createProfile(UserProfileRequest request) {
        // Check if email or username already exists
        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (repository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        UserProfile profile = new UserProfile();
        profile.setUsername(request.getUsername());
        profile.setEmail(request.getEmail());
        profile.setBio(request.getBio());
        profile.setPicture(request.getPicture());

        return repository.save(profile);
    }

    // Get all profiles
    public List<UserProfile> getAllProfiles() {
        return repository.findAll();
    }
    public List<UserProfile> getAllProfiles(List<String> emails) {
        return repository.findByEmailIn(emails);
    }

    // Get profile by ID
    public Optional<UserProfile> getProfileById(String id) {
        return repository.findById(id);
    }

    // Get profile by email
    public Optional<UserProfile> getProfileByEmail(String email) {
        return repository.findByEmail(email);
    }

    // Get profile by username
    public Optional<UserProfile> getProfileByUsername(String username) {
        return repository.findByUsername(username);
    }

    // Update entire profile
    public UserProfile updateProfile(String id, UserProfileRequest request) {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found with id: " + id));

        // Check if new username is already taken by another user
        if (request.getUsername() != null && !request.getUsername().equals(profile.getUsername())) {
            if (repository.existsByUsername(request.getUsername())) {
                throw new RuntimeException("Username already exists");
            }
            profile.setUsername(request.getUsername());
        }

        // Check if new email is already taken by another user
        if (request.getEmail() != null && !request.getEmail().equals(profile.getEmail())) {
            if (repository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            profile.setEmail(request.getEmail());
        }

        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getPicture() != null) {
            profile.setPicture(request.getPicture());
        }

        return repository.save(profile);
    }

    // Update only bio
    public UserProfile updateBio(String id, String bio) {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found with id: " + id));
        profile.setBio(bio);
        return repository.save(profile);
    }

    // Update only picture
    public UserProfile updatePicture(String id, String picture) {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found with id: " + id));
        profile.setPicture(picture);
        return repository.save(profile);
    }

    // Update only username
    public UserProfile updateUsername(String id, String username) {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found with id: " + id));

        if (repository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        profile.setUsername(username);
        return repository.save(profile);
    }

    // Delete profile
    public void deleteProfile(String id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Profile not found with id: " + id);
        }
        repository.deleteById(id);
    }

    // Check if profile exists
    public boolean profileExists(String id) {
        return repository.existsById(id);
    }

    // Search profiles by username pattern (contains)
    public List<UserProfile> searchByUsername(String username) {
        return repository.findAll().stream()
                .filter(profile -> profile.getUsername().toLowerCase()
                        .contains(username.toLowerCase()))
                .toList();
    }
}