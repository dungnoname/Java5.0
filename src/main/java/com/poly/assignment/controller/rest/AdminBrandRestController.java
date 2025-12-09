package com.poly.assignment.controller.rest;

import com.poly.assignment.entity.Hang;
import com.poly.assignment.service.HangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin/brands")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được vào
public class AdminBrandRestController {

    @Autowired
    private HangService hangService;

    // 1. Lấy danh sách Hãng
    @GetMapping
    public ResponseEntity<?> getAllBrands() {
        return ResponseEntity.ok(hangService.findAll());
    }

    // 2. Lấy chi tiết 1 Hãng (Để hiển thị lên modal sửa)
    @GetMapping("/{id}")
    public ResponseEntity<?> getBrandById(@PathVariable Integer id) {
        Optional<Hang> hang = hangService.findById(id);
        if (hang.isPresent()) {
            return ResponseEntity.ok(hang.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 3. Thêm mới Hãng
    @PostMapping
    public ResponseEntity<?> createBrand(@RequestBody Hang hang) {
        // Validate đơn giản
        if (hang.getTenHang() == null || hang.getTenHang().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên hãng không được để trống!");
        }

        try {
            // Set ID null để đảm bảo insert mới (tránh trường hợp frontend gửi ID linh tinh)
            hang.setMaHang(null);

            Hang savedHang = hangService.save(hang);
            return ResponseEntity.ok(savedHang);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi thêm hãng: " + e.getMessage());
        }
    }

    // 4. Cập nhật Hãng
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBrand(@PathVariable Integer id, @RequestBody Hang hang) {
        Optional<Hang> existingOpt = hangService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (hang.getTenHang() == null || hang.getTenHang().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên hãng không được để trống!");
        }

        try {
            Hang existingHang = existingOpt.get();
            // Cập nhật thông tin
            existingHang.setTenHang(hang.getTenHang());

            // Lưu lại
            hangService.save(existingHang);
            return ResponseEntity.ok(existingHang);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi cập nhật: " + e.getMessage());
        }
    }

    // 5. Xóa Hãng
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBrand(@PathVariable Integer id) {
        if (!hangService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        try {
            hangService.deleteById(id);
            return ResponseEntity.ok("Xóa hãng thành công!");
        } catch (Exception e) {
            // Lỗi này xảy ra khi Hãng đang có sản phẩm (Ràng buộc khóa ngoại)
            return ResponseEntity.badRequest().body("Không thể xóa hãng này vì đang có sản phẩm thuộc hãng!");
        }
    }
}