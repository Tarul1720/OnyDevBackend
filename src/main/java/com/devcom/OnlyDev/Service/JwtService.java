package com.devcom.OnlyDev.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
public class JwtService {

    public static final String SECRET = "5367566859703373367639792F423F452848284D6251655468576D5A71347437";
    private final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();
    public void blacklistToken(String token) {
        tokenBlacklist.add(token);

        // Optional: Schedule cleanup of expired tokens from blacklist
        // to prevent memory leak
        scheduleTokenCleanup(token);
    }
    /**
     * Remove expired token from blacklist
     */
    private void scheduleTokenCleanup(String token) {
        try {
            Date expirationDate = extractExpiration(token);
            long delay = expirationDate.getTime() - System.currentTimeMillis();

            if (delay > 0) {
                // Schedule removal after token expires
                new Thread(() -> {
                    try {
                        Thread.sleep(delay);
                        tokenBlacklist.remove(token);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
        } catch (Exception e) {
            // If extraction fails, remove immediately
            tokenBlacklist.remove(token);
        }
    }
    public void clearBlacklist() {
        tokenBlacklist.clear();
    }
    public int getBlacklistSize() {
        return tokenBlacklist.size();
    }
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }

    public String generateToken(String email) { // Use email as username
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        if (isTokenBlacklisted(token)) {
            return false;
        }

        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }
}