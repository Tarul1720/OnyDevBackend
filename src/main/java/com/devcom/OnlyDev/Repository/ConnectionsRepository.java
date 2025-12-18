package com.devcom.OnlyDev.Repository;

import com.devcom.OnlyDev.Modal.ConnectionStatus;
import com.devcom.OnlyDev.Modal.Connections;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionsRepository extends MongoRepository<Connections, String> {

    // Find connection between two users (bidirectional)
    @Query("{ $or: [ { 'from': ?0, 'to': ?1 }, { 'from': ?1, 'to': ?0 } ] }")
    Optional<Connections> findConnectionBetweenUsers(String user1, String user2);

    // Find connection by from and to
    Optional<Connections> findByFromAndTo(String from, String to);

    // Find all connections for a user (both sent and received)
    @Query("{ $or: [ { 'from': ?0 }, { 'to': ?0 } ] }")
    List<Connections> findAllConnectionsByUser(String userId);

    // Find all accepted connections for a user (bidirectional)
    @Query("{ $and: [ { $or: [ { 'from': ?0 }, { 'to': ?0 } ] }, { 'status': 'ACCEPTED' } ] }")
    List<Connections> findAcceptedConnectionsForUser(String userId);

    // Find connections by from and status
    List<Connections> findByFromAndStatus(String from, ConnectionStatus status);

    // Find connections by to and status
    List<Connections> findByToAndStatus(String to, ConnectionStatus status);

    // Find all connections by status for a user (both directions)
    @Query("{ $and: [ { $or: [ { 'from': ?0 }, { 'to': ?0 } ] }, { 'status': ?1 } ] }")
    List<Connections> findByUserAndStatus(String userId, ConnectionStatus status);

    // Find blocked users
    @Query("{ 'from': ?0, 'status': 'BLOCKED' }")
    List<Connections> findBlockedByUser(String userId);

    // Count connections by status
    @Query(value = "{ $and: [ { $or: [ { 'from': ?0 }, { 'to': ?0 } ] }, { 'status': ?1 } ] }", count = true)
    long countConnectionsByUserAndStatus(String userId, ConnectionStatus status);

    // Check if users are connected (accepted status)
    @Query(value = "{ $and: [ { $or: [ { 'from': ?0, 'to': ?1 }, { 'from': ?1, 'to': ?0 } ] }, { 'status': 'ACCEPTED' } ] }", exists = true)
    boolean areUsersConnected(String user1, String user2);

    // Check if connection exists with any status
    @Query(value = "{ $or: [ { 'from': ?0, 'to': ?1 }, { 'from': ?1, 'to': ?0 } ] }", exists = true)
    boolean existsBetweenUsers(String user1, String user2);

    // Delete connection between users
    @Query(value = "{ $or: [ { 'from': ?0, 'to': ?1 }, { 'from': ?1, 'to': ?0 } ] }", delete = true)
    void deleteConnectionBetweenUsers(String user1, String user2);
}