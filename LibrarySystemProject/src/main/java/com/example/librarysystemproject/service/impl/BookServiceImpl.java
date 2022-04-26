package com.example.librarysystemproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.librarysystemproject.domain.Book;
import com.example.librarysystemproject.domain.BorrowingBooks;
import com.example.librarysystemproject.domain.Vo.BookVo;
import com.example.librarysystemproject.mapper.BookMapper;
import com.example.librarysystemproject.mapper.BorrowingBooksMapper;
import com.example.librarysystemproject.service.BookService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BorrowingBooksMapper borrowingBooksMapper;
    /**
     * 根据书名查询对应的BookVo信息
     *
     * @param bookPartInfo
     * @return
     */
    @Override
    public List<BookVo> selectBooksByBookPartInfo(String bookPartInfo) {
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Book::getBookName, bookPartInfo);
        //select *from book where book_name like bookPartInfo;
        List<Book> books = bookMapper.selectList(queryWrapper);
        //未查询到此书名
        if (books == null || books.size() < 1) {
            return null;
        }
        List<BookVo> bookVos = books.stream().map((item) -> {
            BookVo bookVo = new BookVo();
            BeanUtils.copyProperties(item, bookVo);
            //根据book_id在borrowing_books表中查看是否可借
            LambdaQueryWrapper<BorrowingBooks> queryWrapper1 = new LambdaQueryWrapper<>();
            //select *from borrowing_book where book_id=?
            queryWrapper1.eq(BorrowingBooks::getBookId, item.getBookId());
            BorrowingBooks borrowingBooks = borrowingBooksMapper.selectOne(queryWrapper1);
            if (borrowingBooks==null){
                //未查询到 可借
                bookVo.setIsExist("可借");
            }else {
                //查询到了 不可借
                bookVo.setIsExist("不可借");
            }
            return bookVo;
        }).collect(Collectors.toList());
        return bookVos;
    }
    /**
     * 根据分类id分页查找所对应的书籍
     *
     * @param bookCategory
     * @param pageNum
     * @return
     */
}
