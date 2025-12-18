package com.devcom.OnlyDev.Repository;


import com.devcom.OnlyDev.Modal.UserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserInfo, String> {
    UserInfo findByEmail(String email);
}