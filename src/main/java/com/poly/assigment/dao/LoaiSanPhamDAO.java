package com.poly.assigment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.assigment.entity.LoaiSanPham;

@Repository
public interface LoaiSanPhamDAO extends JpaRepository<LoaiSanPham, Integer> {
}
