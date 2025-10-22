package com.poly.assigment.controller.crud;

import com.poly.assigment.entity.Hang;
import com.poly.assigment.service.HangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/hang") // Đặt base path cho controller này
public class HangController {

    @Autowired
    private HangService hangService;

    // Hiển thị danh sách các hãng
    // và form thêm mới/chỉnh sửa
    @GetMapping
    public String hienThiDanhSachHang(Model model) {
        List<Hang> listHang = hangService.findAll();
        model.addAttribute("listHang", listHang);
        // Dùng một đối tượng Hang trống để phục vụ cho form thêm mới
        model.addAttribute("hang", new Hang()); // Đối tượng này sẽ được dùng cho form
        return "CRUD/Hang/index"; // Tên file Thymeleaf: resources/templates/CRUD/Hang/index.html
    }

    // Xử lý thêm mới hoặc cập nhật hãng
    @PostMapping("/save")
    public String saveHang(@ModelAttribute("hang") Hang hang) {
        hangService.save(hang);
        return "redirect:/admin/hang"; // Redirect về trang danh sách sau khi lưu
    }

    // Load dữ liệu của một hãng vào form để chỉnh sửa
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Optional<Hang> hangOptional = hangService.findById(id);
        if (hangOptional.isPresent()) {
            model.addAttribute("hang", hangOptional.get()); // Đối tượng Hang cần chỉnh sửa
        } else {
            // Nếu không tìm thấy, tạo một đối tượng Hang mới để form không bị lỗi
            model.addAttribute("hang", new Hang());
            // Có thể thêm thông báo lỗi nếu muốn
        }
        List<Hang> listHang = hangService.findAll(); // Lấy lại danh sách để hiển thị bảng
        model.addAttribute("listHang", listHang);
        return "CRUD/Hang/index"; // Trả về cùng trang index
    }


    // Xóa hãng theo ID
    @GetMapping("/delete/{id}")
    public String deleteHang(@PathVariable("id") Integer id) {
        hangService.deleteById(id);
        return "redirect:/admin/hang"; // Redirect về trang danh sách sau khi xóa
    }
}