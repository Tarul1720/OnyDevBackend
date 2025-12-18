package com.devcom.OnlyDev.Controller;


import com.devcom.OnlyDev.DTO.SendConnectionRequest;
import com.devcom.OnlyDev.Modal.Connections;
import com.devcom.OnlyDev.Service.ConnectionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/connections")
@RequiredArgsConstructor
public class ConnectionsController {

    private final ConnectionsService connectionsService;

    // Helper method to get current user ID
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    // ==================== SEND & MANAGE REQUESTS ====================

    @PostMapping("/send")
    public ResponseEntity<?> sendConnectionRequest(@RequestBody SendConnectionRequest request) {
        try {
            String from = getCurrentUserId();
            Connections connection = connectionsService.sendConnectionRequest(from, request.getTo());
            return ResponseEntity.status(HttpStatus.CREATED).body(connection);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/add")
    public ResponseEntity<?> addConnectionRequest(@RequestBody SendConnectionRequest request) {
        try {
            String from = getCurrentUserId();
            Connections connection = connectionsService.addConnectionRequest(from, request.getTo());
            return ResponseEntity.status(HttpStatus.CREATED).body(connection);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/remove")
    public ResponseEntity<?> removeConnectionRequest(@RequestBody SendConnectionRequest request) {
        try {
            String from = getCurrentUserId();
            Connections connection = connectionsService.removeConnectionRequest(from, request.getTo());
            return ResponseEntity.status(HttpStatus.CREATED).body(connection);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{connectionId}/accept")
    public ResponseEntity<?> acceptConnectionRequest(@PathVariable String connectionId) {
        try {
            String currentUserId = getCurrentUserId();
            Connections connection = connectionsService.acceptConnectionRequest(connectionId, currentUserId);
            return ResponseEntity.ok(connection);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{connectionId}/reject")
    public ResponseEntity<?> rejectConnectionRequest(@PathVariable String connectionId) {
        try {
            String currentUserId = getCurrentUserId();
            Connections connection = connectionsService.rejectConnectionRequest(connectionId, currentUserId);
            return ResponseEntity.ok(connection);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{connectionId}/cancel")
    public ResponseEntity<?> cancelConnectionRequest(@PathVariable String connectionId) {
        try {
            String currentUserId = getCurrentUserId();
            connectionsService.cancelConnectionRequest(connectionId, currentUserId);
            return ResponseEntity.ok(Map.of("message", "Connection request cancelled"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== BLOCK & UNBLOCK ====================

    @PostMapping("/block/{userId}")
    public ResponseEntity<?> blockUser(@PathVariable String userId) {
        try {
            String from = getCurrentUserId();
            Connections connection = connectionsService.blockUser(from, userId);
            return ResponseEntity.ok(connection);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/unblock/{userId}")
    public ResponseEntity<?> unblockUser(@PathVariable String userId) {
        try {
            String from = getCurrentUserId();
            connectionsService.unblockUser(from, userId);
            return ResponseEntity.ok(Map.of("message", "User unblocked"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== REMOVE CONNECTION ====================

    @DeleteMapping("/remove/{userId}")
    public ResponseEntity<?> removeConnection(@PathVariable String userId) {
        try {
            String currentUserId = getCurrentUserId();
            connectionsService.removeConnection(currentUserId, userId);
            return ResponseEntity.ok(Map.of("message", "Connection removed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== GET CONNECTIONS ====================

    @GetMapping("/my-connections")
    public ResponseEntity<List<Connections>> getMyConnections() {
        String userId = getCurrentUserId();
        List<Connections> connections = connectionsService.getConnections(userId);
        return ResponseEntity.ok(connections);
    }

    @GetMapping("/my-connections/ids")
    public ResponseEntity<List<String>> getMyConnectionIds() {
        String userId = getCurrentUserId();
        List<String> connectionIds = connectionsService.getConnectionUserIds(userId);
        return ResponseEntity.ok(connectionIds);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Connections>> getUserConnections(@PathVariable String userId) {
        List<Connections> connections = connectionsService.getConnections(userId);
        return ResponseEntity.ok(connections);
    }

    // ==================== PENDING REQUESTS ====================

    @GetMapping("/requests/received")
    public ResponseEntity<List<Connections>> getPendingRequestsReceived() {
        String userId = getCurrentUserId();
        List<Connections> requests = connectionsService.getPendingRequestsReceived(userId);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/requests/sent")
    public ResponseEntity<List<Connections>> getPendingRequestsSent() {
        String userId = getCurrentUserId();
        List<Connections> requests = connectionsService.getPendingRequestsSent(userId);
        return ResponseEntity.ok(requests);
    }

    // ==================== BLOCKED USERS ====================

    @GetMapping("/blocked")
    public ResponseEntity<List<Connections>> getBlockedUsers() {
        String userId = getCurrentUserId();
        List<Connections> blocked = connectionsService.getBlockedUsers(userId);
        return ResponseEntity.ok(blocked);
    }

    // ==================== CHECK CONNECTION STATUS ====================

    @GetMapping("/check/{userId}")
    public ResponseEntity<Map<String, Boolean>> checkConnection(@PathVariable String userId) {
        String currentUserId = getCurrentUserId();
        boolean isConnected = connectionsService.areUsersConnected(currentUserId, userId);
        return ResponseEntity.ok(Map.of("isConnected", isConnected));
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<Map<String, String>> getConnectionStatus(@PathVariable String userId) {
        String currentUserId = getCurrentUserId();
        String status = connectionsService.getConnectionStatus(currentUserId, userId);
        return ResponseEntity.ok(Map.of("status", status));
    }

    // ==================== MUTUAL CONNECTIONS ====================

    @GetMapping("/mutual/{userId}")
    public ResponseEntity<Map<String, Object>> getMutualConnections(@PathVariable String userId) {
        String currentUserId = getCurrentUserId();
        List<String> mutualConnections = connectionsService.getMutualConnections(currentUserId, userId);
        return ResponseEntity.ok(Map.of(
                "mutualConnections", mutualConnections,
                "count", mutualConnections.size()
        ));
    }

    // ==================== STATISTICS ====================

    @GetMapping("/stats")
    public ResponseEntity<?> getConnectionStats() {
        String userId = getCurrentUserId();
        Object stats = connectionsService.getConnectionStats(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/{userId}")
    public ResponseEntity<?> getUserConnectionStats(@PathVariable String userId) {
        Object stats = connectionsService.getConnectionStats(userId);
        return ResponseEntity.ok(stats);
    }

    // ==================== SUGGESTIONS ====================

    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getConnectionSuggestions(@RequestParam List<String> allUserIds) {
        String userId = getCurrentUserId();
        List<String> suggestions = connectionsService.getSuggestedConnections(userId, allUserIds);
        return ResponseEntity.ok(suggestions);
    }

    // ==================== ADMIN ====================

    @GetMapping("/all")
    public ResponseEntity<List<Connections>> getAllConnections() {
        List<Connections> connections = connectionsService.getAllConnections();
        return ResponseEntity.ok(connections);
    }

    @GetMapping("/{connectionId}")
    public ResponseEntity<?> getConnectionById(@PathVariable String connectionId) {
        return connectionsService.getConnectionById(connectionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}