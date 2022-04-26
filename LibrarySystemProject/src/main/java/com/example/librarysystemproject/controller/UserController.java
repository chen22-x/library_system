package com.example.librarysystemproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.librarysystemproject.domain.BorrowingBooks;
import com.example.librarysystemproject.domain.User;
import com.example.librarysystemproject.domain.Vo.BorrowingBooksVo;
import com.example.librarysystemproject.service.BorrowingBooksService;
import com.example.librarysystemproject.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private BorrowingBooksService borrowingBooksService;

    /**
     * 根据userId删除用户信息  有外键约束 借了书的用户无法删除
     *
     * @return
     */
    @RequestMapping("/deleteUser")
    @ResponseBody
    public String deleteUser(User user) {
        log.info("userId:", user.getUserId());
        LambdaQueryWrapper<BorrowingBooks> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(BorrowingBooks::getUserId, user.getUserId());
        //select *from borrowing_books where user_id = ?
        BorrowingBooks borrowingBooks = borrowingBooksService.getOne(queryWrapper1);

        if (borrowingBooks != null) {
            //该用户借了书无法删除
            return "false";
        } else {
            //该用户没有借书 可以删除
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserId, user.getUserId());
            //delete from user where user_id = userId;
            boolean remove = userService.remove(queryWrapper);
            if (remove) {
                return "true";
            } else {
                return "false";
            }
        }
    }

    /**
     * 新增用户信息 需要判断user表是否存在新增的用户名，已经存在的用户名无法添加，
     *
     * @return
     */
    @PostMapping("/addUser")
    @ResponseBody
    public String addUserPage(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, user.getUserName());

        User user1 = userService.getOne(queryWrapper);
        //如果查询到说明用户名已存在
        if (user1 != null) {
            return "false";
        } else {
            //没有查询到，可以添加
            boolean insert = userService.insert(user);
            if (insert) {
                //插入成功
                return "true";
            }
            return "false";
        }
    }

    /**
     * 用户登录
     *
     * @return
     */
    @PostMapping("/userLogin")
    public String userLogin(HttpServletRequest request) {
        String username = request.getParameter("userName");
        String password = request.getParameter("password");
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(username != null, User::getUserName, username);
        queryWrapper.eq(password != null, User::getUserPwd, password);
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            //登录失败
            request.getSession().setAttribute("flag", 1);
            return "index";
        }
        //登录成功
        request.getSession().setAttribute("flag", 0);
        request.getSession().setAttribute("user", user);
        return "user/index";
    }

    /**
     * 用户退出登录
     *
     * @return
     */
    @GetMapping("/userLogOut")
    public String userLogOut(HttpServletRequest request) {
        request.getSession().removeAttribute("user");
        return "index";
    }

    /**
     * 查看用户信息
     *
     * @return
     */
    @GetMapping("/userMessagePage")
    public String userMessagePage(HttpServletRequest request) {

        return "user/userMessage";
    }

    /**
     * 更新用户信息
     *
     * @return
     */
    @PostMapping("/updateUser")
    @ResponseBody
    public boolean updateUser(User user, HttpServletRequest request) {
        return userService.updateUser(user, request);
    }

    /**
     * 展示用户借阅记录 根据当前用户id 在borrowing_books中查看借阅记录 再根据book_id在book表中查看所借阅的书籍信息
     * 书籍id（book表）  书名（book表）  借书日期（borrowing_books表）  还书日期（borrowing_books表）
     *
     * @return
     */
    @GetMapping("/userBorrowBookRecord")
    public String userBorrowBookRecord(Model model, HttpServletRequest request) {
        List<BorrowingBooksVo> borrowingBooksVos = borrowingBooksService.selectAllBorrowRecord(request);
        model.addAttribute("borrowingBooksList", borrowingBooksVos);
        return "user/borrowingBooksRecord";
    }

    /**
     * 返回借书页面
     * @return
     */
    @GetMapping("/borrowingPage")
    public String borrowingPage(){
        return "user/borrowingBooks";
    }



    /**
     * 用户查询书籍页面
     * @return
     */
    @GetMapping("/findBookPage")
    public String findBookPage() {
        return "user/findBook";
    }

    /**
     * 用户借书
     * 根据book_id借书
     * 在borrowing_books表查询是否已经借出
     *      如果借出则借书失败
     *      未借出则可以借书成功
     * 修改当前用户的借书记录 BorrowingBooksVo表
     * @return
     */
    @PostMapping("/userBorrowingBook")
    @ResponseBody
    public boolean userBorrowingBook(int bookId,HttpServletRequest request){
        return userService.userBorrowingBook(bookId,request);
    }

    /**
     * 返回还书页面
     * @return
     */
    @GetMapping("/userReturnBooksPage")
    public String userReturnBooksPage(){
        return "user/returnBooks";
    }

    /**
     * 还书 根据book_id与当前登录用户的user_id
     * 删除borrowing_books表中的借阅信息
     * @return
     */
    @PostMapping("/userReturnBook")
    @ResponseBody
    public boolean userReturnBook(int bookId,HttpServletRequest request){
        return userService.userReturnBook(bookId, request);
    }
}
