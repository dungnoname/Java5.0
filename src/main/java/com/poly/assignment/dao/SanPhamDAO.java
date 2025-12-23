package com.poly.assignment.dao;

import com.poly.assignment.entity.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.assignment.entity.SanPham;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SanPhamDAO extends JpaRepository<SanPham, Integer> {
    List<SanPham> findByLoaiSanPham_MaLoai(Integer maLoai);
    List<SanPham> findByLoaiSanPham_MaLoaiAndDonGiaBanBetween(Integer maLoai, BigDecimal min, BigDecimal max);
    List<SanPham> findByChiTietHoaDonList_HoaDon(HoaDon hoaDon);

    Page<SanPham> findAllByLoaiSanPham_MaLoai(Integer maLoai, Pageable pageable);

    Page<SanPham> findByLoaiSanPham_MaLoaiAndHang_MaHang(Integer maLoai, Integer maHang, Pageable pageable);
}