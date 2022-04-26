package com.example.librarysystemproject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.librarysystemproject.domain.User;

import javax.servlet.http.HttpServletRequest;

public interface UserService extends IService<User> {
    boolean insert(User user);
    boolean updateUser(User user, HttpServletRequest request);
    boolean userBorrowingBook(int bookId,HttpServletRequest request);
    boolean userReturnBook(int bookId,HttpServletRequest request);
}
