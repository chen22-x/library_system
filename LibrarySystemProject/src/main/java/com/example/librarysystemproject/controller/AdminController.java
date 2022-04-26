package com.example.librarysystemproject.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.librarysystemproject.domain.Admin;
import com.example.librarysystemproject.domain.BookCategory;
import com.example.librarysystemproject.domain.User;
import com.example.librarysystemproject.domain.Vo.BookVo;
import com.example.librarysystemproject.service.AdminService;
import com.example.librarysystemproject.service.BookCategoryService;
import com.example.librarysystemproject.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
//@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private BookCategoryService bookCategoryService;
    @Autowired
    private UserService userService;
    /**
     * 登录
     * @param request
     * @return
     */
    @PostMapping("/adminLogin")
    public String adminLogin(HttpServletRequest request) {
        String userName = request.getParameter("userName");
        String password = request.getParameter("password");
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userName != null, Admin::getAdminName, userName);
        queryWrapper.eq(password!=null,Admin::getAdminPwd,password);
        Admin admin = adminService.getOne(queryWrapper);
        //未查询到
        if (admin==null){
            request.getSession().setAttribute("flag",1);//1为登录失败
            return "index";
        }
        request.getSession().setAttribute("admin",admin);
        request.getSession().setAttribute("flag",0);//0表示登陆成功
        return "admin/index";//登录成功 跳转到admin主页面
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @GetMapping("/adminLogOut")
    public String adminLogOut(HttpServletRequest request){
        request.getSession().removeAttribute("admin");
        return "index";//返回登录页面
    }
    /**
     * 个人信息
     */
    @GetMapping("/adminInfoPage")
    public String adminInfoPage(HttpServletRequest request){
        return "admin/adminInfo";
    }

    /**
     * 修改admin信息
     * @param admin 需要更新修改后的admin信息
     * @return
     */
    @PostMapping("/updateAdmin")
    @ResponseBody
    public boolean updateAdmin(Admin adminUpdate, HttpServletRequest request){
        log.info("admin:",adminUpdate.toString());
        return adminService.updateAdmin(adminUpdate,request);
    }

    @GetMapping("/addBookPage")
    public String addBookPage(){
       return "admin/addBook";
    }

    @GetMapping("/addCategoryPage")
    public String addCategoryPage(@RequestParam("pageNum") int pageNum, Model model){
        Page<BookCategory> page = new Page<>(pageNum,5);
        bookCategoryService.page(page);
//        page.getRecords().forEach(System.out::println);
        model.addAttribute("page",page);

        return "admin/addCategory";
    }

    /**
     * 按分类查询书籍页面
     * @param model
     * @return
     */
    @GetMapping("/showBooksPage")
    public String showBooksPage(Model model){
        Page<BookVo> page = new Page<>();
        page.setCurrent(1);
//        page.setTotal(1);//总记录数
        page.setPages(1);//总页数
        model.addAttribute("page",page);

        return "admin/showBooks";
    }

    /**
     * 分页查询用户信息
     * @param pageNum
     * @param model
     * @return
     */
    @GetMapping("/showUsersPage")
    public String showUsersPage(@RequestParam("pageNum") int pageNum,Model model){
        Page<User> page = new Page<>(pageNum,10);
        // select *from user limit pageNum,10;
        //执行分页查询 将所有user查询出来
        userService.page(page);
        model.addAttribute("page",page);
        //展示页面
        return "admin/showUsers";
    }
//    @GetMapping("/addUserPage")
//    public String addUserPage(){
//
//    }

    /**
     * 新增用户
     */
    @GetMapping("/addUserPage")
    public String addUserPage(){
        return "admin/addUser";
    }
}
