package com.example.librarysystemproject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.librarysystemproject.domain.Admin;
import com.example.librarysystemproject.domain.Book;
import com.example.librarysystemproject.domain.BookCategory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AdminService extends IService<Admin> {
    boolean updateAdmin(Admin admin, HttpServletRequest request);
    List<BookCategory> getBookCategory();
    boolean addBook(Book book);
    boolean addBookCategory(String categoryName);
}
