package com.poly.assignment.controller;

import com.poly.assignment.entity.SanPham;
import com.poly.assignment.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private SanPhamService sanPhamService;

    @RequestMapping("/")
    public String home(Model model) {

        // Lấy tất cả sản phẩm
        List<SanPham> allProducts = sanPhamService.findAll();

        // Tính giá gốc cho từng sản phẩm (tạm thời)
        allProducts.forEach(sp -> {
            double donGiaBan = sp.getDonGiaBan().doubleValue();
            double donGiaGoc = donGiaBan * 1.2;
            sp.setMoTa("donGiaGoc:" + donGiaGoc); // lưu tạm vào mô tả
        });

        // Nhóm sản phẩm
        List<SanPham> spMoi = allProducts.stream().limit(4).toList();
        List<SanPham> laptopGaming = allProducts.stream()
                .filter(sp -> sp.getLoaiSanPham().getMaLoai() == 2)
                .limit(4)
                .toList();
        List<SanPham> laptopVP = allProducts.stream()
                .filter(sp -> sp.getLoaiSanPham().getMaLoai() == 2)
                .skip(4)
                .limit(4)
                .toList();
        List<SanPham> banPhim = allProducts.stream()
                .filter(sp -> sp.getLoaiSanPham().getMaLoai() == 3)
                .limit(4)
                .toList();
        List<SanPham> chuot = allProducts.stream()
                .filter(sp -> sp.getLoaiSanPham().getMaLoai() == 6)
                .limit(4)
                .toList();

        // Truyền dữ liệu ra view
        model.addAttribute("spMoi", spMoi);
        model.addAttribute("laptopGaming", laptopGaming);
        model.addAttribute("laptopVP", laptopVP);
        model.addAttribute("banPhim", banPhim);
        model.addAttribute("chuot", chuot);

        return "home/index";
    }
}
