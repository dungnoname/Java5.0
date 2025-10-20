package com.poly.assigment.controller;

import com.poly.assigment.entity.DanhGia;
import com.poly.assigment.entity.LoaiSanPham;
import com.poly.assigment.entity.SanPham;
import com.poly.assigment.service.DanhGiaService;
import com.poly.assigment.service.LoaiSanPhamService;
import com.poly.assigment.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/SanPham")
public class SanPhamController {

    @Autowired
    private SanPhamService sanPhamService;

    @Autowired
    private LoaiSanPhamService loaiSanPhamService;

    @Autowired
    private DanhGiaService danhGiaService;

    @GetMapping("/")
    public String danhSachSanPham(Model model) {
        List<SanPham> products = sanPhamService.findAll();
        List<LoaiSanPham> categories = loaiSanPhamService.findAll();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "home/DanhMucSanPham";
    }

    @GetMapping("/detail/{id}")
    public String chiTietSanPham(@PathVariable("id") Integer id, Model model) {
        SanPham product = sanPhamService.findById(id).orElse(null);
        if (product == null) {
            // nếu không tìm thấy, có thể chuyển hướng về danh sách
            return "redirect:/SanPham";
        }
        List<DanhGia> nhungDanhGia = danhGiaService.findBySanPham(product);

        // tính điểm trung bình (số sao trung bình)
        double avgRating = nhungDanhGia.stream()
                .mapToInt(DanhGia::getSoSao)
                .average()
                .orElse(0.0);

        double donGia = product.getDonGiaBan().doubleValue();
        double donGiaGoc = donGia * 1.2;

        model.addAttribute("donGiaGoc", donGiaGoc);
        model.addAttribute("product", product);
        model.addAttribute("nhungDanhGia", nhungDanhGia);
        model.addAttribute("avgRating", avgRating);

        model.addAttribute("product", product);
        return "home/ChiTietSanPham";
    }

    @GetMapping("/loc")
    public String locSanPham(
            @RequestParam(required = false) Integer maLoai,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Model model) {

        List<LoaiSanPham> categories = loaiSanPhamService.findAll();

        List<SanPham> products = sanPhamService.findByLoaiVaGia(maLoai, minPrice, maxPrice);

        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("selectedLoai", maLoai);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "home/DanhMucSanPham";
    }


}
