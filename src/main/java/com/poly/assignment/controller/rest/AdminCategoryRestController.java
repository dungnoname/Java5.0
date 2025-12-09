package com.poly.assignment.controller.rest;

import com.poly.assignment.entity.LoaiSanPham;
import com.poly.assignment.service.LoaiSanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin/categories")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được vào
public class AdminCategoryRestController {

    @Autowired
    private LoaiSanPhamService loaiSanPhamService;

    // 1. Lấy danh sách Loại sản phẩm
    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(loaiSanPhamService.findAll());
    }

    // 2. Lấy chi tiết 1 Loại (để hiển thị lên form sửa)
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Integer id) {
        Optional<LoaiSanPham> loai = loaiSanPhamService.findById(id);
        if (loai.isPresent()) {
            return ResponseEntity.ok(loai.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 3. Thêm mới Loại sản phẩm
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody LoaiSanPham loaiSanPham) {
        // Validate đơn giản
        if (loaiSanPham.getTenLoai() == null || loaiSanPham.getTenLoai().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên loại không được để trống!");
        }

        try {
            // Vì MaLoai thường là tự tăng (Identity), nên ta set null để đảm bảo insert mới
            loaiSanPham.setMaLoai(null);

            LoaiSanPham savedLoai = loaiSanPhamService.save(loaiSanPham);
            return ResponseEntity.ok(savedLoai);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi thêm: " + e.getMessage());
        }
    }

    // 4. Cập nhật Loại sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @RequestBody LoaiSanPham loaiSanPham) {
        Optional<LoaiSanPham> existingOpt = loaiSanPhamService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (loaiSanPham.getTenLoai() == null || loaiSanPham.getTenLoai().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên loại không được để trống!");
        }

        try {
            LoaiSanPham existingLoai = existingOpt.get();
            // Cập nhật tên
            existingLoai.setTenLoai(loaiSanPham.getTenLoai());

            // Lưu lại
            loaiSanPhamService.save(existingLoai);
            return ResponseEntity.ok(existingLoai);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi cập nhật: " + e.getMessage());
        }
    }

    // 5. Xóa Loại sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        if (!loaiSanPhamService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        try {
            loaiSanPhamService.deleteById(id);
            return ResponseEntity.ok("Xóa thành công!");
        } catch (Exception e) {
            // Lỗi phổ biến: DataIntegrityViolationException
            // Xảy ra khi có Sản phẩm đang thuộc Loại này -> SQL không cho xóa
            return ResponseEntity.badRequest().body("Không thể xóa loại này vì đang có sản phẩm sử dụng nó!");
        }
    }
}