package com.poly.assigment.controller;

import com.poly.assigment.dao.RoleDAO;
import com.poly.assigment.entity.Role;
import com.poly.assigment.entity.User;
import com.poly.assigment.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleDAO roleDAO;


    @GetMapping("/dang-ky")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "home/dangky";
    }

    @PostMapping("/dang-ky")
    public String processRegistration(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            @RequestParam("confirmMatKhau") String confirmMatKhau,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra xem tên đăng nhập đã tồn tại chưa
        if (userService.findByTenDangNhap(user.getTenDangNhap()).isPresent()) {
            bindingResult.rejectValue("tenDangNhap", "error.user", "Tên đăng nhập này đã tồn tại!");
        }

        // Kiểm tra xem email đã tồn tại chưa
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "error.user", "Email này đã được sử dụng!");
        }

        // Kiểm tra mật khẩu và nhập lại mật khẩu có khớp không
        if (!user.getMatKhau().equals(confirmMatKhau)) {
            bindingResult.rejectValue("matKhau", "error.user", "Mật khẩu nhập lại không khớp!");
        }

        if (bindingResult.hasErrors()) {
            // Trả lại form đăng ký theo đường dẫn mới và hiển thị lỗi
            return "home/dangky";
        }

        // Bổ sung lại logic gán vai trò mặc định ---
        Role defaultRole = roleDAO.findById(0)
                .orElseThrow(() -> new RuntimeException("Error: Default Role 'User' (ID=0) not found."));
        user.setRole(defaultRole);

        // Lưu người dùng mới vào database
        userService.save(user);

        // Thêm thông báo thành công để hiển thị ở trang chủ
        redirectAttributes.addFlashAttribute("success", "Đăng ký tài khoản thành công!");

        return "redirect:/";
    }
}