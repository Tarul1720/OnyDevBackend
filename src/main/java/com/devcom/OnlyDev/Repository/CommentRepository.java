package com.devcom.OnlyDev.Repository;



import com.devcom.OnlyDev.Modal.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPostId(String postId);
    List<Comment> findByUserId(String userId);
    long countByPostId(String postId);
    void deleteByPostId(String postId);
}