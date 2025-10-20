package com.poly.assigment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

public class HomeController {

    @RequestMapping("/")
    public String home() {
        return "home/index"; // Trang chủ
    }

    @RequestMapping("/admin")
    public String adminHome() {
        return "CRUD/QuanLySanPham";
    }





//    @GetMapping("/")
//    public String home() {
//        return "home/index";
//    }
//
//    @GetMapping("/SanPham/list")
//    public String gioiThieu() {
//        return "home/danhMucSanPham";
//    }
//
//    @GetMapping("/SanPham/detail/{id}")
//    public String chiTietSanPham(@PathVariable("id") Integer id, Model model) {
//        model.addAttribute("productId", id);
//        return "home/chiTietSanPham";
//    }
//
//    @GetMapping("/GioHang/list")
//    public String gioHang() {
//        return "home/gioHang";
//    }
//
//    @GetMapping("/TaiKhoan")
//    public String taiKhoan() {
//        return "home/taiKhoan";
//    }
//
//    @GetMapping("/donHang")
//    public String donHang() {
//        return "home/donHang";
//    }
//
//    @GetMapping("/dang-ky")
//    public String dangKy() {
//        return "home/dangKy";
//    }
//
//    @GetMapping("/dang-nhap")
//    public String dangNhap() {
//        return "home/dangNhap";
//    }
}
