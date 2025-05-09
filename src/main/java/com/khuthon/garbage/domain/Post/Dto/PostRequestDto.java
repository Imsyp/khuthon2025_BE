package com.khuthon.garbage.domain.Post.Dto;

import lombok.Getter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostRequestDto {
    private String fertId;
    private String title;
    private String description;
    private MultipartFile image;
}