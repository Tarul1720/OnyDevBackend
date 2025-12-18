package com.devcom.OnlyDev.Modal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "posts")
@Data
@AllArgsConstructor
public class Posts {
    @Id
    private String id;
    private String title;
    private String description;
    private List<String> images = new ArrayList<>();
    private List<String> videos = new ArrayList<>();
    private List<String> comments = new ArrayList<>();
    private HashSet<String> likes = new HashSet<>();
    private String userid;
    private List<String> tags = new ArrayList<>();
    private String content;
    private HashSet<String> view=new HashSet<>();
    private Date createtime;
    private Date updatetime;
    private List<ContentBlock> contentBlocks = new ArrayList<>();
    private List<Posts> subposts = new ArrayList<>();
    public Posts() {
        this.id = String.valueOf(UUID.randomUUID());
    }
}