package com.poly.assignment.controller.rest;

import com.poly.assignment.entity.SanPham;
import com.poly.assignment.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.poly.assignment.service.LoaiSanPhamService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173") // Cho phép Vue truy cập
public class ProductRestController {

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private LoaiSanPhamService loaiSanPhamService;

    // 1. Lấy toàn bộ sản phẩm
    @GetMapping("")
    public ResponseEntity<List<SanPham>> getAllProducts() {
        List<SanPham> list = sanPhamService.findAll();
        return ResponseEntity.ok(list);
    }

    // 2. Lấy chi tiết 1 sản phẩm
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductDetail(@PathVariable("id") Integer id) {
        Optional<SanPham> product = sanPhamService.findById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 3. Lọc sản phẩm (Theo Loại hoặc Giá)
    @GetMapping("/filter")
    public ResponseEntity<List<SanPham>> filterProducts(
            @RequestParam(required = false) Integer maLoai,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        // Gọi hàm tìm kiếm trong Service của bạn
        // Lưu ý: Bạn cần đảm bảo sanPhamService.findByLoaiVaGia đã được viết đúng
        List<SanPham> list = sanPhamService.findByLoaiVaGia(maLoai, minPrice, maxPrice);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // <--- 2. GẮN LUẬT Ở ĐÂY
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        try {
            sanPhamService.deleteById(id); // Giả sử service có hàm delete
            return ResponseEntity.ok("Đã xóa sản phẩm thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xóa: " + e.getMessage());
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(loaiSanPhamService.findAll());
    }
}