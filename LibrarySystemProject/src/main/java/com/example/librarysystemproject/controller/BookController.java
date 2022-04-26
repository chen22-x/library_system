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
     * 删除图书分类
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
     * 根据分类查询对应的书籍 分页查找
     * 已知分类号 在book表中查询对应分类的所有书籍
     *
     * @param bookCategory
     * @return
     */
    @GetMapping("/showBooksResultPageByCategoryId")
    public String showBooksResultPageByCategoryId(@RequestParam("bookCategory") int bookCategory, @RequestParam("pageNum") int pageNum, Model model) {
        Page<Book> pageInfo = new Page<>(pageNum, 5);
        Page<BookVo> bookVoPage = new Page<>();

        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        //根据图书类别号查找
        queryWrapper.eq(Book::getBookCategory, bookCategory);
        //执行查询  select * from book where book_category=? limit pageNum,5;
        bookService.page(pageInfo, queryWrapper);

        //对象拷贝 忽略records 泛型不一致
        BeanUtils.copyProperties(pageInfo, bookVoPage, "records");

        //分类号=bookCategory的所有书籍
        List<Book> books = pageInfo.getRecords();
//        List<Book> books = bookService.list(queryWrapper);//查询出指定分类的所有书籍

        //如果没有查询到 则直接返回
        if (books == null || books.size() < 1) {
            bookVoPage.setPages(1);//总页数
            bookVoPage.setCurrent(1);//当前页数
            model.addAttribute("page", bookVoPage);
            model.addAttribute("bookCategory", bookCategory);
        } else {
            List<BookVo> bookVos = books.stream().map((item) -> {
                BookVo bookVo = new BookVo();
                //对象拷贝:
                BeanUtils.copyProperties(item, bookVo);
                //设置isExist属性
                LambdaQueryWrapper<BorrowingBooks> queryWrapper1 = new LambdaQueryWrapper<>();
                //根据book_id在borrowing_books中查询是否已经借出
                queryWrapper1.eq(BorrowingBooks::getBookId, item.getBookId());
                List<BorrowingBooks> borrowingBooks = borrowingBooksService.list(queryWrapper1);
                //borrowingBooks为空则当前书籍未被借  设置为可借
                if (borrowingBooks == null || borrowingBooks.size() < 1) {
                    bookVo.setIsExist("可借");
                } else {
                    bookVo.setIsExist("不可借");
                }
                return bookVo;
            }).collect(Collectors.toList());

            //设置查询书籍数据
            bookVoPage.setRecords(bookVos);
            model.addAttribute("page", bookVoPage);
            model.addAttribute("bookCategory", bookCategory);
        }
/*
        //没有查询到直接存入session并返回
        if (books == null || books.size() < 1) {
            pageInfo.setCurrent(1);//当前页码
            pageInfo.setPages(1);//总页数
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
                //在借阅表中没有查询到当前图片信息  则表示可借出
                if (list == null || list.size() < 1) {
                    bookVo.setIsExist("可借");
                } else {
                    bookVo.setIsExist("不可借");
                }
                bookVos.add(bookVo);
            }
//            page.setCurrent(pageNum); //当前页码数
//            page.setSize(5); //每页显示条数
//            page.setTotal(bookVos.size());//总记录数
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
     * 条件查询书籍  BookVo表
     * 根据书名bookPartInfo查询对应的BookVo信息
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
