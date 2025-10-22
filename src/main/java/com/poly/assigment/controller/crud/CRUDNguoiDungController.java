package com.poly.assigment.controller.crud;

import com.poly.assigment.entity.Role;
import com.poly.assigment.entity.User;
import com.poly.assigment.service.RoleService;
import com.poly.assigment.service.UserService;
import jakarta.validation.Valid; // Import @Valid
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Import BindingResult
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users")
public class CRUDNguoiDungController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping
    public String listUsers(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userService.findAll(pageable);

        model.addAttribute("userPage", userPage);
        model.addAttribute("users", userPage.getContent());

        if (!model.containsAttribute("user")) { // Chỉ tạo User mới nếu không có đối tượng user nào từ redirect (do lỗi validation)
            model.addAttribute("user", new User());
        }

        List<Role> roles = roleService.findAll();
        model.addAttribute("roles", roles);

        return "CRUD/QuanLyNguoiDung";
    }

    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") User user, // Thêm @Valid
                           BindingResult bindingResult, // Thêm BindingResult để bắt lỗi validation
                           RedirectAttributes redirectAttributes,
                           Model model, // Thêm Model để truyền lại dữ liệu nếu có lỗi
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size) {

        // *************************************************************
        // 1. Xử lý lỗi validation từ @Valid trong Entity
        // *************************************************************
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng kiểm tra lại thông tin nhập liệu.");
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult); // Truyền lỗi BindingResult
            redirectAttributes.addFlashAttribute("user", user); // Truyền lại đối tượng user để giữ dữ liệu trên form
            return "redirect:/admin/users";
        }

        try {
            // *************************************************************
            // 2. Kiểm tra trùng lặp Email và Tên Đăng Nhập
            // *************************************************************
            Optional<User> existingEmailUser = userService.findByEmail(user.getEmail());
            if (existingEmailUser.isPresent() && (user.getUserId() == null || !existingEmailUser.get().getUserId().equals(user.getUserId()))) {
                redirectAttributes.addFlashAttribute("errorMessage", "Email đã tồn tại.");
                redirectAttributes.addFlashAttribute("user", user);
                return "redirect:/admin/users";
            }

            Optional<User> existingUsernameUser = userService.findByTenDangNhap(user.getTenDangNhap());
            if (existingUsernameUser.isPresent() && (user.getUserId() == null || !existingUsernameUser.get().getUserId().equals(user.getUserId()))) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
                redirectAttributes.addFlashAttribute("user", user);
                return "redirect:/admin/users";
            }

            // *************************************************************
            // 3. Xử lý Role
            // *************************************************************
            if (user.getRole() != null && user.getRole().getRoleId() != null) {
                Optional<Role> selectedRole = roleService.findById(user.getRole().getRoleId());
                if (selectedRole.isPresent()) {
                    user.setRole(selectedRole.get());
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Vai trò không hợp lệ.");
                    redirectAttributes.addFlashAttribute("user", user);
                    return "redirect:/admin/users";
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn vai trò cho người dùng.");
                redirectAttributes.addFlashAttribute("user", user);
                return "redirect:/admin/users";
            }

            // *************************************************************
            // 4. Xử lý Mật khẩu
            // *************************************************************
            if (user.getUserId() == null) { // Thêm mới người dùng
                if (user.getMatKhau() == null || user.getMatKhau().isEmpty()) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu là bắt buộc khi tạo người dùng mới.");
                    redirectAttributes.addFlashAttribute("user", user);
                    return "redirect:/admin/users";
                }
                // TODO: Mã hóa mật khẩu ở đây: user.setMatKhau(passwordEncoder.encode(user.getMatKhau()));
            } else { // Cập nhật người dùng
                Optional<User> existingUserOptional = userService.findById(user.getUserId());
                if (existingUserOptional.isPresent()) {
                    User existingUser = existingUserOptional.get();
                    if (user.getMatKhau() == null || user.getMatKhau().isEmpty()) {
                        // Nếu mật khẩu trong form trống, giữ nguyên mật khẩu cũ
                        user.setMatKhau(existingUser.getMatKhau());
                    } else {
                        // Nếu có nhập mật khẩu mới, thì mã hóa và cập nhật
                        // TODO: user.setMatKhau(passwordEncoder.encode(user.getMatKhau()));
                    }
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng để cập nhật.");
                    redirectAttributes.addFlashAttribute("user", user);
                    return "redirect:/admin/users";
                }
            }

            userService.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi lưu người dùng: " + e.getMessage());
            redirectAttributes.addFlashAttribute("user", user);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size) {

        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            List<Role> roles = roleService.findAll();
            model.addAttribute("roles", roles);

            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = userService.findAll(pageable);
            model.addAttribute("userPage", userPage);
            model.addAttribute("users", userPage.getContent());

            return "CRUD/QuanLyNguoiDung";
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy người dùng có ID: " + id);
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi xóa người dùng: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}//