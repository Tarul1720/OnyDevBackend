package com.devcom.OnlyDev.Service;

import com.devcom.OnlyDev.Modal.Connections;
import com.devcom.OnlyDev.Modal.Posts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShowAllMyFollowPost {
    @Autowired
    ConnectionsService connectionsService;
    @Autowired
    PostsService postsService;

    public Page<Posts> ShowAllMyFollowPostPagenated(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createtime").descending());

        List<String> userConnected=connectionsService.getConnectionUserIds(userId);
        return postsService.findMyAllPosts(userConnected,pageable);
    }
    public Page<Posts> ShowAllMyLikedPostPagenated(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createtime").descending());

        List<String> userConnected=connectionsService.getConnectionUserIds(userId);
        return postsService.findMyAllPosts(userConnected,pageable);
    }
}
