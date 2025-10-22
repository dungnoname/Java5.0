package com.poly.assigment.controller.crud; // Khớp với package của bạn

import com.poly.assigment.entity.User;
import com.poly.assigment.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class CURDLoginController {

    @Autowired
    private UserService userService;
    
    @GetMapping("/crud/login")
    public String showLoginForm() {
        return "CRUD/login";
    }

    @PostMapping("/crud/login")
    public String processLogin(
            @RequestParam("tenDangNhap") String tenDangNhap,
            @RequestParam("matKhau") String matKhau,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Optional<User> userOptional = userService.findByTenDangNhap(tenDangNhap);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (matKhau.equals(user.getMatKhau())) {
                if (user.getRole() != null && (user.getRole().getRoleId() == 1 || user.getRole().getRoleId() == 2)) {

                    session.setAttribute("currentUser", user);
                    session.setMaxInactiveInterval(60 * 60 * 2);
                    return "redirect:/admin/users";

                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Tài khoản không có quyền truy cập.");
                    return "redirect:/crud/login";
                }
            }
        }

        redirectAttributes.addFlashAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
        return "redirect:/crud/login";
    }

    /**
     * ✅ GET /crud/logout : Xử lý đăng xuất
     */
    @GetMapping("/crud/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "Bạn đã đăng xuất thành công.");
        return "redirect:/crud/login";
    }
}