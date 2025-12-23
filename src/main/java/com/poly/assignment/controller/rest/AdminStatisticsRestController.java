package com.poly.assignment.controller.rest;

import com.poly.assignment.dto.ThongKeDoanhThuLoaiHangDTO;
import com.poly.assignment.dto.TopKhachHangVipDTO;
import com.poly.assignment.service.ThongKeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/statistics")
@CrossOrigin(origins = "http://localhost:5173")
@PreAuthorize("hasRole('ADMIN')") // Chỉ Admin được xem thống kê
public class AdminStatisticsRestController {

    @Autowired
    private ThongKeService thongKeService;

    // 1. API Thống kê doanh thu theo loại hàng (Dùng để vẽ biểu đồ tròn/cột)
    // URL: GET /api/admin/statistics/revenue-by-category
    @GetMapping("/revenue-by-category")
    public ResponseEntity<?> getRevenueByCategory() {
        try {
            return ResponseEntity.ok(thongKeService.getDoanhThuByLoaiSanPham());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi Server: " + e.getMessage());
        }
    }

    // 2. API Top 10 Khách hàng VIP (Dùng để hiển thị bảng)
    // URL: GET /api/admin/statistics/top-customers
    @GetMapping("/top-customers")
    public ResponseEntity<?> getTopCustomers() {
        List<TopKhachHangVipDTO> data = thongKeService.getTop10KhachHangVip();
        return ResponseEntity.ok(data);
    }
}