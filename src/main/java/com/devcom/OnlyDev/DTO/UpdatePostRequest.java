package com.devcom.OnlyDev.DTO;

import com.devcom.OnlyDev.Modal.ContentBlock;
import com.devcom.OnlyDev.Modal.Posts;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Request DTO for updating a post
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostRequest {
    private String title;
    private String description;
    private String content;
    private List<String> images;
    private List<String> videos;
    private List<String> tags;
    private List<String> comments;
    private int likes;
    private List<ContentBlock> contentBlocks;


}
