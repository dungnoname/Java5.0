package com.poly.assigment.controller;

import com.poly.assigment.entity.User;
import com.poly.assigment.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;


    @Autowired
    private HttpSession session;

    @GetMapping("/dang-nhap")
    public String showLoginForm() {
        return "home/dangnhap";
    }

    @PostMapping("/dang-nhap")
    public String processLogin(@RequestParam("username") String username,
                               @RequestParam("password") String password) {

        // 1. Tìm người dùng theo tên đăng nhập
        Optional<User> optionalUser = userService.findByTenDangNhap(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (password.equals(user.getMatKhau())) {
                // Đăng nhập thành công
                session.setAttribute("currentUser", user); // Lưu thông tin người dùng vào session
                return "redirect:/"; // Chuyển hướng về trang chủ
            }

        }

        // 3. Nếu người dùng không tồn tại hoặc sai mật khẩu
        return "redirect:/dang-nhap?error"; // Chuyển hướng lại trang đăng nhập với thông báo lỗi
    }

    @GetMapping("/dang-xuat")
    public String logout() {
        session.removeAttribute("currentUser"); // Xóa người dùng khỏi session
        return "redirect:/dang-nhap";
    }
}