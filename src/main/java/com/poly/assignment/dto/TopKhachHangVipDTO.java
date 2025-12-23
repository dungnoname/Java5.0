package com.poly.assignment.dto;

import java.time.LocalDateTime;

public interface TopKhachHangVipDTO {
    String getTenKhachHang();
    Double getTongTienDaMua();
    LocalDateTime getNgayMuaDauTien();
    LocalDateTime getNgayMuaSauCung();
}