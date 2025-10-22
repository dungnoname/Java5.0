package com.poly.assigment.controller.crud;

import com.poly.assigment.entity.LoaiSanPham;
import com.poly.assigment.service.LoaiSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/crud/loai-san-pham")
public class LoaiSanPhamController {

    @Autowired
    private LoaiSanPhamService loaiSanPhamService;

    // Hiển thị danh sách loại sản phẩm và form thêm/sửa/xóa qua modal
    @GetMapping
    public String hienThi(Model model, @ModelAttribute("loaiSanPham") LoaiSanPham loaiSanPham) {
        List<LoaiSanPham> listLoaiSanPham = loaiSanPhamService.findAll();
        model.addAttribute("listLoaiSanPham", listLoaiSanPham);
        // loaiSanPham đã được thêm vào model qua @ModelAttribute cho form tạo mới/chỉnh sửa
        return "CRUD/QuanLyLoaiHang"; // Tên file Thymeleaf của bạn
    }

    // Xử lý thêm mới loại sản phẩm
    @PostMapping("/save") // Sử dụng chung cho cả thêm mới và cập nhật
    public String saveLoaiSanPham(@ModelAttribute("loaiSanPham") LoaiSanPham loaiSanPham, RedirectAttributes ra) {
        try {
            // Nếu maLoai là null hoặc 0, coi là thêm mới
            if (loaiSanPham.getMaLoai() == null || loaiSanPham.getMaLoai() == 0) {
                loaiSanPhamService.save(loaiSanPham);
                ra.addFlashAttribute("message", "Thêm loại sản phẩm thành công!");
                ra.addFlashAttribute("messageType", "success");
            } else { // Ngược lại, là cập nhật
                Optional<LoaiSanPham> existingLoaiSanPham = loaiSanPhamService.findById(loaiSanPham.getMaLoai());
                if (existingLoaiSanPham.isPresent()) {
                    loaiSanPhamService.save(loaiSanPham); // maLoai đã có trong đối tượng loaiSanPham
                    ra.addFlashAttribute("message", "Cập nhật loại sản phẩm thành công!");
                    ra.addFlashAttribute("messageType", "success");
                } else {
                    ra.addFlashAttribute("message", "Không tìm thấy loại sản phẩm để cập nhật.");
                    ra.addFlashAttribute("messageType", "error");
                }
            }
        } catch (Exception e) {
            ra.addFlashAttribute("message", "Thao tác thất bại: " + e.getMessage());
            ra.addFlashAttribute("messageType", "error");
        }
        return "redirect:/crud/loai-san-pham";
    }

    // Xử lý xóa loại sản phẩm
    @GetMapping("/delete/{maLoai}") // Có thể đổi thành @PostMapping nếu muốn an toàn hơn
    public String deleteLoaiSanPham(@PathVariable("maLoai") Integer maLoai, RedirectAttributes ra) {
        try {
            if (loaiSanPhamService.findById(maLoai).isPresent()) {
                loaiSanPhamService.deleteById(maLoai);
                ra.addFlashAttribute("message", "Xóa loại sản phẩm thành công!");
                ra.addFlashAttribute("messageType", "success");
            } else {
                ra.addFlashAttribute("message", "Không tìm thấy loại sản phẩm có mã " + maLoai);
                ra.addFlashAttribute("messageType", "error");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("message", "Xóa loại sản phẩm thất bại: " + e.getMessage());
            ra.addFlashAttribute("messageType", "error");
        }
        return "redirect:/crud/loai-san-pham";
    }
}