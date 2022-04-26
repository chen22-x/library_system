package com.example.librarysystemproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.librarysystemproject.domain.BookCategory;
import com.example.librarysystemproject.mapper.BookCategoryMapper;
import com.example.librarysystemproject.service.BookCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookCategoryServiceImpl extends ServiceImpl<BookCategoryMapper, BookCategory> implements BookCategoryService {
    @Autowired
    private BookCategoryMapper bookCategoryMapper;

    /**
     * 删除图书分类
     * @param bookCategoryId
     * @return
     */
    @Override
    public int deleteBookCategory(int bookCategoryId) {
        LambdaQueryWrapper<BookCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookCategory::getCategoryId,bookCategoryId);
        return bookCategoryMapper.delete(queryWrapper);
    }
}
