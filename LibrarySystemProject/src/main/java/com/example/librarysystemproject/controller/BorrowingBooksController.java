package com.example.librarysystemproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.librarysystemproject.domain.Book;
import com.example.librarysystemproject.domain.BorrowingBooks;
import com.example.librarysystemproject.domain.User;
import com.example.librarysystemproject.domain.Vo.BorrowingBooksVo;
import com.example.librarysystemproject.service.BookService;
import com.example.librarysystemproject.service.BorrowingBooksService;
import com.example.librarysystemproject.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class BorrowingBooksController {
    @Autowired
    private BorrowingBooksService borrowingBooksService;

    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;

    /**
     * 展示所有借阅记录  user.userName book.name 借书时间 还书时间   这里getPages()=0-->参考BookController
     *
     * @return
     */
    @RequestMapping("/allBorrowBooksRecordPage")
    public String allBorrowBooksRecordPage(@RequestParam("pageNum") int pageNum, Model model) {
        Page<BorrowingBooks> borrowingBooksPage = new Page<>(pageNum, 5);
        Page<BorrowingBooksVo> pageInfo = new Page<>();
        //查询所有的借阅记录 select *from borrowing_books limit pageNum,10;
        borrowingBooksService.page(borrowingBooksPage);
        //对象拷贝
        BeanUtils.copyProperties(borrowingBooksPage,pageInfo,"records");
        List<BorrowingBooks> borrowingBooks = borrowingBooksPage.getRecords();//借阅书籍的list集合
        //无借阅记录

        if (borrowingBooks == null || borrowingBooks.size() < 1) {
            pageInfo.setPages(1);
            pageInfo.setCurrent(1);
            model.addAttribute("page", pageInfo);
        } else {


            //有借阅记录
            List<BorrowingBooksVo> borrowingBooksVos = borrowingBooks.stream().map((item) -> {
                BorrowingBooksVo booksVo = new BorrowingBooksVo();
                //根据book_id查询book_name
                Integer bookId = item.getBookId();
                LambdaQueryWrapper<Book> bookLambdaQueryWrapper = new LambdaQueryWrapper<>();
                bookLambdaQueryWrapper.eq(Book::getBookId, bookId);
                Book book = bookService.getOne(bookLambdaQueryWrapper);
                booksVo.setBook(book);
                //根据user_id查询user_name
                Integer userId = item.getUserId();
                LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
                userLambdaQueryWrapper.eq(User::getUserId, userId);
                User user = userService.getOne(userLambdaQueryWrapper);
                booksVo.setUser(user);
                //设置借书时间
                Date date = item.getDate();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateOfBorrowing = sdf.format(date);
                booksVo.setDateOfBorrowing(dateOfBorrowing);
                //设置还书时间 增加2个月
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.MONTH, 2);
                Date date2 = calendar.getTime();
                String dateOfReturn = sdf.format(date2);
                booksVo.setDateOfReturn(dateOfReturn);
                return booksVo;
            }).collect(Collectors.toList());
            pageInfo.setRecords(borrowingBooksVos);//设置借阅记录等相关记录
            model.addAttribute("page", pageInfo);
        }
        return "admin/allBorrowingBooksRecord";
    }
}
