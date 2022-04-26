package com.example.librarysystemproject.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.librarysystemproject.domain.BorrowingBooks;
import com.example.librarysystemproject.domain.Vo.BorrowingBooksVo;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public interface BorrowingBooksService extends IService<BorrowingBooks> {
    ArrayList<BorrowingBooksVo> selectAllBorrowRecord(HttpServletRequest request);
}
