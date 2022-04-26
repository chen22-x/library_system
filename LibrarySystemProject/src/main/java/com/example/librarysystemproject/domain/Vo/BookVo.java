package com.example.librarysystemproject.domain.Vo;

import com.example.librarysystemproject.domain.Book;
import lombok.Data;

@Data
public class BookVo extends Book {
//    private Integer bookId;  //书籍id
//
//    private String bookName; //书名
//
//    private String bookAuthor;//作者
//
//    private String bookPublish;//出版社

    private String isExist;  //是否可借

}
