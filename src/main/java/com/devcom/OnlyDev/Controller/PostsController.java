package com.devcom.OnlyDev.Controller;




import com.devcom.OnlyDev.DTO.AddCommentRequest;
import com.devcom.OnlyDev.DTO.CreatePostRequest;
import com.devcom.OnlyDev.DTO.UpdatePostRequest;
import com.devcom.OnlyDev.Modal.Comment;
import com.devcom.OnlyDev.Modal.Posts;
import com.devcom.OnlyDev.Service.PostsService;
import com.devcom.OnlyDev.Service.ShowAllMyFollowPost;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostsController {

    private final PostsService postsService;

    // Helper method to get current user ID
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // Returns email/username
    }

    // ==================== CREATE ====================

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody CreatePostRequest request) {
        try {
            String userid = getCurrentUserId();
            Posts post = postsService.createPost(request, userid);
            return ResponseEntity.status(HttpStatus.CREATED).body(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== READ ====================

    @GetMapping
    public ResponseEntity<List<Posts>> getAllPosts() {
        List<Posts> posts = postsService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<Posts>> getAllPostsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Posts> posts = postsService.getAllPostsPaginated(page, size);
        return ResponseEntity.ok(posts);
    }
    @Autowired
    ShowAllMyFollowPost showAllMyFollowPost;
    @GetMapping("/findMy/paginated")
    public ResponseEntity<Page<Posts>> getAllMyPostsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userid = getCurrentUserId();
        Page<Posts> posts = showAllMyFollowPost.ShowAllMyFollowPostPagenated(userid,page, size);
        return ResponseEntity.ok(posts);
    }
    @GetMapping("/findMyLiked/paginated")
    public ResponseEntity<Page<Posts>> getAllMyPostsLikedPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userid = getCurrentUserId();
        Page<Posts> posts = postsService.alllikedPost(userid,page, size);
        return ResponseEntity.ok(posts);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable String id) {
        return postsService.getPostById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userid}")
    public ResponseEntity<List<Posts>> getPostsByUser(@PathVariable String userid) {
        List<Posts> posts = postsService.getPostsByUserId(userid);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{userid}/paginated")
    public ResponseEntity<Page<Posts>> getPostsByUserPaginated(
            @PathVariable String userid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Posts> posts = postsService.getPostsByUserIdPaginated(userid, page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/my-posts")
    public ResponseEntity<List<Posts>> getMyPosts() {
        String userid = getCurrentUserId();
        List<Posts> posts = postsService.getPostsByUserId(userid);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<Posts>> getPostsByTag(@PathVariable String tag) {
        List<Posts> posts = postsService.getPostsByTag(tag);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Posts>> searchPosts(@RequestParam String title) {
        List<Posts> posts = postsService.searchPostsByTitle(title);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Posts>> getRecentPosts() {
        List<Posts> posts = postsService.getRecentPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/recent/paginated")
    public ResponseEntity<Page<Posts>> getRecentPostsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Posts> posts = postsService.getRecentPostsPaginated(page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Posts>> getPopularPosts() {
        List<Posts> posts = postsService.getPopularPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/popular/paginated")
    public ResponseEntity<Page<Posts>> getPopularPostsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Posts> posts = postsService.getPopularPostsPaginated(page, size);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{userid}/count")
    public ResponseEntity<Map<String, Long>> getPostCountByUser(@PathVariable String userid) {
        long count = postsService.getPostCountByUser(userid);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // ==================== UPDATE ====================

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable String id,
            @RequestBody UpdatePostRequest request) {
        try {
            Posts updated = postsService.updatePost(id, request);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/{id}/view")
    public ResponseEntity<?> updateView(
            @PathVariable String id) {
        try {

            Posts updated = postsService.increseView(id,getCurrentUserId());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PutMapping("/delete-subpost/{id}")
    public ResponseEntity<?> updateDeleteSubPost(
            @PathVariable String id,
            @RequestBody Map <String,String> subpost) {
        try {
            System.out.println(subpost);
            Posts updated = postsService.updateDeleteSubpost(id,subpost.get("subpost"));
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdatePost(
            @PathVariable String id,
            @RequestBody UpdatePostRequest request) {
        try {
            Posts updated = postsService.updatePost(id, request);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== LIKES ====================

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable String id) {
        try {
            String userid = getCurrentUserId();
            Posts post = postsService.likePost(id,userid);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable String id) {
        try {
            String userid = getCurrentUserId();
            Posts post = postsService.unlikePost(id,userid);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/{id}/subpost/{subpostid}/like")
    public ResponseEntity<?> likePost(@PathVariable String id,@PathVariable String subpostid) {
        try {
            String userid = getCurrentUserId();
            Posts post = postsService.likePost(id,subpostid,userid);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/subpost/{subpostid}/unlike")
    public ResponseEntity<?> unlikePost(@PathVariable String id,@PathVariable String subpostid) {
        try {
            String userid = getCurrentUserId();
            Posts post = postsService.unlikePost(id,subpostid,userid);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== MEDIA ====================

    @PostMapping("/{id}/images")
    public ResponseEntity<?> addImages(
            @PathVariable String id,
            @RequestBody Map<String, List<String>> request) {
        try {
            Posts post = postsService.addImages(id, request.get("images"));
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/videos")
    public ResponseEntity<?> addVideos(
            @PathVariable String id,
            @RequestBody Map<String, List<String>> request) {
        try {
            Posts post = postsService.addVideos(id, request.get("videos"));
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/images")
    public ResponseEntity<?> removeImage(
            @PathVariable String id,
            @RequestParam String imageUrl) {
        try {
            Posts post = postsService.removeImage(id, imageUrl);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/videos")
    public ResponseEntity<?> removeVideo(
            @PathVariable String id,
            @RequestParam String videoUrl) {
        try {
            Posts post = postsService.removeVideo(id, videoUrl);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== TAGS ====================

    @PostMapping("/{id}/tags")
    public ResponseEntity<?> addTag(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        try {
            Posts post = postsService.addTag(id, request.get("tag"));
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/tags")
    public ResponseEntity<?> removeTag(
            @PathVariable String id,
            @RequestParam String tag) {
        try {
            Posts post = postsService.removeTag(id, tag);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== COMMENTS ====================

    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable String id,
            @RequestBody AddCommentRequest request) {
        try {
            String userid = getCurrentUserId();

            Comment comment = postsService.addComment(id, userid, request.getContent());
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable String id) {
        List<Comment> comments = postsService.getCommentsByPostId(id);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable String postId,
            @PathVariable String commentId) {
        try {
            postsService.deleteComment(postId, commentId);
            return ResponseEntity.ok(Map.of("message", "Comment deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== SUBPOSTS ====================

    @PostMapping("/{parentId}/subposts")
    public ResponseEntity<?> addSubpost(
            @PathVariable String parentId,
            @RequestBody CreatePostRequest request) {
        try {
            String userid = getCurrentUserId();
            Posts subpost = postsService.addSubpost(parentId, request, userid);
            return ResponseEntity.status(HttpStatus.CREATED).body(subpost);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/{parentId}/updateSubposts/{subpostId}")
    public ResponseEntity<?> updateSubpost(
            @PathVariable String parentId,@PathVariable String subpostId,
            @RequestBody CreatePostRequest request) {
        try {
            String userid = getCurrentUserId();
            System.out.println(request.getContentBlocks());
            Posts subpost = postsService.updateSubpost(parentId,subpostId, request, userid);
            return ResponseEntity.status(HttpStatus.CREATED).body(subpost);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{parentId}/subposts")
    public ResponseEntity<List<Posts>> getSubposts(@PathVariable String parentId) {
        try {
            List<Posts> subposts = postsService.getSubposts(parentId);
            return ResponseEntity.ok(subposts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ==================== DELETE ====================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id) {
        try {
            postsService.deletePost(id);
            return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ==================== STATISTICS ====================

    @GetMapping("/stats/total")
    public ResponseEntity<Map<String, Long>> getTotalPosts() {
        long total = postsService.getTotalPosts();
        return ResponseEntity.ok(Map.of("totalPosts", total));
    }

    @GetMapping("/stats/likes")
    public ResponseEntity<Map<String, Long>> getTotalLikes() {
        long total = postsService.getTotalLikes();
        return ResponseEntity.ok(Map.of("totalLikes", total));
    }

}