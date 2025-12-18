package com.devcom.OnlyDev.Modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    private String id;
    private String postId;
    private String userId;
    private String username;
    private String profilePic;
    private String content;
    private Date createdAt;
    private int likes = 0;
}