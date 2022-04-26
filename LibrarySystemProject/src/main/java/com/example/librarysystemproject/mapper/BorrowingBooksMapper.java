package com.example.librarysystemproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.librarysystemproject.domain.BorrowingBooks;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BorrowingBooksMapper extends BaseMapper<BorrowingBooks> {

}
