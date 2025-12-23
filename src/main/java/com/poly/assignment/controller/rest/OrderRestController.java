package com.poly.assignment.controller.rest;

import com.poly.assignment.dao.*;
import com.poly.assignment.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderRestController {

    @Autowired
    HoaDonDAO hoaDonDAO;
    @Autowired
    ChiTietHoaDonDAO chiTietHoaDonDAO;
    @Autowired
    GioHangDAO gioHangDAO;
    @Autowired
    UserDAO userDAO;
    @Autowired
    TrangThaiDonHangDAO trangThaiDonHangDAO; // Bạn cần tạo DAO này nếu chưa có

    // --- Helper: Lấy User hiện tại ---
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userDAO.findByTenDangNhap(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // 1. ĐẶT HÀNG (CHECKOUT)
    @PostMapping("/checkout")
    @Transactional // Quan trọng: Đảm bảo thêm hóa đơn và xóa giỏ hàng cùng thành công hoặc cùng thất bại
    public ResponseEntity<?> checkout() {
        User user = getCurrentUser();

        // 1. Lấy giỏ hàng
        List<GioHang> cartItems = gioHangDAO.findByUser(user);
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("Giỏ hàng trống!");
        }

        // 2. Tạo Hóa Đơn mới
        HoaDon order = new HoaDon();
        order.setNguoiDung(user);
        order.setNgayLap(LocalDateTime.now());
        // Trạng thái 0: Chờ xác nhận (như SQL bạn gửi)
        TrangThaiDonHang status = trangThaiDonHangDAO.findById(0)
                .orElseThrow(() -> new RuntimeException("Lỗi trạng thái đơn hàng"));
        order.setTrangThaiDonHang(status);

        HoaDon savedOrder = hoaDonDAO.save(order);

        // 3. Chuyển Giỏ hàng sang Chi Tiết Hóa Đơn
        for (GioHang item : cartItems) {
            ChiTietHoaDon detail = new ChiTietHoaDon();
            detail.setHoaDon(savedOrder);
            detail.setSanPham(item.getSanPham());
            detail.setSoLuongBan(item.getSoLuong());
            detail.setDonGia(item.getSanPham().getDonGiaBan());

            chiTietHoaDonDAO.save(detail);

            // TODO: Ở đây bạn nên thêm logic trừ SoLuongTon của Sản Phẩm
            // SanPham sp = item.getSanPham();
            // sp.setSoLuongTon(sp.getSoLuongTon() - item.getSoLuong());
            // sanPhamDAO.save(sp);
        }

        // 4. Xóa sạch giỏ hàng
        gioHangDAO.deleteAllByUser(user); // Cần thêm @Transactional ở DAO hoặc Service

        return ResponseEntity.ok(savedOrder); // Trả về hóa đơn vừa tạo
    }

    // 2. XEM LỊCH SỬ ĐƠN HÀNG
    @GetMapping("/history")
    public ResponseEntity<List<HoaDon>> getOrderHistory() {
        User user = getCurrentUser();
        // Lấy tất cả hóa đơn của User này, sắp xếp giảm dần theo ngày (nếu muốn)
        List<HoaDon> orders = hoaDonDAO.findAll().stream()
                .filter(hd -> hd.getNguoiDung().getUserId().equals(user.getUserId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    // 3. XEM CHI TIẾT 1 ĐƠN HÀNG
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(@PathVariable Integer id) {
        User user = getCurrentUser();
        HoaDon order = hoaDonDAO.findById(id).orElse(null);

        if (order == null) return ResponseEntity.notFound().build();

        // Bảo mật: Chỉ xem được đơn của chính mình
        if (!order.getNguoiDung().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(403).body("Không có quyền xem đơn hàng này");
        }

        // Lấy danh sách chi tiết
        List<ChiTietHoaDon> details = chiTietHoaDonDAO.findByHoaDon(order);

        // Bạn có thể tạo DTO để trả về cả Info Hóa Đơn + List Chi Tiết
        // Hoặc trả về List chi tiết thôi tùy Frontend cần gì
        return ResponseEntity.ok(order);
    }

    // 4. HỦY ĐƠN HÀNG
    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Integer id) {
        User user = getCurrentUser();
        HoaDon order = hoaDonDAO.findById(id).orElse(null);

        if (order == null) return ResponseEntity.notFound().build();

        if (!order.getNguoiDung().getUserId().equals(user.getUserId())) {
            return ResponseEntity.status(403).body("Không có quyền hủy đơn này");
        }

        // Chỉ cho hủy khi đang ở trạng thái "Chờ xác nhận" (Mã 0)
        if (order.getTrangThaiDonHang().getMaTT() == 0) {
            TrangThaiDonHang cancelStatus = trangThaiDonHangDAO.findById(4).orElse(null); // Mã 4: Hủy
            order.setTrangThaiDonHang(cancelStatus);
            hoaDonDAO.save(order);
            return ResponseEntity.ok("Đã hủy đơn hàng");
        } else {
            return ResponseEntity.badRequest().body("Không thể hủy đơn hàng đang giao hoặc đã hoàn thành");
        }
    }
}