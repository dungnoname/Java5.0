package com.poly.assignment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.assignment.entity.LoaiSanPham;

@Repository
public interface LoaiSanPhamDAO extends JpaRepository<LoaiSanPham, Integer> {
}
