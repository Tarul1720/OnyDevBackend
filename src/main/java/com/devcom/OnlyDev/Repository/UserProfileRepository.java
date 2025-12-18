package com.devcom.OnlyDev.Repository;


import com.devcom.OnlyDev.Modal.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    Optional<UserProfile> findByEmail(String email);
    List<UserProfile> findByEmailIn(List<String> emails);
    Optional<UserProfile> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
