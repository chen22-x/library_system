package com.example.librarysystemproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.librarysystemproject.domain.Book;
import com.example.librarysystemproject.domain.BorrowingBooks;
import com.example.librarysystemproject.domain.User;
import com.example.librarysystemproject.domain.Vo.BorrowingBooksVo;
import com.example.librarysystemproject.mapper.BorrowingBooksMapper;
import com.example.librarysystemproject.service.BookService;
import com.example.librarysystemproject.service.BorrowingBooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class BorrowingBooksServiceImpl extends ServiceImpl<BorrowingBooksMapper, BorrowingBooks> implements BorrowingBooksService {
    @Autowired
    private BorrowingBooksMapper borrowingBooksMapper;
    @Autowired
    private BookService bookService;

    /**
     * 查询当前用户的所有借书记录
     *
     * @param request
     * @return
     */
    @Override
    public ArrayList<BorrowingBooksVo> selectAllBorrowRecord(HttpServletRequest request) {
        //当前用户借阅的所有书籍信息
        ArrayList<BorrowingBooksVo> borrowingBooksVos = new ArrayList<>();

        User user = (User) request.getSession().getAttribute("user");
        Integer userId = user.getUserId();//当前登录用户的id
        LambdaQueryWrapper<BorrowingBooks> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(BorrowingBooks::getDate);
        queryWrapper.eq(BorrowingBooks::getUserId, userId);

        //当前用户所借阅书籍 borrowing_books表
        List<BorrowingBooks> borrowingBooks = borrowingBooksMapper.selectList(queryWrapper);
        //没有借阅记录
        if (borrowingBooks == null)
            return null;
        for(BorrowingBooks item : borrowingBooks){
            BorrowingBooksVo booksVo = new BorrowingBooksVo();
            LambdaQueryWrapper<Book> bookLambdaQueryWrapper = new LambdaQueryWrapper<>();
            bookLambdaQueryWrapper.eq(Book::getBookId,item.getBookId());
            Book book = bookService.getOne(bookLambdaQueryWrapper);
            booksVo.setBook(book);

            //借书日期
            Date date1 = item.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateOfBorrowing = sdf.format(date1);
            //还书日期
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date1);
            calendar.add(Calendar.MONTH,2);
            Date date2 = calendar.getTime();
            String dateOfReturning = sdf.format(date2);

            booksVo.setDateOfBorrowing(dateOfBorrowing);
            booksVo.setDateOfReturn(dateOfReturning);

            borrowingBooksVos.add(booksVo);
        }
        return borrowingBooksVos;
    }
}
