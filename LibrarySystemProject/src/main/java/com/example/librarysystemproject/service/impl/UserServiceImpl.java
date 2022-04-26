package com.example.librarysystemproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.librarysystemproject.domain.BorrowingBooks;
import com.example.librarysystemproject.domain.User;
import com.example.librarysystemproject.mapper.BorrowingBooksMapper;
import com.example.librarysystemproject.mapper.UserMapper;
import com.example.librarysystemproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BorrowingBooksMapper borrowingBooksMapper;
    static int id = 101;

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    @Override
    public boolean insert(User user) {
        int insert = userMapper.insert(user);
        if (insert > 0) {
            return true;
        }
        return false;
    }

    /**
     * 修改用户信息
     *
     * @param user
     * @param request
     * @return
     */
    @Override
    public boolean updateUser(User user, HttpServletRequest request) {
        User userInSession = (User) request.getSession().getAttribute("user");
        user.setUserId(userInSession.getUserId());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserId, user.getUserId());
        int update = userMapper.update(user, queryWrapper);
        if (update > 0) {
            request.getSession().setAttribute("user", user);
            return true;
        }
        return false;
    }

    /**
     * 用户根据bookId借书
     *
     * @param bookId
     * @param request
     * @return
     */
    @Override
    @Transactional
    public boolean userBorrowingBook(int bookId, HttpServletRequest request) {
        //在borrowing_books表中查询是否有书籍号为book_id的书籍
        LambdaQueryWrapper<BorrowingBooks> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BorrowingBooks::getBookId, bookId);
        //select *from borrowing_books where book_id = bookId;
        BorrowingBooks borrowingBooks = borrowingBooksMapper.selectOne(queryWrapper);
        //有 借书失败
        if (borrowingBooks != null)
            return false;
        //没有 借书成功 向borrowing_books中插入一条借阅记录
        User user = (User) request.getSession().getAttribute("user");
        BorrowingBooks borrowingBook = new BorrowingBooks();
        borrowingBook.setBookId(bookId);
        borrowingBook.setUserId(user.getUserId());
        borrowingBook.setDate(new Date());
        borrowingBook.setId(id++);

        int insert = 0;
        try {
//            borrowingBooksMapper.
            insert = borrowingBooksMapper.insert(borrowingBook);
        } catch (Exception e) {
            return false;
//            e.printStackTrace();
        }
        if (insert > 0)
            return true;
        return false;
    }

    /**
     * 删除borrowing_books表中book_id=bookId,user_id=userId的借阅记录
     * @param bookId
     * @param request
     * @return
     */
    @Override
    public boolean userReturnBook(int bookId, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        LambdaQueryWrapper<BorrowingBooks> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BorrowingBooks::getBookId,bookId);
        queryWrapper.eq(BorrowingBooks::getUserId,user.getUserId());
        BorrowingBooks borrowingBooks = borrowingBooksMapper.selectOne(queryWrapper);
        //不存在该用户的借书记录则还书失败
        if (borrowingBooks==null){
            return false;
        }
        //还书成功
        int delete = borrowingBooksMapper.delete(queryWrapper);
        if (delete>0){
            return true;
        }
        return false;
    }

    /**
     * 还书页面
     * @return
     */
    @GetMapping("/userReturnBooksPage")
    public String userReturnBooksPage(){
        return "user/returnBooks";
    }

}
