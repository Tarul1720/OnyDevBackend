package com.devcom.OnlyDev.Service;

import com.devcom.OnlyDev.Modal.UserInfo;
import com.devcom.OnlyDev.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {
    @Autowired
    UserRepository repository;
    @Autowired
    PasswordEncoder encoder;

    // Method to load user details by username (email)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserInfo user = repository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        return User.withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole())  // Must be a string like "ROLE_USER"
                .build();
    }

    // Add any additional methods for registering or managing users
    public String addUser(UserInfo userInfo) {
        // Encrypt password before saving
        UserInfo user = repository.findByEmail(userInfo.getEmail());
        try {
            if (user == null) {
            userInfo.setPassword(encoder.encode(userInfo.getPassword()));
            repository.save(userInfo);
            return "User added successfully!";
            }else{
                return "User already exists!";
            }
        }
        catch (Exception ex) {
            System.err.println("Error adding user: " + ex.getMessage());
            return "User Password error exists! "+ex.getMessage();
        }
    }
}