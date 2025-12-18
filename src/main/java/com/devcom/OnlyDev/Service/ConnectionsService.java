package com.devcom.OnlyDev.Service;


import com.devcom.OnlyDev.Modal.ConnectionStatus;
import com.devcom.OnlyDev.Modal.Connections;
import com.devcom.OnlyDev.Repository.ConnectionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConnectionsService {

    private final ConnectionsRepository connectionsRepository;

    public Connections addConnectionRequest(String from, String to) {
        // Prevent self-connection
        if (from.equals(to)) {
            throw new RuntimeException("Cannot send connection request to yourself");
        }

        // Check if connection already exists
        Optional<Connections> existing = connectionsRepository.findConnectionBetweenUsers(from, to);
        if (existing.isPresent()) {
            Connections conn = existing.get();
            if (conn.getStatus() == ConnectionStatus.PENDING) {
                throw new RuntimeException("Connection request already pending");
            } else if (conn.getStatus() == ConnectionStatus.ACCEPTED) {
                throw new RuntimeException("Already connected with this user");
            } else if (conn.getStatus() == ConnectionStatus.BLOCKED) {
                throw new RuntimeException("Cannot send connection request to blocked user");
            }
        }

        Connections connection = new Connections();
        connection.setFrom(from);
        connection.setTo(to);
        connection.setStatus(ConnectionStatus.ACCEPTED);
        connection.setRequestedAt(new Date());

        return connectionsRepository.save(connection);
    }
    public Connections removeConnectionRequest(String from, String to) {
        // Prevent self-connection
        if (from.equals(to)) {
            throw new RuntimeException("Cannot send connection request to yourself");
        }

        // Check if connection already exists
        Optional<Connections> existing = connectionsRepository.findConnectionBetweenUsers(from, to);
        if (existing.isPresent()) {
            Connections conn = existing.get();
            if (conn.getStatus() == ConnectionStatus.PENDING) {
                throw new RuntimeException("Connection request already pending");
            } else if (conn.getStatus() == ConnectionStatus.ACCEPTED) {

                conn.setStatus(ConnectionStatus.REJECTED);
                conn.setRequestedAt(new Date());

                return connectionsRepository.save(conn);
            } else if (conn.getStatus() == ConnectionStatus.BLOCKED) {
                throw new RuntimeException("Cannot send connection request to blocked user");
            }
        }

        throw new RuntimeException("No connections with this user");
    }
    // Send connection request
    public Connections sendConnectionRequest(String from, String to) {
        // Prevent self-connection
        if (from.equals(to)) {
            throw new RuntimeException("Cannot send connection request to yourself");
        }

        // Check if connection already exists
        Optional<Connections> existing = connectionsRepository.findConnectionBetweenUsers(from, to);
        if (existing.isPresent()) {
            Connections conn = existing.get();
            if (conn.getStatus() == ConnectionStatus.PENDING) {
                throw new RuntimeException("Connection request already pending");
            } else if (conn.getStatus() == ConnectionStatus.ACCEPTED) {
                throw new RuntimeException("Already connected with this user");
            } else if (conn.getStatus() == ConnectionStatus.BLOCKED) {
                throw new RuntimeException("Cannot send connection request to blocked user");
            }
        }

        Connections connection = new Connections();
        connection.setFrom(from);
        connection.setTo(to);
        connection.setStatus(ConnectionStatus.PENDING);
        connection.setRequestedAt(new Date());

        return connectionsRepository.save(connection);
    }

    // Accept connection request
    public Connections acceptConnectionRequest(String connectionId, String currentUserId) {
        Connections connection = connectionsRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection request not found"));

        // Only the receiver can accept
        if (!connection.getTo().equals(currentUserId)) {
            throw new RuntimeException("You are not authorized to accept this connection");
        }

        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new RuntimeException("Connection request is not in pending state");
        }

        connection.setStatus(ConnectionStatus.ACCEPTED);
        connection.setRespondedAt(new Date());

        return connectionsRepository.save(connection);
    }

    // Reject connection request
    public Connections rejectConnectionRequest(String connectionId, String currentUserId) {
        Connections connection = connectionsRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection request not found"));

        // Only the receiver can reject
        if (!connection.getTo().equals(currentUserId)) {
            throw new RuntimeException("You are not authorized to reject this connection");
        }

        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new RuntimeException("Connection request is not in pending state");
        }

        connection.setStatus(ConnectionStatus.REJECTED);
        connection.setRespondedAt(new Date());

        return connectionsRepository.save(connection);
    }

    // Block user
    public Connections blockUser(String from, String to) {
        if (from.equals(to)) {
            throw new RuntimeException("Cannot block yourself");
        }

        Optional<Connections> existing = connectionsRepository.findConnectionBetweenUsers(from, to);

        Connections connection;
        if (existing.isPresent()) {
            connection = existing.get();
            connection.setStatus(ConnectionStatus.BLOCKED);
            connection.setRespondedAt(new Date());
        } else {
            connection = new Connections();
            connection.setFrom(from);
            connection.setTo(to);
            connection.setStatus(ConnectionStatus.BLOCKED);
            connection.setRequestedAt(new Date());
            connection.setRespondedAt(new Date());
        }

        return connectionsRepository.save(connection);
    }

    // Unblock user
    public void unblockUser(String from, String to) {
        Optional<Connections> connection = connectionsRepository.findConnectionBetweenUsers(from, to);

        if (connection.isPresent() && connection.get().getStatus() == ConnectionStatus.BLOCKED) {
            connectionsRepository.delete(connection.get());
        } else {
            throw new RuntimeException("No blocked connection found");
        }
    }

    // Remove connection (unfriend)
    public void removeConnection(String user1, String user2) {
        Optional<Connections> connection = connectionsRepository.findConnectionBetweenUsers(user1, user2);

        if (connection.isPresent()) {
            connectionsRepository.delete(connection.get());
        } else {
            throw new RuntimeException("Connection not found");
        }
    }

    // Cancel sent connection request
    public void cancelConnectionRequest(String connectionId, String currentUserId) {
        Connections connection = connectionsRepository.findById(connectionId)
                .orElseThrow(() -> new RuntimeException("Connection request not found"));

        // Only sender can cancel
        if (!connection.getFrom().equals(currentUserId)) {
            throw new RuntimeException("You are not authorized to cancel this request");
        }

        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new RuntimeException("Can only cancel pending requests");
        }

        connectionsRepository.delete(connection);
    }

    // Get all connections for a user (accepted only)
    public List<Connections> getConnections(String userId) {
        return connectionsRepository.findAcceptedConnectionsForUser(userId);
    }

    // Get all connection IDs (user IDs of connected users)
    public List<String> getConnectionUserIds(String userId) {
        List<Connections> connections = connectionsRepository.findAcceptedConnectionsForUser(userId);

        return connections.stream()
                .map(conn -> conn.getFrom().equals(userId) ? conn.getTo() : conn.getFrom())
                .collect(Collectors.toList());
    }

    // Get pending requests received
    public List<Connections> getPendingRequestsReceived(String userId) {
        return connectionsRepository.findByToAndStatus(userId, ConnectionStatus.PENDING);
    }

    // Get pending requests sent
    public List<Connections> getPendingRequestsSent(String userId) {
        return connectionsRepository.findByFromAndStatus(userId, ConnectionStatus.PENDING);
    }

    // Get blocked users
    public List<Connections> getBlockedUsers(String userId) {
        return connectionsRepository.findByFromAndStatus(userId, ConnectionStatus.BLOCKED);
    }

    // Check if users are connected
    public boolean areUsersConnected(String user1, String user2) {
        return connectionsRepository.areUsersConnected(user1, user2);
    }

    // Get connection status between two users
    public String getConnectionStatus(String user1, String user2) {
        Optional<Connections> connection = connectionsRepository.findConnectionBetweenUsers(user1, user2);

        if (connection.isEmpty()) {
            return "NOT_CONNECTED";
        }

        Connections conn = connection.get();

        // If current user is 'from', they sent the request
        if (conn.getFrom().equals(user1)) {
            return conn.getStatus().toString() + "_SENT";
        } else {
            return conn.getStatus().toString() + "_RECEIVED";
        }
    }

    // Get mutual connections between two users
    public List<String> getMutualConnections(String user1, String user2) {
        List<String> user1Connections = getConnectionUserIds(user1);
        List<String> user2Connections = getConnectionUserIds(user2);

        return user1Connections.stream()
                .filter(user2Connections::contains)
                .collect(Collectors.toList());
    }

    // Get connection statistics
    public ConnectionStats getConnectionStats(String userId) {
        long accepted = connectionsRepository.countConnectionsByUserAndStatus(userId, ConnectionStatus.ACCEPTED);
        long pending = connectionsRepository.findByToAndStatus(userId, ConnectionStatus.PENDING).size();
        long sent = connectionsRepository.findByFromAndStatus(userId, ConnectionStatus.PENDING).size();
        long rejected = connectionsRepository.countConnectionsByUserAndStatus(userId, ConnectionStatus.REJECTED);
        long blocked = connectionsRepository.findByFromAndStatus(userId, ConnectionStatus.BLOCKED).size();

        return new ConnectionStats(accepted, pending, sent, accepted, rejected, blocked);
    }

    // Get connection by ID
    public Optional<Connections> getConnectionById(String connectionId) {
        return connectionsRepository.findById(connectionId);
    }

    // Get all connections (for admin)
    public List<Connections> getAllConnections() {
        return connectionsRepository.findAll();
    }

    // Connection suggestions (users not connected)
    public List<String> getSuggestedConnections(String userId, List<String> allUserIds) {
        List<String> connectedUsers = getConnectionUserIds(userId);
        List<Connections> pendingRequests = getPendingRequestsSent(userId);
        List<String> pendingUserIds = pendingRequests.stream()
                .map(Connections::getTo)
                .collect(Collectors.toList());

        return allUserIds.stream()
                .filter(id -> !id.equals(userId))
                .filter(id -> !connectedUsers.contains(id))
                .filter(id -> !pendingUserIds.contains(id))
                .collect(Collectors.toList());
    }
}

// Stats class (should be in DTO package)
class ConnectionStats {
    private long totalConnections;
    private long pendingRequests;
    private long sentRequests;
    private long acceptedConnections;
    private long rejectedConnections;
    private long blockedUsers;

    public ConnectionStats(long totalConnections, long pendingRequests, long sentRequests,
                           long acceptedConnections, long rejectedConnections, long blockedUsers) {
        this.totalConnections = totalConnections;
        this.pendingRequests = pendingRequests;
        this.sentRequests = sentRequests;
        this.acceptedConnections = acceptedConnections;
        this.rejectedConnections = rejectedConnections;
        this.blockedUsers = blockedUsers;
    }

    // Getters and setters
    public long getTotalConnections() { return totalConnections; }
    public void setTotalConnections(long totalConnections) { this.totalConnections = totalConnections; }
    public long getPendingRequests() { return pendingRequests; }
    public void setPendingRequests(long pendingRequests) { this.pendingRequests = pendingRequests; }
    public long getSentRequests() { return sentRequests; }
    public void setSentRequests(long sentRequests) { this.sentRequests = sentRequests; }
    public long getAcceptedConnections() { return acceptedConnections; }
    public void setAcceptedConnections(long acceptedConnections) { this.acceptedConnections = acceptedConnections; }
    public long getRejectedConnections() { return rejectedConnections; }
    public void setRejectedConnections(long rejectedConnections) { this.rejectedConnections = rejectedConnections; }
    public long getBlockedUsers() { return blockedUsers; }
    public void setBlockedUsers(long blockedUsers) { this.blockedUsers = blockedUsers; }
}