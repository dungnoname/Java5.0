package com.poly.assignment.controller;

import com.poly.assignment.dao.UserDAO;
import com.poly.assignment.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:5173")
public class ProfileRestController {

    @Autowired
    UserDAO userDAO;

    // Helper: Lấy user đang đăng nhập
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userDAO.findByTenDangNhap(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
    }

    // 1. XEM THÔNG TIN CÁ NHÂN
    @GetMapping
    public ResponseEntity<?> getProfile() {
        User user = getCurrentUser();
        return ResponseEntity.ok(user);
    }

    // DTO cho cập nhật thông tin
    public static class UpdateProfileRequest {
        public String hoTen;
        public String email;
        public String soDienThoai;
        public String diaChi;
    }

    // 2. CẬP NHẬT THÔNG TIN
    @PutMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest req) {
        User user = getCurrentUser();

        // Cập nhật các trường
        user.setHoTen(req.hoTen);
        user.setEmail(req.email);
        user.setSoDienThoai(req.soDienThoai);
        user.setDiaChi(req.diaChi);

        userDAO.save(user);

        return ResponseEntity.ok("Cập nhật thông tin thành công!");
    }

    // DTO cho đổi mật khẩu
    public static class ChangePasswordRequest {
        public String oldPassword;
        public String newPassword;
        public String confirmPassword;
    }

    // 3. ĐỔI MẬT KHẨU (Trực tiếp, không cần mail)
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest req) {
        User user = getCurrentUser();

        // Kiểm tra mật khẩu cũ
        // Lưu ý: Vì bạn đang dùng NoOp (không mã hóa), nên so sánh equals bình thường.
        // Nếu sau này dùng BCrypt, phải dùng passwordEncoder.matches(raw, encoded)
        if (!user.getMatKhau().equals(req.oldPassword)) {
            return ResponseEntity.badRequest().body("Mật khẩu cũ không đúng!");
        }

        // Kiểm tra mật khẩu mới
        if (!req.newPassword.equals(req.confirmPassword)) {
            return ResponseEntity.badRequest().body("Mật khẩu xác nhận không khớp!");
        }

        // Lưu mật khẩu mới
        user.setMatKhau(req.newPassword);
        userDAO.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Đổi mật khẩu thành công!");
        return ResponseEntity.ok(response);
    }
}