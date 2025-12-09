package com.poly.assignment.controller.rest;

import com.poly.assignment.entity.HoaDon;
import com.poly.assignment.entity.TrangThaiDonHang;
import com.poly.assignment.entity.User;
import com.poly.assignment.service.HoaDonService;
import com.poly.assignment.service.TrangThaiDonHangService;
import com.poly.assignment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được vào
public class AdminOrderRestController {

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private TrangThaiDonHangService trangThaiDonHangService;

    @Autowired
    private UserService userService;

    // ==========================================
    // 1. API HỖ TRỢ (Lấy danh sách Trạng thái)
    // ==========================================
    @GetMapping("/statuses")
    public ResponseEntity<?> getAllStatuses() {
        return ResponseEntity.ok(trangThaiDonHangService.findAll());
    }

    // ==========================================
    // 2. QUẢN LÝ ĐƠN HÀNG
    // ==========================================

    // Lấy danh sách tất cả đơn hàng
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        // Mặc định sắp xếp đơn mới nhất lên đầu (nếu Service bạn chưa làm thì thôi)
        List<HoaDon> list = hoaDonService.findAll();
        // Sắp xếp giảm dần theo ngày (Optional - xử lý List tại chỗ)
        list.sort((o1, o2) -> o2.getNgayLap().compareTo(o1.getNgayLap()));

        return ResponseEntity.ok(list);
    }

    // Lấy chi tiết 1 đơn hàng (Kèm theo danh sách sản phẩm bên trong)
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Integer id) {
        Optional<HoaDon> hd = hoaDonService.findById(id);
        if (hd.isPresent()) {
            return ResponseEntity.ok(hd.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Cập nhật Trạng thái đơn hàng
    // Vue gửi lên: { "trangThaiId": 2 }
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> requestBody // Dùng Map để hứng JSON đơn giản
    ) {
        Optional<HoaDon> hdOpt = hoaDonService.findById(id);
        if (hdOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Integer newStatusId = requestBody.get("trangThaiId");
        Optional<TrangThaiDonHang> ttOpt = trangThaiDonHangService.findById(newStatusId);

        if (ttOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Trạng thái không hợp lệ");
        }

        try {
            HoaDon hoaDon = hdOpt.get();

            // 1. Cập nhật trạng thái mới
            hoaDon.setTrangThaiDonHang(ttOpt.get());

            // 2. Gán nhân viên xử lý (Lấy từ Token của Admin đang đăng nhập)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = auth.getName();
            Optional<User> staff = userService.findByTenDangNhap(currentUsername);

            if (staff.isPresent()) {
                hoaDon.setNhanVien(staff.get()); // Lưu vết người sửa
            }

            // 3. Lưu lại
            hoaDonService.save(hoaDon);
            return ResponseEntity.ok(hoaDon);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi cập nhật: " + e.getMessage());
        }
    }

    // Xóa đơn hàng (Thường ít dùng, chủ yếu là Hủy đơn)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Integer id) {
        if (!hoaDonService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        try {
            hoaDonService.deleteById(id);
            return ResponseEntity.ok("Xóa đơn hàng thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xóa (có thể do ràng buộc khóa ngoại): " + e.getMessage());
        }
    }
}