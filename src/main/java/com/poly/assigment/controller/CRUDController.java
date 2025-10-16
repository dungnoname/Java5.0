package com.poly.assigment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CRUDController {

    @GetMapping("/crud/don-hang")
    public String quanLyDonHang() {
        return "CRUD/QuanLyDonHang";
    }

    @GetMapping("/crud/loai-hang")
    public String quanLyLoaiHang() {
        return "CRUD/QuanLyLoaiHang";
    }

    @GetMapping("/crud/san-pham")
    public String quanLySanPham() {
        return "CRUD/QuanLySanPham";
    }

    @GetMapping("/crud/nguoi-dung")
    public String quanLyNguoiDung() {
        return "CRUD/QuanLyNguoiDung";
    }

    @GetMapping("/crud/thong-ke")
    public String tongHopThongKe() {
        return "CRUD/TongHopThongKe";
    }
}
