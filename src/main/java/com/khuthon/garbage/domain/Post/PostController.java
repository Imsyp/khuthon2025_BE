package com.khuthon.garbage.domain.Post;

import com.khuthon.garbage.domain.Post.Dto.PostRequestDto;
import com.khuthon.garbage.domain.User.User;
import com.khuthon.garbage.global.dto.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @PostMapping(value = "/{userId}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Post>> createPost(@ModelAttribute PostRequestDto dto,@PathVariable String userId , HttpSession session) {
        Post post = postService.createPost(dto, userId, session);
        return ResponseEntity.ok(new ApiResponse<>(true, "Post created", post));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Post>>> getAllPosts(@RequestParam(defaultValue = "10") int limit) {
        List<Post> posts = postService.getAllPosts(limit);
        return ResponseEntity.ok(new ApiResponse<>(true, "Posts fetched", posts));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<Post>> getPostById(@PathVariable String postId) {
        return postService.getPostById(postId)
                .map(post -> ResponseEntity.ok(new ApiResponse<>(true, "Post fetched", post)))
                .orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse<>(false, "Post not found", null)));
    }

    @PutMapping(value = "/{postId}", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Post>> updatePost(@PathVariable String postId, @ModelAttribute PostRequestDto dto) {
        return postService.updatePost(postId, dto)
                .map(updated -> ResponseEntity.ok(new ApiResponse<>(true, "Post updated", updated)))
                .orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse<>(false, "Post not found", null)));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable String postId) {
        if (postService.deletePost(postId)) {
            return ResponseEntity.ok(new ApiResponse<>(true, "Post deleted", postId));
        } else {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, "Post not found", null));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<Post>>> getMyPosts(
            HttpSession session,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(401).body(new ApiResponse<>(false, "Unauthorized", null));
        }

        // 기본값: 최근 일주일
        if (from == null) from = LocalDateTime.now().minusWeeks(1);
        if (to == null) to = LocalDateTime.now();

        List<Post> posts = postService.getMyPosts(user.getUserId(), from, to);
        return ResponseEntity.ok(new ApiResponse<>(true, "My posts fetched", posts));
    }

    @GetMapping("/me/{userId}")
    public ResponseEntity<ApiResponse<List<Post>>> getMyPosts(
            HttpSession session,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PathVariable String userId
    ) {
        // 기본값: 최근 일주일
        if (from == null) from = LocalDateTime.now().minusWeeks(1);
        if (to == null) to = LocalDateTime.now();

        List<Post> posts = postService.getMyPosts(userId, from, to);
        return ResponseEntity.ok(new ApiResponse<>(true, "My posts fetched", posts));
    }
}