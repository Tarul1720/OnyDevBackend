package com.devcom.OnlyDev.DTO;

import com.devcom.OnlyDev.Modal.ContentBlock;
import com.devcom.OnlyDev.Modal.Posts;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

// Request DTO for creating a post
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostRequest {
    private String title;
    private String description;
    private String content;
    private List<String> images = new ArrayList<>();
    private List<String> videos = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
    private List<ContentBlock> contentBlocks;
}

// Response DTO for post
@Data
@AllArgsConstructor
@NoArgsConstructor
class PostResponse {
    private String id;
    private String title;
    private String description;
    private List<String> images;
    private List<String> videos;
    private List<String> commentIds;
    private List<ContentBlockDTO> contentBlocks;
    private int likes;
    private String userid;
    private String username;
    private List<String> tags;
    private String content;
    private Date createtime;
    private Date updatetime;
    private int subpostCount;
}
@Data
@NoArgsConstructor
@AllArgsConstructor
class ContentBlockDTO {
    private Long id;
    private String type;
    private String content;
    private Integer order;
    private Map<String, Object> styles;
    private Map<String, Object> gridConfig;
}

// DTO for adding media
@Data
@AllArgsConstructor
@NoArgsConstructor
class AddMediaRequest {
    private List<String> images;
    private List<String> videos;
}

// DTO for adding subpost
@Data
@AllArgsConstructor
@NoArgsConstructor
class AddSubpostRequest {
    private String title;
    private String description;
    private String content;
    private List<String> images;
    private List<String> videos;
    private List<String> tags;
}

// DTO for pagination
@Data
@AllArgsConstructor
@NoArgsConstructor
class PostPageResponse {
    private List<Posts> posts;
    private int currentPage;
    private long totalItems;
    private int totalPages;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class UpdateContentBlocksRequest {
    private List<ContentBlockDTO> contentBlocks;
}
