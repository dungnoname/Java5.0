package com.poly.assigment.controller.crud;

import com.poly.assigment.entity.User;
import com.poly.assigment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/users") // Base URL cho phần quản lý người dùng
public class NguoiDungController {

    @Autowired
    private UserService userService;

    // Hiển thị danh sách người dùng
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("user", new User()); // Để phục vụ form thêm mới/sửa
        return "CRUD/QuanLyNguoiDung"; // Trỏ đến file HTML
    }

    // Xử lý thêm người dùng mới (POST)
    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user) {
        userService.save(user);
        return "redirect:/admin/users"; // Redirect về trang danh sách sau khi lưu
    }

    // Hiển thị form sửa người dùng theo ID
    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable("id") Integer id, Model model) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            model.addAttribute("users", userService.findAll()); // Vẫn cần danh sách để hiển thị bảng
            return "CRUD/QuanLyNguoiDung";
        }
        return "redirect:/admin/users"; // Nếu không tìm thấy, redirect về trang danh sách
    }

    // Xóa người dùng theo ID
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        userService.deleteById(id);
        return "redirect:/admin/users"; // Redirect về trang danh sách sau khi xóa
    }
}