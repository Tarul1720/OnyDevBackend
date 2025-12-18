package com.devcom.OnlyDev.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/cache")
@PreAuthorize("hasRole('ADMIN')")
public class CacheController {

    @Autowired
    private CacheManager cacheManager;

    // Clear specific cache
    @DeleteMapping("/clear/{cacheName}")
    public ResponseEntity<Map<String, String>> clearCache(@PathVariable String cacheName) {
        Map<String, String> response = new HashMap<>();
        try {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
            response.put("message", "Cache '" + cacheName + "' cleared successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Failed to clear cache: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Clear all caches
    @DeleteMapping("/clear-all")
    public ResponseEntity<Map<String, String>> clearAllCaches() {
        Map<String, String> response = new HashMap<>();
        cacheManager.getCacheNames().forEach(cacheName ->
                Objects.requireNonNull(cacheManager.getCache(cacheName)).clear()
        );
        response.put("message", "All caches cleared successfully");
        return ResponseEntity.ok(response);
    }

    // Get cache statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                stats.put(cacheName, "Active");
            }
        });
        return ResponseEntity.ok(stats);
    }
}