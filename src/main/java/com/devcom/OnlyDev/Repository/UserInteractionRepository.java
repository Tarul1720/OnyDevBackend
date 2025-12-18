package com.devcom.OnlyDev.Repository;

import com.devcom.OnlyDev.Modal.UserInteractions;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserInteractionRepository  extends MongoRepository<UserInteractions, String> {
    Optional<UserInteractions> findByUserId(String userId);
    // Optional<UserInteractions> findByUserId(String userId);
}
