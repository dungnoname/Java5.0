package com.poly.assignment.controller.rest;

import com.poly.assignment.entity.Role;
import com.poly.assignment.entity.User;
import com.poly.assignment.service.RoleService;
import com.poly.assignment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:5173") // Cho phép Vue truy cập
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Lấy danh sách User (Có phân trang)
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userService.findAll(pageable);
        return ResponseEntity.ok(userPage);
    }

    // 2. Lấy danh sách Roles (Để Vue đổ vào dropdown chọn quyền)
    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        List<Role> roles = roleService.findAll();
        return ResponseEntity.ok(roles);
    }

    // 3. Lấy chi tiết 1 User (Để hiển thị lên form sửa)
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 4. Thêm mới User
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        // Validation thủ công (Thay cho BindingResult)
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email đã tồn tại!");
        }
        if (userService.findByTenDangNhap(user.getTenDangNhap()).isPresent()) {
            return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại!");
        }
        if (user.getMatKhau() == null || user.getMatKhau().isEmpty()) {
            return ResponseEntity.badRequest().body("Mật khẩu không được để trống!");
        }

        try {
            // Xử lý Role (Vue gửi lên {role: {roleId: 1}})
            if (user.getRole() == null || user.getRole().getRoleId() == null) {
                return ResponseEntity.badRequest().body("Vui lòng chọn vai trò!");
            }

            // TODO: Mã hóa mật khẩu ở đây nếu cần: user.setMatKhau(encoder.encode(user.getMatKhau()));
            String encodedPass = passwordEncoder.encode(user.getMatKhau());
            user.setMatKhau(encodedPass);

            User savedUser = userService.save(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi thêm: " + e.getMessage());
        }
    }

    // 5. Cập nhật User
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody User user) {
        Optional<User> existingUserOpt = userService.findById(id);
        if (existingUserOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User existingUser = existingUserOpt.get();

        // Check trùng Email (Trừ chính nó ra)
        Optional<User> emailCheck = userService.findByEmail(user.getEmail());
        if (emailCheck.isPresent() && !emailCheck.get().getUserId().equals(id)) {
            return ResponseEntity.badRequest().body("Email đã được sử dụng bởi người khác!");
        }

        try {
            // Cập nhật thông tin cơ bản
            existingUser.setHoTen(user.getHoTen());
            existingUser.setEmail(user.getEmail());
            existingUser.setSoDienThoai(user.getSoDienThoai());
            existingUser.setDiaChi(user.getDiaChi());

            // Cập nhật Role
            if (user.getRole() != null) {
                existingUser.setRole(user.getRole());
            }

            // Xử lý mật khẩu: Nếu Vue gửi mật khẩu rỗng -> Giữ nguyên mật khẩu cũ
            if (user.getMatKhau() != null && !user.getMatKhau().isEmpty()) {
                // TODO: existingUser.setMatKhau(encoder.encode(user.getMatKhau()));
                String encodedPass = passwordEncoder.encode(user.getMatKhau());
                existingUser.setMatKhau(encodedPass);
            }
            // Nếu user.getMatKhau() rỗng thì không làm gì -> Giữ pass cũ

            userService.save(existingUser);
            return ResponseEntity.ok(existingUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi cập nhật: " + e.getMessage());
        }
    }

    // 6. Xóa User
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        if (!userService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        try {
            userService.deleteById(id);
            return ResponseEntity.ok("Xóa thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không thể xóa user này (có thể do dính khóa ngoại): " + e.getMessage());
        }
    }
}
