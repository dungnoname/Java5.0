package com.poly.assignment.controller.rest;

import com.poly.assignment.dao.RoleDAO;
import com.poly.assignment.dao.UserDAO;
import com.poly.assignment.entity.Role;
import com.poly.assignment.entity.User;
import com.poly.assignment.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

//
import com.poly.assignment.service.EmailService;
import com.poly.assignment.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.UUID;

//
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Collections;

import com.poly.assignment.security.CustomUserDetailsService;

@RestController // Dùng RestController để trả về JSON
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // Cho phép Vue gọi
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtService jwtUtils;

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

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    // Chỉ cần nhận Email là đủ để tìm user và gửi mail
    public static class ForgotPasswordRequest {
        public String email;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> processForgotPassword(@RequestBody ForgotPasswordRequest req) {
        String token = UUID.randomUUID().toString();

        try {

            userService.updateResetPasswordToken(token, req.email);

            // 2. Tạo link đặt lại mật khẩu
            String resetLink = "http://localhost:5173/dat-lai-mat-khau?token=" + token;

            // 3. Gửi Email
            String subject = "Yêu cầu đặt lại mật khẩu";
            String body = "Xin chào,\n\n"
                    + "Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng nhấn vào link dưới đây:\n"
                    + resetLink + "\n\n"
                    + "Link này chỉ có hiệu lực trong thời gian ngắn.";

            emailService.sendEmail(req.email, subject, body);

            return ResponseEntity.ok("Link đặt lại mật khẩu đã được gửi vào email của bạn!");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // DTO nhận token và mật khẩu mới từ frontend
    public static class ResetPasswordRequest {
        public String token;
        public String newPassword;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> processResetPassword(@RequestBody ResetPasswordRequest req) {
        // 1. Tìm user theo token qua Service
        Optional<User> userOpt = userService.getByResetPasswordToken(req.token);

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Link không hợp lệ hoặc đã hết hạn!");
        }

        User user = userOpt.get();

        // 2. Cập nhật mật khẩu mới
        // QUAN TRỌNG: Phải mã hóa trước khi truyền vào hàm updatePassword của Service
        // Vì hàm updatePassword trong Service của bạn chỉ setMatKhau() chứ không encode.
        String encodedPassword = passwordEncoder.encode(req.newPassword);

        userService.updatePassword(user, encodedPassword);

        return ResponseEntity.ok("Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
    }

    public static class GoogleLoginRequest {
        public String idToken;
    }

    @Value("${google.client.id}") // Cấu hình trong application.properties
    String googleClientId;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody GoogleLoginRequest req) {
        try {
            // 1. Cấu hình bộ xác thực Google
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            // 2. Xác thực token gửi từ Vue
            GoogleIdToken idToken = verifier.verify(req.idToken);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                // 3. Lấy thông tin người dùng từ Google
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                // 4. Kiểm tra xem email này đã có trong DB chưa
                User user = userDAO.findByEmail(email).orElse(null);

                if (user == null) {
                    // Nếu chưa có -> Tự động đăng ký
                    user = new User();
                    user.setTenDangNhap(email); // Lấy email làm tên đăng nhập
                    user.setEmail(email);
                    user.setHoTen(name);
                    user.setMatKhau(passwordEncoder.encode("GoogleLogin@123")); // Mật khẩu ngẫu nhiên
                    user.setRole(roleDAO.findById(0).get()); // Role User
                    userDAO.save(user);
                }



                // 5. Tạo JWT của hệ thống BẠN (giống hệt lúc login thường)
                // Lưu ý: Cần tạo đối tượng Authentication thủ công để nạp vào JwtUtils
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getTenDangNhap());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                String jwt = jwtUtils.generateToken(authentication);

                List<String> roles = userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .collect(Collectors.toList());

                // 6. Trả về như login thường
                Map<String, Object> response = new HashMap<>();
                response.put("token", jwt);
                response.put("username", user.getTenDangNhap());
                response.put("roles", roles);

                return ResponseEntity.ok(response);

            } else {
                return ResponseEntity.badRequest().body("Token Google không hợp lệ");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi xác thực Google: " + e.getMessage());
        }
    }
}