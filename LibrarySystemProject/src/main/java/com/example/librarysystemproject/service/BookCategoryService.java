package com.example.librarysystemproject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.librarysystemproject.domain.BookCategory;


public interface BookCategoryService extends IService<BookCategory> {
    int deleteBookCategory(int bookCategoryId);
}
