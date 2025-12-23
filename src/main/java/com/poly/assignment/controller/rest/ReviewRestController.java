package com.poly.assignment.controller.rest;

import com.poly.assignment.dto.ReviewRequestDTO;
import com.poly.assignment.entity.*;
import com.poly.assignment.service.ChiTietHoaDonService;
import com.poly.assignment.service.DanhGiaService;
import com.poly.assignment.service.HoaDonService;
import com.poly.assignment.service.SanPhamService;
import com.poly.assignment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewRestController {

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private ChiTietHoaDonService chiTietHoaDonService;

    @Autowired
    private DanhGiaService danhGiaService;

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private UserService userService;

    // 1. Kiểm tra và Lấy danh sách sản phẩm cần đánh giá
    // URL: GET /api/reviews/form/{orderId}
    @GetMapping("/form/{orderId}")
    public ResponseEntity<?> getProductsToReview(@PathVariable Integer orderId) {
        // Lấy User hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        HoaDon hd = hoaDonService.findById(orderId).orElse(null);

        // A. Kiểm tra đơn hàng tồn tại
        if (hd == null) {
            return ResponseEntity.badRequest().body("Đơn hàng không tồn tại!");
        }

        // B. Bảo mật: Đơn hàng phải của chính người đang đăng nhập
        if (!hd.getNguoiDung().getTenDangNhap().equals(currentUsername)) {
            return ResponseEntity.status(403).body("Bạn không có quyền đánh giá đơn hàng này!");
        }

        // C. Kiểm tra trạng thái: Phải là Hoàn thành (3) mới được đánh giá
        if (hd.getTrangThaiDonHang().getMaTT() != 3) {
            return ResponseEntity.badRequest().body("Đơn hàng chưa hoàn thành, không thể đánh giá!");
        }

        // D. Trả về danh sách chi tiết (chứa sản phẩm) để Vue vẽ form
        List<ChiTietHoaDon> chiTietList = chiTietHoaDonService.findByHoaDon(hd);
        return ResponseEntity.ok(chiTietList);
    }

    // 2. Gửi đánh giá
    // URL: POST /api/reviews/submit/{orderId}
    @PostMapping("/submit/{orderId}")
    public ResponseEntity<?> submitReview(
            @PathVariable Integer orderId,
            @RequestBody List<ReviewRequestDTO> reviews // Nhận danh sách JSON
    ) {
        // Lấy User hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Optional<User> currentUserOpt = userService.findByTenDangNhap(currentUsername);

        HoaDon hd = hoaDonService.findById(orderId).orElse(null);

        // Validation lại (cho chắc chắn)
        if (hd == null || currentUserOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Lỗi dữ liệu!");
        }
        if (!hd.getNguoiDung().getTenDangNhap().equals(currentUsername)) {
            return ResponseEntity.status(403).body("Không có quyền!");
        }
        if (hd.getTrangThaiDonHang().getMaTT() != 3) {
            return ResponseEntity.badRequest().body("Đơn hàng chưa hoàn thành!");
        }

        try {
            // Duyệt qua từng đánh giá gửi lên
            for (ReviewRequestDTO req : reviews) {
                // Chỉ lưu nếu có số sao > 0
                if (req.getRating() != null && req.getRating() > 0) {
                    Optional<SanPham> spOpt = sanPhamService.findById(req.getProductId());

                    if (spOpt.isPresent()) {
                        DanhGia dg = new DanhGia();
                        dg.setSanPham(spOpt.get());
                        dg.setUser(currentUserOpt.get());
                        dg.setSoSao(req.getRating());
                        dg.setNoiDung(req.getComment());
                        dg.setNgayDanhGia(LocalDateTime.now());

                        // Lưu vào DB
                        danhGiaService.save(dg);
                    }
                }
            }

            // Có thể cập nhật trạng thái đơn hàng thành "Đã đánh giá" nếu cần
            // Để tránh user đánh giá đi đánh giá lại nhiều lần (Optional logic)

            return ResponseEntity.ok("Cảm ơn bạn đã đánh giá sản phẩm!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi lưu đánh giá: " + e.getMessage());
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProductReviews(@PathVariable Integer productId) {
        SanPham sp = sanPhamService.findById(productId).orElse(null);
        if (sp == null) {
            return ResponseEntity.notFound().build();
        }

        // Lấy list đánh giá từ DB (Bạn cần viết hàm này trong DanhGiaService/DAO)
        // Nếu dùng JPA cơ bản thì: danhGiaDAO.findBySanPham(sp);
        List<DanhGia> list = danhGiaService.findBySanPham(sp);

        return ResponseEntity.ok(list);
    }
}