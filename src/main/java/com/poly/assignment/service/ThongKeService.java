package com.poly.assignment.service;

import com.poly.assignment.dto.ThongKeDoanhThuLoaiHangDTO;
import com.poly.assignment.dto.TopKhachHangVipDTO;

import java.util.List;

public interface ThongKeService {
    List<ThongKeDoanhThuLoaiHangDTO> getDoanhThuByLoaiSanPham();
    List<TopKhachHangVipDTO> getTop10KhachHangVip();
}//