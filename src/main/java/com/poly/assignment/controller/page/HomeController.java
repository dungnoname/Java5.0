package com.poly.assignment.controller.page;

import com.poly.assignment.entity.SanPham;
import com.poly.assignment.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Cho phép Vue gọi API thoải mái
public class HomeController {

    @Autowired
    private SanPhamService sanPhamService;

    // API 1: Lấy dữ liệu trang chủ (Lọc theo 3 loại)
    @GetMapping("/home")
    public Map<String, List<SanPham>> getHomeData() {
        List<SanPham> allProducts = sanPhamService.findAll();
        Map<String, List<SanPham>> response = new HashMap<>();

        // 1. Lọc Bàn phím (Tìm chữ "Phím" hoặc "Key" trong tên loại)
        List<SanPham> banPhim = allProducts.stream()
                .filter(p -> p.getLoaiSanPham() != null &&
                        (p.getLoaiSanPham().getTenLoai().toLowerCase().contains("phím") ||
                                p.getLoaiSanPham().getTenLoai().toLowerCase().contains("key")))
                .limit(20).collect(Collectors.toList());

        // 2. Lọc Laptop
        List<SanPham> laptop = allProducts.stream()
                .filter(p -> p.getLoaiSanPham() != null &&
                        p.getLoaiSanPham().getTenLoai().toLowerCase().contains("laptop"))
                .limit(20).collect(Collectors.toList());

        // 3. Lọc Chuột
        List<SanPham> chuot = allProducts.stream()
                .filter(p -> p.getLoaiSanPham() != null &&
                        (p.getLoaiSanPham().getTenLoai().toLowerCase().contains("chuột") ||
                                p.getLoaiSanPham().getTenLoai().toLowerCase().contains("mouse")))
                .limit(20).collect(Collectors.toList());

        response.put("banPhim", banPhim);
        response.put("laptop", laptop);
        response.put("chuot", chuot);

        return response;
    }

    // API 2: Lấy chi tiết 1 sản phẩm theo ID (Vue sẽ gọi cái này khi bấm vào ảnh)
    @GetMapping("/product/{id}")
    public ResponseEntity<SanPham> getProductDetail(@PathVariable Integer id) {
        // SỬA LẠI DÒNG NÀY: Thêm .orElse(null)
        SanPham sp = sanPhamService.findById(id).orElse(null);

        if (sp != null) {
            return ResponseEntity.ok(sp);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}