package com.poly.assigment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TopKhachHangVipDTO {
    String getTenKhachHang();
    BigDecimal getTongTienDaMua();
    LocalDate getNgayMuaDauTien();
    LocalDate getNgayMuaSauCung();
}