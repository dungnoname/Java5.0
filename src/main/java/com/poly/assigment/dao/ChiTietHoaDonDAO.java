package com.poly.assigment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.assigment.entity.ChiTietHoaDon;

@Repository
public interface ChiTietHoaDonDAO extends JpaRepository<ChiTietHoaDon, Integer> {
}
