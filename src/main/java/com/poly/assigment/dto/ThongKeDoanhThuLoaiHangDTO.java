package com.poly.assigment.dto;

import java.math.BigDecimal;


public interface ThongKeDoanhThuLoaiHangDTO {
    String getTenLoai();
    BigDecimal getTongDoanhThu();
    Long getTongSoLuong();
    BigDecimal getGiaCaoNhat();
    BigDecimal getGiaThapNhat();
    BigDecimal getGiaTrungBinh();
}