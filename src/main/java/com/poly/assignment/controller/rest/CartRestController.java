package com.poly.assignment.controller.rest;

import com.poly.assignment.dao.GioHangDAO;
import com.poly.assignment.dao.SanPhamDAO;
import com.poly.assignment.dao.UserDAO;
import com.poly.assignment.entity.GioHang;
import com.poly.assignment.entity.SanPham;
import com.poly.assignment.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173")
public class CartRestController {

    @Autowired
    GioHangDAO gioHangDAO;

    @Autowired
    SanPhamDAO sanPhamDAO;

    @Autowired
    UserDAO userDAO;

    // --- HÀM PHỤ: Lấy User đang đăng nhập từ Token ---
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        // Tìm user trong DB theo username từ Token
        return userDAO.findByTenDangNhap(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
    }

    // 1. Xem giỏ hàng
    @GetMapping("")
    public ResponseEntity<?> viewCart() {
        try {
            User currentUser = getCurrentUser();
            List<GioHang> cartItems = gioHangDAO.findByUser(currentUser);
            return ResponseEntity.ok(cartItems);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi lấy giỏ hàng: " + e.getMessage());
        }
    }

    // DTO để nhận dữ liệu thêm vào giỏ (Thay vì @RequestParam)
    public static class CartRequest {
        public Integer maSP;
        public Integer soLuong;
    }

    // 2. Thêm vào giỏ hàng
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartRequest req) {
        try {
            User currentUser = getCurrentUser();
            SanPham sp = sanPhamDAO.findById(req.maSP)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

            GioHang existingItem = gioHangDAO.findByUserAndSanPham(currentUser, sp);

            if (existingItem != null) {
                // Nếu đã có -> Cộng dồn số lượng
                existingItem.setSoLuong(existingItem.getSoLuong() + req.soLuong);
                existingItem.setNgayTao(LocalDateTime.now());
                gioHangDAO.save(existingItem);
            } else {
                // Nếu chưa có -> Tạo mới
                GioHang newItem = new GioHang();
                newItem.setUser(currentUser);
                newItem.setSanPham(sp);
                newItem.setSoLuong(req.soLuong);
                newItem.setNgayTao(LocalDateTime.now());
                gioHangDAO.save(newItem);
            }

            // Trả về thông báo thành công kèm số lượng hiện tại trong giỏ để update icon
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đã thêm vào giỏ hàng");
            response.put("totalItems", gioHangDAO.findByUser(currentUser).size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi thêm giỏ hàng: " + e.getMessage());
        }
    }

    // 3. Cập nhật số lượng (Khi bấm tăng giảm trong giỏ)
    @PutMapping("/update")
    public ResponseEntity<?> updateQuantity(@RequestBody CartRequest req) {
        User currentUser = getCurrentUser();
        SanPham sp = sanPhamDAO.findById(req.maSP).orElse(null);

        if (sp == null) return ResponseEntity.badRequest().body("Sản phẩm lỗi");

        GioHang item = gioHangDAO.findByUserAndSanPham(currentUser, sp);
        if (item != null) {
            // Đảm bảo số lượng ít nhất là 1
            int newQty = Math.max(1, req.soLuong);
            item.setSoLuong(newQty);
            gioHangDAO.save(item);
            return ResponseEntity.ok(item);
        }
        return ResponseEntity.notFound().build();
    }

    // 4. Xóa sản phẩm khỏi giỏ
    @DeleteMapping("/remove/{maSP}")
    public ResponseEntity<?> removeFromCart(@PathVariable Integer maSP) {
        User currentUser = getCurrentUser();
        SanPham sp = sanPhamDAO.findById(maSP).orElse(null);

        if (sp != null) {
            GioHang item = gioHangDAO.findByUserAndSanPham(currentUser, sp);
            if (item != null) {
                gioHangDAO.delete(item);
                return ResponseEntity.ok("Đã xóa sản phẩm");
            }
        }
        return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm trong giỏ");
    }

    // 5. Xóa toàn bộ giỏ (Dùng khi thanh toán xong)
    // Lưu ý: Cần thêm @Transactional ở Service hoặc DAO nếu dùng deleteBy...
    /* @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart() {
        User currentUser = getCurrentUser();
        gioHangDAO.deleteAllByUser(currentUser);
        return ResponseEntity.ok("Đã dọn sạch giỏ");
    }
    */
}