package com.devcom.OnlyDev.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO for adding comment
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCommentRequest {
    private String content;
    private String userId;
    private String userName;
    private String profilePic;
}
