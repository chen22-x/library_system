package com.example.librarysystemproject.domain;

import lombok.Data;

import java.util.Date;

@Data
public class BorrowingBooks {
    private Integer id;

    private Integer userId;

    private Integer bookId;

    private Date date;
}