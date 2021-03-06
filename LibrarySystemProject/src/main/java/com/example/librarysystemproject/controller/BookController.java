package com.example.librarysystemproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.librarysystemproject.domain.Book;
import com.example.librarysystemproject.domain.BookCategory;
import com.example.librarysystemproject.domain.BorrowingBooks;
import com.example.librarysystemproject.domain.Vo.BookVo;
import com.example.librarysystemproject.service.AdminService;
import com.example.librarysystemproject.service.BookCategoryService;
import com.example.librarysystemproject.service.BookService;
import com.example.librarysystemproject.service.BorrowingBooksService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class BookController {

    @Autowired
    private BookService bookService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private BookCategoryService bookCategoryService;
    @Autowired
    private BorrowingBooksService borrowingBooksService;

    @PostMapping("/findAllBookCategory")
    @ResponseBody
    public List<BookCategory> findAllBookCategory() {
        return adminService.getBookCategory();
    }

    @PostMapping("/addBook")
    @ResponseBody
    public String addBook(Book book) {
        log.info("book:", book.toString());
        boolean insert = adminService.addBook(book);
        if (insert)
            return "true";
        return "false";
    }

    @PostMapping("/addBookCategory")
    @ResponseBody
    public String addBookCategory(@RequestParam("categoryName") String categoryName) {
        boolean addBookCategory = adminService.addBookCategory(categoryName);
        if (addBookCategory) {
            return "true";
        }
        return "false";
    }

    /**
     * ??????????????????
     *
     * @param bookCategoryId
     * @return
     */
    @RequestMapping("/deleteCategory")
    @ResponseBody
    public String deleteCategory(@RequestParam("bookCategoryId") String bookCategoryId) {
        int i = bookCategoryService.deleteBookCategory(Integer.parseInt(bookCategoryId));
        if (i > 0)
            return "true";
        return "false";
    }

    /**
     * ????????????????????????????????? ????????????
     * ??????????????? ???book???????????????????????????????????????
     *
     * @param bookCategory
     * @return
     */
    @GetMapping("/showBooksResultPageByCategoryId")
    public String showBooksResultPageByCategoryId(@RequestParam("bookCategory") int bookCategory, @RequestParam("pageNum") int pageNum, Model model) {
        Page<Book> pageInfo = new Page<>(pageNum, 5);
        Page<BookVo> bookVoPage = new Page<>();

        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        //???????????????????????????
        queryWrapper.eq(Book::getBookCategory, bookCategory);
        //????????????  select * from book where book_category=? limit pageNum,5;
        bookService.page(pageInfo, queryWrapper);

        //???????????? ??????records ???????????????
        BeanUtils.copyProperties(pageInfo, bookVoPage, "records");

        //?????????=bookCategory???????????????
        List<Book> books = pageInfo.getRecords();
//        List<Book> books = bookService.list(queryWrapper);//????????????????????????????????????

        //????????????????????? ???????????????
        if (books == null || books.size() < 1) {
            bookVoPage.setPages(1);//?????????
            bookVoPage.setCurrent(1);//????????????
            model.addAttribute("page", bookVoPage);
            model.addAttribute("bookCategory", bookCategory);
        } else {
            List<BookVo> bookVos = books.stream().map((item) -> {
                BookVo bookVo = new BookVo();
                //????????????:
                BeanUtils.copyProperties(item, bookVo);
                //??????isExist??????
                LambdaQueryWrapper<BorrowingBooks> queryWrapper1 = new LambdaQueryWrapper<>();
                //??????book_id???borrowing_books???????????????????????????
                queryWrapper1.eq(BorrowingBooks::getBookId, item.getBookId());
                List<BorrowingBooks> borrowingBooks = borrowingBooksService.list(queryWrapper1);
                //borrowingBooks??????????????????????????????  ???????????????
                if (borrowingBooks == null || borrowingBooks.size() < 1) {
                    bookVo.setIsExist("??????");
                } else {
                    bookVo.setIsExist("?????????");
                }
                return bookVo;
            }).collect(Collectors.toList());

            //????????????????????????
            bookVoPage.setRecords(bookVos);
            model.addAttribute("page", bookVoPage);
            model.addAttribute("bookCategory", bookCategory);
        }
/*
        //???????????????????????????session?????????
        if (books == null || books.size() < 1) {
            pageInfo.setCurrent(1);//????????????
            pageInfo.setPages(1);//?????????
            model.addAttribute("page", pageInfo);
        } else {
            for (Book book : books) {
                BookVo bookVo = new BookVo();
                bookVo.setBookId(book.getBookId());
                bookVo.setBookAuthor(book.getBookAuthor());
                bookVo.setBookName(book.getBookName());
                bookVo.setBookPublish(book.getBookPublish());
                LambdaQueryWrapper<BorrowingBooks> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(BorrowingBooks::getBookId, book.getBookId());
                List<BorrowingBooks> list = borrowingBooksService.list(queryWrapper1);
                //????????????????????????????????????????????????  ??????????????????
                if (list == null || list.size() < 1) {
                    bookVo.setIsExist("??????");
                } else {
                    bookVo.setIsExist("?????????");
                }
                bookVos.add(bookVo);
            }
//            page.setCurrent(pageNum); //???????????????
//            page.setSize(5); //??????????????????
//            page.setTotal(bookVos.size());//????????????
//            long pageCount = 0;
//            if (page.getTotal() / page.getSize() == 0) {
//                pageCount = page.getTotal() / page.getSize();
//                page.setPages(pageCount);
//
//            } else {
//                pageCount = page.getTotal() / page.getSize() + 1;
//                page.setPages(pageCount);
//            }
            //select * from book where bookcategory=? limit ?,?;
            pageInfo.setRecords(bookVos);
 */
        return "admin/showBooks";
    }

    /**
     * ??????????????????  BookVo???
     * ????????????bookPartInfo???????????????BookVo??????
     * @return
     */
    @GetMapping("/findBookByBookPartInfo")
    public String findBookByBookPartInfo(@RequestParam("bookPartInfo") String bookPartInfo,Model model){
        log.info("bookPartInfo:",bookPartInfo);
        List<BookVo> bookVos = bookService.selectBooksByBookPartInfo(bookPartInfo);
        model.addAttribute("bookList",bookVos);
        return "user/findBook";
    }
}
