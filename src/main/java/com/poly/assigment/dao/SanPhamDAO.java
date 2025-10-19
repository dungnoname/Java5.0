package com.poly.assigment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.assigment.entity.SanPham;

@Repository
public interface SanPhamDAO extends JpaRepository<SanPham, Integer> {
}