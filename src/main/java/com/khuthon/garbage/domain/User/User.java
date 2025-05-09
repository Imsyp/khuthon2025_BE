package com.khuthon.garbage.domain.User;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "Users")
public class User {
    @Id
    private String userId;
    private String password;
    private String username;

    private Integer trustScore;
    private Integer point;

    private String[] postIds;
    private String[] transactionIds;

    private LocalDateTime createdAt;

    public User() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String username) {
        this.username = username;
        this.trustScore = 0;
        this.point = 0;
        this.createdAt = LocalDateTime.now();
    }
}