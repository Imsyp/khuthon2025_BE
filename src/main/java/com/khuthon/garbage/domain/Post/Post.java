package com.khuthon.garbage.domain.Post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Posts")
public class Post {
    @Id
    private String postId;
    private String fertId;
    private String sellerId;
    private String title;
    private String description;
    private String imageUrl;
    private Boolean isSold;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Post(String fertId, String sellerId, String title, String description, String imageUrl) {
        this.fertId = fertId;
        this.sellerId = sellerId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isSold = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}