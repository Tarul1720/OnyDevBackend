package com.devcom.OnlyDev.Service;


import com.devcom.OnlyDev.DTO.UpdatePostRequest;
import com.devcom.OnlyDev.Modal.UserProfile;
import com.devcom.OnlyDev.Repository.PostRepository;
import com.devcom.OnlyDev.Repository.UserProfileRepository;
import com.devcom.OnlyDev.Repository.UserRepository;
import org.springframework.stereotype.Service;

import com.devcom.OnlyDev.DTO.CreatePostRequest;

import com.devcom.OnlyDev.Modal.Comment;
import com.devcom.OnlyDev.Modal.Posts;
import com.devcom.OnlyDev.Repository.CommentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostsService {

    private final PostRepository postsRepository;
    private final CommentRepository commentRepository;
    private final UserProfileRepository userProfileRepository;

    public List<Posts> findAllPosts() {
        return postsRepository.findAll();
    }
    public Page<Posts> findMyAllPosts(List<String> userids, Pageable pageable) {
        return postsRepository.findByUseridIn(userids,pageable);
    }
    // CREATE operations
    public Posts createPost(CreatePostRequest request, String userid) {
        Posts post = new Posts();
        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setContent(request.getContent());
        post.setImages(request.getImages());
        post.setVideos(request.getVideos());
        post.setTags(request.getTags());
        post.setUserid(userid);
        post.setCreatetime(new Date());
        post.setUpdatetime(new Date());
        post.setLikes(new HashSet<>());

        return postsRepository.save(post);
    }

    // READ operations
    public List<Posts> getAllPosts() {
        return postsRepository.findAll()
                .stream()
                .sorted((p1, p2) -> p2.getCreatetime().compareTo(p1.getCreatetime()))
                .collect(Collectors.toList());
    }

    public Page<Posts> getAllPostsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createtime").descending());
        return postsRepository.findAll(pageable);
    }

    public Optional<Posts> getPostById(String id) {
        return postsRepository.findById(id);
    }

    public List<Posts> getPostsByUserId(String userid) {
        return postsRepository.findByUserid(userid);
    }

    public Page<Posts> getPostsByUserIdPaginated(String userid, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createtime").descending());
        return postsRepository.findByUserid(userid, pageable);
    }

    public List<Posts> getPostsByTag(String tag) {
        return postsRepository.findByTagsContaining(tag);
    }

    public List<Posts> searchPostsByTitle(String title) {
        return postsRepository.findByTitleContaining(title);
    }

    public List<Posts> getRecentPosts() {
        return postsRepository.findAllByOrderByCreatetimeDesc();
    }

    public Page<Posts> getRecentPostsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postsRepository.findAllByOrderByCreatetimeDesc(pageable);
    }

    public List<Posts> getPopularPosts() {
        return postsRepository.findAllByOrderByLikesDesc();
    }

    public Page<Posts> getPopularPostsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postsRepository.findAllByOrderByLikesDesc(pageable);
    }

    public long getPostCountByUser(String userid) {
        return postsRepository.countByUserid(userid);
    }

    // UPDATE operations
    public Posts updatePost(String id, UpdatePostRequest request) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            post.setDescription(request.getDescription());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
        }
        if (request.getImages() != null) {
            post.setImages(request.getImages());
        }
        if (request.getVideos() != null) {
            post.setVideos(request.getVideos());
        }
        if (request.getTags() != null) {
            post.setTags(request.getTags());
        }


        post.setUpdatetime(new Date());
        return postsRepository.save(post);
    }
    public Posts updateDeleteSubpost(String id, String subpostid) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        System.out.println(post.getId());

        post.getSubposts().removeIf(subpost -> subpostid.equals(subpost.getId()));
        System.out.println(post.getId());

        post.setUpdatetime(new Date());
        return postsRepository.save(post);
    }

    // Like/Unlike operations
    public Posts likePost(String id,String userid) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        post.getLikes().add(userid);
        return postsRepository.save(post);
    }
    public Page<Posts> alllikedPost(String id,int page,int size) {
        List<String> myLikes = Collections.singletonList(id);
        Pageable pageable = PageRequest.of(page, size);

        return postsRepository.findByLikesIn(myLikes,pageable);
    }

    public Posts increseView(String id,String userid) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        post.getView().add(userid);
        return postsRepository.save(post);
    }

    public Posts unlikePost(String id,String userid) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        if (!post.getLikes().isEmpty()) {
            post.getLikes().remove(userid);
        }
        return postsRepository.save(post);
    }
    public Posts likePost(String id,String subpostid,String userid) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        Posts subpost=post.getSubposts().stream().filter(subpost1 -> subpostid.equals(subpost1.getId())).findFirst().orElse(null);
        post.getSubposts().removeIf(subp -> subpostid.equals(subp.getId()));

        assert subpost != null;
        subpost.getLikes().add(userid);
        subpost.setUpdatetime(new Date());
        post.setUpdatetime(new Date());
        post.getSubposts().add(subpost);
        return postsRepository.save(post);
    }

    public Posts unlikePost(String id,String subpostid,String userid) {

        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        Posts subpost=post.getSubposts().stream().filter(subpost1 -> subpostid.equals(subpost1.getId())).findFirst().orElse(null);
        post.getSubposts().removeIf(subp -> subpostid.equals(subp.getId()));

        assert subpost != null;
        if (!subpost.getLikes().isEmpty()) {
            subpost.getLikes().remove(userid);
        }

        subpost.setUpdatetime(new Date());
        post.setUpdatetime(new Date());
        post.getSubposts().add(subpost);

        return postsRepository.save(post);
    }

    // Media operations
    public Posts addImages(String id, List<String> images) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        post.getImages().addAll(images);
        post.setUpdatetime(new Date());
        return postsRepository.save(post);
    }

    public Posts addVideos(String id, List<String> videos) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        post.getVideos().addAll(videos);
        post.setUpdatetime(new Date());
        return postsRepository.save(post);
    }

    public Posts removeImage(String id, String imageUrl) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        post.getImages().remove(imageUrl);
        post.setUpdatetime(new Date());
        return postsRepository.save(post);
    }

    public Posts removeVideo(String id, String videoUrl) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        post.getVideos().remove(videoUrl);
        post.setUpdatetime(new Date());
        return postsRepository.save(post);
    }

    // Tag operations
    public Posts addTag(String id, String tag) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        if (!post.getTags().contains(tag)) {
            post.getTags().add(tag);
            post.setUpdatetime(new Date());
            return postsRepository.save(post);
        }
        return post;
    }

    public Posts removeTag(String id, String tag) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        post.getTags().remove(tag);
        post.setUpdatetime(new Date());
        return postsRepository.save(post);
    }

    // Comment operations
    public Comment addComment(String postId, String userId, String content) {
        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        UserProfile user=userProfileRepository.findByEmail(userId).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Comment comment = new Comment();
        comment.setProfilePic(user.getPicture());
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setUsername(user.getUsername());
        comment.setContent(content);
        comment.setCreatedAt(new Date());
        comment.setLikes(0);

        Comment savedComment = commentRepository.save(comment);

        post.getComments().add(savedComment.getId());
        post.setUpdatetime(new Date());
        postsRepository.save(post);

        return savedComment;
    }

    public List<Comment> getCommentsByPostId(String postId) {
        return commentRepository.findByPostId(postId);
    }

    public void deleteComment(String postId, String commentId) {
        Posts post = postsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        post.getComments().remove(commentId);
        postsRepository.save(post);
        commentRepository.deleteById(commentId);
    }

    // Subpost operations
    public Posts addSubpost(String parentId, CreatePostRequest request, String userid) {
        Posts parentPost = postsRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent post not found with id: " + parentId));

        Posts subpost = new Posts();

        subpost.setTitle(request.getTitle());
        subpost.setDescription(request.getDescription());
        subpost.setContent(request.getContent());
        subpost.setImages(request.getImages());
        subpost.setVideos(request.getVideos());
        subpost.setTags(request.getTags());
        subpost.setUserid(userid);
        subpost.setContentBlocks(request.getContentBlocks());
        subpost.setCreatetime(new Date());
        subpost.setUpdatetime(new Date());

       // Posts savedSubpost = postsRepository.save(subpost);

        parentPost.getSubposts().add(subpost);
        parentPost.setUpdatetime(new Date());
        postsRepository.save(parentPost);

        return parentPost;
    }
    public Posts updateSubpost(String parentId,String subpostId, CreatePostRequest request, String userid) {
        Posts parentPost = postsRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent post not found with id: " + parentId));


        Posts subpost = parentPost.getSubposts().stream()
                .filter(p -> p.getId() == subpostId)
                .findAny()
                .orElse(new Posts()); // or handle Optional differently
        subpost.setTitle(request.getTitle());
        subpost.setDescription(request.getDescription());
        subpost.setContent(request.getContent());
        subpost.setImages(request.getImages());
        subpost.setVideos(request.getVideos());
        subpost.setTags(request.getTags());
        subpost.setUserid(userid);
        subpost.setContentBlocks(request.getContentBlocks());
        subpost.setCreatetime(new Date());
        subpost.setUpdatetime(new Date());

        // Posts savedSubpost = postsRepository.save(subpost);
        parentPost.getSubposts().removeIf(subpo -> subpostId.equals(subpo.getId()));
        parentPost.getSubposts().add(subpost);
        parentPost.setUpdatetime(new Date());
        postsRepository.save(parentPost);

        return parentPost;
    }
    public List<Posts> getSubposts(String parentId) {
        Posts post = postsRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + parentId));
        return post.getSubposts();
    }


    // DELETE operations
    public void deletePost(String id) {
        Posts post = postsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        // Delete all comments associated with this post
        commentRepository.deleteByPostId(id);

        // Delete all subposts
        for (Posts subpost : post.getSubposts()) {
            deletePost(subpost.getId());
        }

        postsRepository.deleteById(id);
    }

    // Statistics
    public long getTotalPosts() {
        return postsRepository.count();
    }

    public long getTotalLikes() {
        return postsRepository.findAll().stream()
                .mapToLong(posts-> posts.getLikes().size())
                .sum();
    }

}
