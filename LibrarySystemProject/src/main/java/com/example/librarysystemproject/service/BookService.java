package com.example.librarysystemproject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.librarysystemproject.domain.Book;
import com.example.librarysystemproject.domain.Vo.BookVo;

import java.util.List;

public interface BookService extends IService<Book> {
//    Page<BookVo> findBooksByCategoryId(int bookCategory, int pageNum);
    List<BookVo> selectBooksByBookPartInfo(String bookPartInfo);
}
