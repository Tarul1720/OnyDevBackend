package com.devcom.OnlyDev.DTO;


import com.devcom.OnlyDev.Modal.ConnectionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

// Request to send connection
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendConnectionRequest {
    private String to; // User ID to connect with
}

// Response for connection
@Data
@AllArgsConstructor
@NoArgsConstructor
class ConnectionResponse {
    private String id;
    private String from;
    private String fromUsername;
    private String to;
    private String toUsername;
    private ConnectionStatus status;
    private Date requestedAt;
    private Date respondedAt;
}

// Connection statistics
@Data
@AllArgsConstructor
@NoArgsConstructor
class ConnectionStats {
    private long totalConnections;
    private long pendingRequests;
    private long sentRequests;
    private long acceptedConnections;
    private long rejectedConnections;
    private long blockedUsers;
}

// User connection info
@Data
@AllArgsConstructor
@NoArgsConstructor
class UserConnectionInfo {
    private String userId;
    private String username;
    private String email;
    private ConnectionStatus connectionStatus;
    private boolean isConnected;
    private Date connectedSince;
}

// Mutual connections
@Data
@AllArgsConstructor
@NoArgsConstructor
class MutualConnectionsResponse {
    private String userId;
    private List<String> mutualConnectionIds;
    private int count;
}