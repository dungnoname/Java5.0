package com.poly.assignment.controller;

import com.poly.assignment.dao.RoleDAO;
import com.poly.assignment.dao.UserDAO;
import com.poly.assignment.entity.Role;
import com.poly.assignment.entity.User;
import com.poly.assignment.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController // Dùng RestController để trả về JSON
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // Cho phép Vue gọi
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    // Class phụ để nhận dữ liệu từ Vue gửi lên (Thay cho @RequestParam)
    public static class LoginRequest {
        public String username;
        public String password;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        // 1. Gọi AuthenticationManager để xác thực (Nó sẽ tự so sánh password đã mã hóa)
        // Lưu ý: Nếu DB của bạn pass đang là "123" (chưa mã hóa), xem mục Lưu ý bên dưới
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username,
                        loginRequest.password
                )
        );

        // 2. Lưu thông tin vào Context (quan trọng để Spring biết đã login)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Sinh ra Token JWT
        String jwt = jwtUtils.generateToken(authentication);

        // 4. Lấy thông tin UserDetails để trả về frontend (Tên, Quyền...)
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // 5. Đóng gói kết quả trả về JSON
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("username", userDetails.getUsername());
        response.put("roles", roles);

        return ResponseEntity.ok(response);
    }

    @Autowired
    UserDAO userDAO;

    @Autowired
    RoleDAO roleDAO;

    // DTO Đăng ký
    public static class RegisterRequest {
        public String username;
        public String password;
        public String confirmPassword;
        public String email;
        public String fullname;
    }

    // API ĐĂNG KÝ
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest req) {
        // 1. Kiểm tra trùng Username
        if (userDAO.findByTenDangNhap(req.username).isPresent()) {
            return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại!");
        }

        // 2. Kiểm tra trùng Email
        if (userDAO.findByEmail(req.email).isPresent()) {
            return ResponseEntity.badRequest().body("Email này đã được sử dụng!");
        }

        // 3. Kiểm tra khớp mật khẩu
        if (!req.password.equals(req.confirmPassword)) {
            return ResponseEntity.badRequest().body("Mật khẩu xác nhận không khớp!");
        }

        // 4. Tạo User mới
        User user = new User();
        user.setTenDangNhap(req.username);
        user.setMatKhau(req.password); // Sau này nhớ mã hóa
        user.setHoTen(req.fullname);
        user.setEmail(req.email);

        // Giá trị mặc định
        user.setNgaySinh(LocalDate.now());
        user.setGioiTinh(true);
        user.setSoDienThoai("");
        user.setDiaChi("");

        // 5. Set Role Mặc định (User - ID 0)
        Role roleUser = roleDAO.findById(0)
                .orElseThrow(() -> new RuntimeException("Lỗi: Không tìm thấy Role User (ID=0)"));
        user.setRole(roleUser);

        userDAO.save(user);

        return ResponseEntity.ok("Đăng ký thành công!");


    }
}