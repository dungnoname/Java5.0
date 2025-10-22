package com.poly.assigment.service;

import com.poly.assigment.entity.HoaDon;
import com.poly.assigment.entity.LoaiSanPham;
import com.poly.assigment.entity.SanPham;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SanPhamService {
    List<SanPham> findAll();
    Optional<SanPham> findById(Integer id);
    SanPham save(SanPham sp);
    void deleteById(Integer id);

    List<SanPham> findByLoaiVaGia(Integer maLoai, BigDecimal minPrice, BigDecimal maxPrice);

    public List<SanPham> findByHoaDon(HoaDon hoaDon);
}
