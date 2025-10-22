package com.poly.assigment.service;

import com.poly.assigment.entity.DanhGia;
import com.poly.assigment.entity.HoaDon;
import com.poly.assigment.entity.SanPham;

import java.util.List;
import java.util.Optional;

public interface DanhGiaService {
    List<DanhGia> findAll();
    Optional<DanhGia> findById(Integer id);
    DanhGia save(DanhGia dg);
    void deleteById(Integer id);
    List<DanhGia> findBySanPham(SanPham sanPham);

    // Thêm phương thức theo HoaDon
    List<DanhGia> findByHoaDon(HoaDon hoaDon);

    // Theo sản phẩm
    List<DanhGia> findBySanPhamMaSP(Integer maSP);
}
