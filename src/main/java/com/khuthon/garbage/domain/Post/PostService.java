package com.khuthon.garbage.domain.Post;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.khuthon.garbage.domain.Post.Dto.PostRequestDto;
import com.khuthon.garbage.domain.User.User;
import com.khuthon.garbage.domain.User.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final Storage storage;
    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    public Post createPost(PostRequestDto dto, String userId, HttpSession session) {

        // 이미지 업로드 처리
        String imageUrl = null;
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            String contentType = dto.getImage().getContentType();

            try {
                storage.create(
                        BlobInfo.newBuilder(bucketName, uuid)
                                .setContentType(contentType)
                                .build(),
                        dto.getImage().getInputStream()
                );
                imageUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, uuid);
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 실패", e);
            }
        }

        User user = userRepository.findById(userId).get();

        // Post 저장
        Post post = new Post(dto.getFertId(), user.getUserId(), dto.getTitle(), dto.getDescription(), imageUrl);
        Post savedPost = postRepository.save(post);

        // User 객체의 postIds에 postId 추가
        List<String> postIds = user.getPostIds() != null ?
                new ArrayList<>(Arrays.asList(user.getPostIds())) : new ArrayList<>();
        postIds.add(savedPost.getPostId());
        user.setPostIds(postIds.toArray(new String[0]));

        userRepository.save(user); // 변경사항 반영

        return savedPost;
    }

    public List<Post> getAllPosts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return postRepository.findAllByOrderByCreatedAtDesc(pageable).getContent();
    }

    public Optional<Post> getPostById(String postId) {
        return postRepository.findById(postId);
    }

    public Optional<Post> updatePost(String postId, PostRequestDto dto) {
        return postRepository.findById(postId).map(post -> {
            post.setTitle(dto.getTitle());
            post.setDescription(dto.getDescription());

            // 이미지가 새로 업로드된 경우
            if (dto.getImage() != null && !dto.getImage().isEmpty()) {
                String uuid = UUID.randomUUID().toString();
                String contentType = dto.getImage().getContentType();

                try {
                    storage.create(
                            BlobInfo.newBuilder(bucketName, uuid)
                                    .setContentType(contentType)
                                    .build(),
                            dto.getImage().getInputStream()
                    );
                    String imageUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, uuid);
                    post.setImageUrl(imageUrl);
                } catch (IOException e) {
                    throw new RuntimeException("Image upload failed during update", e);
                }
            }

            post.setUpdatedAt(LocalDateTime.now());
            return postRepository.save(post);
        });
    }

    public boolean deletePost(String postId) {
        if (postRepository.existsById(postId)) {
            postRepository.deleteById(postId);
            return true;
        }
        return false;
    }

    public List<Post> getMyPosts(String userId, LocalDateTime from, LocalDateTime to) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPostIds() == null || user.getPostIds().length == 0) {
            return Collections.emptyList();
        }

        List<Post> posts = postRepository.findAllById(List.of(user.getPostIds()));

        return posts.stream()
                .filter(post -> !post.getCreatedAt().isBefore(from) && !post.getCreatedAt().isAfter(to))
                .collect(Collectors.toList());
    }
}