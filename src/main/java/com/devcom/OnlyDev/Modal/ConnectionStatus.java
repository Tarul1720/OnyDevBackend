package com.devcom.OnlyDev.Modal;

// Enum for connection status
public enum ConnectionStatus {
    PENDING,    // Connection request sent, waiting for response
    ACCEPTED,   // Connection request accepted
    REJECTED,   // Connection request rejected
    BLOCKED     // User blocked
}
