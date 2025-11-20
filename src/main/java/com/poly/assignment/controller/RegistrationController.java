package com.poly.assignment.controller;

import com.poly.assignment.dao.RoleDAO;
import com.poly.assignment.entity.Role;
import com.poly.assignment.entity.User;
import com.poly.assignment.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleDAO roleDAO;

    // ===== Hiển thị form đăng ký =====
    @GetMapping("/dang-ky")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "home/dangky";
    }

    // ===== Xử lý form đăng ký =====
    @PostMapping("/dang-ky")
    public String processRegistration(
            @Valid @ModelAttribute("user") User user,
            BindingResult bindingResult,
            @RequestParam("confirmMatKhau") String confirmMatKhau,
            RedirectAttributes redirectAttributes,
            Model model) {

        // ⚠️ Kiểm tra trùng tên đăng nhập
        if (userService.findByTenDangNhap(user.getTenDangNhap()).isPresent()) {
            bindingResult.rejectValue("tenDangNhap", "error.user", "Tên đăng nhập này đã tồn tại!");
        }

        // ⚠️ Kiểm tra trùng email
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "error.user", "Email này đã được sử dụng!");
        }

        // ⚠️ Kiểm tra khớp mật khẩu nhập lại
        if (!user.getMatKhau().equals(confirmMatKhau)) {
            bindingResult.rejectValue("matKhau", "error.user", "Mật khẩu nhập lại không khớp!");
        }

        // ⚠️ Nếu có lỗi thì quay lại form
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "home/dangky";
        }

        // ✅ Gán giá trị mặc định cho các trường bắt buộc nhưng không có trong form
        if (user.getNgaySinh() == null) {
            user.setNgaySinh(LocalDate.now());
        }
        if (user.getGioiTinh() == null) {
            user.setGioiTinh(true); // Mặc định là Nam
        }
        if (user.getSoDienThoai() == null || user.getSoDienThoai().isBlank()) {
            user.setSoDienThoai("0123456789");
        }
        if (user.getDiaChi() == null || user.getDiaChi().isBlank()) {
            user.setDiaChi("Chưa cập nhật");
        }

        // ✅ Gán vai trò mặc định (RoleId = 0)
        Role defaultRole = roleDAO.findById(0)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Role mặc định (ID=0)!"));
        user.setRole(defaultRole);

        // ✅ Lưu user vào CSDL
        userService.save(user);

        // ✅ Gửi thông báo thành công
        redirectAttributes.addFlashAttribute("success", "🎉 Đăng ký tài khoản thành công! Bạn có thể đăng nhập ngay.");

        return "redirect:/dang-nhap";
    }
}
