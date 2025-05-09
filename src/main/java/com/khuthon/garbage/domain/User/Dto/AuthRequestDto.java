package com.khuthon.garbage.domain.User.Dto;


import lombok.Getter;

@Getter
public class AuthRequestDto {
    private String username;
    private String password;
}