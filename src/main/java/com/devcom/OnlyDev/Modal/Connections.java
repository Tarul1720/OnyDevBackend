package com.devcom.OnlyDev.Modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "connections")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Connections {
    @Id
    private String id;
    private String from;  // User who sent the connection request
    private String to;    // User who received the connection request
    private ConnectionStatus status; // PENDING, ACCEPTED, REJECTED, BLOCKED
    private Date requestedAt;
    private Date respondedAt;
}

