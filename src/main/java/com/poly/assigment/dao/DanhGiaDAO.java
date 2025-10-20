package com.poly.assigment.dao;

import com.poly.assigment.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.assigment.entity.DanhGia;

import java.util.List;

@Repository
public interface DanhGiaDAO extends JpaRepository<DanhGia, Integer> {
    List<DanhGia> findBySanPham(SanPham sanPham);
}
