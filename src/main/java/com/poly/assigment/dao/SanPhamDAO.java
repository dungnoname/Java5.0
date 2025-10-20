package com.poly.assigment.dao;

import com.poly.assigment.entity.LoaiSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.assigment.entity.SanPham;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SanPhamDAO extends JpaRepository<SanPham, Integer> {
    List<SanPham> findByLoaiSanPham_MaLoai(Integer maLoai);
    List<SanPham> findByLoaiSanPham_MaLoaiAndDonGiaBanBetween(Integer maLoai, BigDecimal min, BigDecimal max);
}