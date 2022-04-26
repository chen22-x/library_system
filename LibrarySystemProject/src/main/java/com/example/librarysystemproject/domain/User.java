package com.example.librarysystemproject.domain;

import lombok.Data;

@Data
public class User {
    private Integer userId;

    private String userName;

    private String userPwd;

    private String userEmail;

}