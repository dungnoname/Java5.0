package com.poly.assignment.controller.rest;

import com.poly.assignment.entity.Hang;
import com.poly.assignment.entity.LoaiSanPham;
import com.poly.assignment.entity.SanPham;
import com.poly.assignment.service.HangService;
import com.poly.assignment.service.LoaiSanPhamService;
import com.poly.assignment.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được thao tác
public class AdminProductRestController {

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private LoaiSanPhamService loaiSanPhamService;

    @Autowired
    private HangService hangService;

    // ==========================================
    // 1. CÁC API HỖ TRỢ (Lấy danh mục để chọn)
    // ==========================================

    // Lấy danh sách Loại sản phẩm (Cho dropdown)
    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(loaiSanPhamService.findAll());
    }

    // Lấy danh sách Hãng (Cho dropdown)
    @GetMapping("/brands")
    public ResponseEntity<?> getBrands() {
        return ResponseEntity.ok(hangService.findAll());
    }

    // ==========================================
    // 2. CRUD SẢN PHẨM
    // ==========================================

    // Lấy toàn bộ sản phẩm
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        // Nếu muốn phân trang, bạn có thể dùng Pageable như bên User
        // Ở đây mình trả về List để giống controller cũ của bạn
        return ResponseEntity.ok(sanPhamService.findAll());
    }

    // Lấy chi tiết 1 sản phẩm (Để hiển thị lên form sửa)
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        Optional<SanPham> sp = sanPhamService.findById(id);
        if (sp.isPresent()) {
            return ResponseEntity.ok(sp.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Thêm mới sản phẩm
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody SanPham sanPham) {
        try {
            // Validate cơ bản
            if (sanPham.getTenSP() == null || sanPham.getTenSP().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên sản phẩm không được để trống");
            }
            if (sanPham.getDonGiaBan() == null || sanPham.getDonGiaBan().doubleValue() < 0) {
                return ResponseEntity.badRequest().body("Giá bán không hợp lệ");
            }

            // Validate quan hệ (Loại & Hãng)
            if (sanPham.getLoaiSanPham() == null || sanPham.getLoaiSanPham().getMaLoai() == null) {
                return ResponseEntity.badRequest().body("Vui lòng chọn loại sản phẩm");
            }
            if (sanPham.getHang() == null || sanPham.getHang().getMaHang() == null) {
                return ResponseEntity.badRequest().body("Vui lòng chọn hãng");
            }

            // ngày tạo
            if (sanPham.getNgayTao() == null) {
                sanPham.setNgayTao(LocalDate.now());
            }

            // Mặc định trạng thái là true (Còn hàng/Kích hoạt) nếu null
            if (sanPham.getTrangThai() == null) {
                sanPham.setTrangThai(true);
            }

            SanPham savedSp = sanPhamService.save(sanPham);
            return ResponseEntity.ok(savedSp);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi thêm sản phẩm: " + e.getMessage());
        }
    }

    // Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id, @RequestBody SanPham sanPham) {
        Optional<SanPham> existingOpt = sanPhamService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SanPham existingSp = existingOpt.get();

        try {
            // Cập nhật các trường thông tin
            existingSp.setTenSP(sanPham.getTenSP());
            existingSp.setDonGiaBan(sanPham.getDonGiaBan());
            existingSp.setSoLuongTon(sanPham.getSoLuongTon());
            existingSp.setMoTa(sanPham.getMoTa());
            existingSp.setImgUrl(sanPham.getImgUrl()); // Đường dẫn ảnh
            existingSp.setTrangThai(sanPham.getTrangThai());

            // Cập nhật quan hệ
            if (sanPham.getLoaiSanPham() != null) {
                existingSp.setLoaiSanPham(sanPham.getLoaiSanPham());
            }
            if (sanPham.getHang() != null) {
                existingSp.setHang(sanPham.getHang());
            }

            SanPham updatedSp = sanPhamService.save(existingSp);
            return ResponseEntity.ok(updatedSp);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi cập nhật: " + e.getMessage());
        }
    }

    // Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        Optional<SanPham> sp = sanPhamService.findById(id);
        if (sp.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            sanPhamService.deleteById(id);
            return ResponseEntity.ok("Đã xóa sản phẩm thành công");
        } catch (Exception e) {
            // Thường lỗi do ràng buộc khóa ngoại (đã có trong đơn hàng)
            return ResponseEntity.badRequest().body("Không thể xóa sản phẩm này (đã có phát sinh đơn hàng).");
        }
    }
}