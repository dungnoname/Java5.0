package com.poly.assigment.service;

import com.poly.assigment.dto.ThongKeDoanhThuLoaiHangDTO;
import com.poly.assigment.dto.TopKhachHangVipDTO;

import java.util.List;

public interface ThongKeService {
    List<ThongKeDoanhThuLoaiHangDTO> getDoanhThuByLoaiSanPham();
    List<TopKhachHangVipDTO> getTop10KhachHangVip();
}//