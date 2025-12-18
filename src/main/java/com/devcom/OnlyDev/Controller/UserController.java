package com.devcom.OnlyDev.Controller;

import com.devcom.OnlyDev.DTO.UserProfileRequest;
import com.devcom.OnlyDev.Modal.AuthRequest;
import com.devcom.OnlyDev.Modal.UserInfo;
import com.devcom.OnlyDev.Modal.UserProfile;
import com.devcom.OnlyDev.Service.JwtService;
import com.devcom.OnlyDev.Service.UserInfoService;
import com.devcom.OnlyDev.Service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserInfoService service;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserProfileService userProfileService;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/addNewUser")
    public String addNewUser(@RequestBody UserInfo userInfo) {
        UserProfileRequest userProfile = new UserProfileRequest(
                userInfo.getName(),
                userInfo.getEmail(),
                "",
                ""
        );
        userProfileService.createProfile(userProfile);
        return service.addUser(userInfo);
    }

    @PostMapping("/generateToken")
    public ResponseEntity<Map<String, String>> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            String accessToken = jwtService.generateToken(authRequest.getUsername());
            String refreshToken = jwtService.generateRefreshToken(authRequest.getUsername());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            tokens.put("tokenType", "Bearer");

            return ResponseEntity.ok(tokens);
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    @PostMapping("/validateToken")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("valid", false);
                response.put("message", "Invalid authorization header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            UserDetails userDetails = service.loadUserByUsername(username);
            boolean isValid = jwtService.validateToken(token, userDetails);

            if (isValid) {
                response.put("valid", true);
                response.put("username", username);
                response.put("message", "Token is valid");
                return ResponseEntity.ok(response);
            } else {
                response.put("valid", false);
                response.put("message", "Token is invalid or expired");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("valid", false);
            response.put("message", "Token validation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();

        try {
            String refreshToken = request.get("refreshToken");

            if (refreshToken == null || refreshToken.isEmpty()) {
                response.put("error", "Refresh token is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            String username = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = service.loadUserByUsername(username);

            if (jwtService.validateToken(refreshToken, userDetails)) {
                String newAccessToken = jwtService.generateToken(username);
                String newRefreshToken = jwtService.generateRefreshToken(username);

                response.put("accessToken", newAccessToken);
                response.put("refreshToken", newRefreshToken);
                response.put("tokenType", "Bearer");

                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Invalid or expired refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("error", "Token refresh failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/userInfo")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();

        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = service.loadUserByUsername(username);

            response.put("username", username);
            response.put("roles", userDetails.getAuthorities());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Failed to retrieve user info: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authHeader) {
        Map<String, String> response = new HashMap<>();

        try {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);

            // Blacklist the token
            jwtService.blacklistToken(token);

            response.put("message", "Logout successful");
            response.put("username", username);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Logout failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}