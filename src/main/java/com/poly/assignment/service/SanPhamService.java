package com.poly.assignment.service;

import com.poly.assignment.entity.HoaDon;
import com.poly.assignment.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    Page<SanPham> findAllByLoaiSanPham_MaLoai(Integer maLoai, Pageable pageable);

    Page<SanPham> findByLoaiSanPham_MaLoaiAndHang_MaHang(Integer maLoai, Integer maHang, Pageable pageable);

}
