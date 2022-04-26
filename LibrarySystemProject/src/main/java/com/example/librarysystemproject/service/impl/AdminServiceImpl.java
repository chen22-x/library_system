package com.example.librarysystemproject.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.librarysystemproject.domain.Admin;
import com.example.librarysystemproject.domain.Book;
import com.example.librarysystemproject.domain.BookCategory;
import com.example.librarysystemproject.mapper.AdminMapper;
import com.example.librarysystemproject.mapper.BookCategoryMapper;
import com.example.librarysystemproject.mapper.BookMapper;
import com.example.librarysystemproject.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private BookCategoryMapper bookCategoryMapper;
    @Autowired
    private BookMapper bookMapper;
    /**
     * 更新admin信息
     * @param admin
     * @param request
     */
    @Override
    public boolean updateAdmin(Admin admin, HttpServletRequest request) {
        //从session中获取admin对象
        Admin adminSession = (Admin) request.getSession().getAttribute("admin");
        Integer adminId = adminSession.getAdminId();//session中存储（此时登录的admin账户）的admin的id
        admin.setAdminId(adminId);//设置请求参数对象的id
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getAdminId,admin.getAdminId());
        int update = adminMapper.update(admin, queryWrapper);
        if (update>0){//更新成功
            request.getSession().setAttribute("admin",admin);//重新设置存储在session中的admin对象
            return true;
        }
        return false;
    }

    @Override
    public List<BookCategory> getBookCategory() {
        return bookCategoryMapper.selectList(null);
    }

    @Override
    public boolean addBook(Book book) {

        int insert = bookMapper.insert(book);
        if (insert>0){
            return true;
        }
        return false;
    }

    /**
     * 新增类别
     * @param categoryName
     * @return
     */
    @Override
    public boolean addBookCategory(String categoryName) {
        LambdaQueryWrapper<BookCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryName!=null,BookCategory::getCategoryName,categoryName);
        BookCategory bookCategory = bookCategoryMapper.selectOne(queryWrapper);
        //不为null说明存在 不能添加重复的类别名称
        if (bookCategory!=null){
            return false;
        }
        BookCategory category = new BookCategory();
        category.setCategoryName(categoryName);
        bookCategoryMapper.insert(category);
        return true;
    }
}
