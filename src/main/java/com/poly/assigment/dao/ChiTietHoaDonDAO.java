package com.poly.assigment.dao;

import com.poly.assigment.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.assigment.entity.ChiTietHoaDon;

import java.util.List;

@Repository
public interface ChiTietHoaDonDAO extends JpaRepository<ChiTietHoaDon, Integer> {
    List<ChiTietHoaDon> findByHoaDon(HoaDon hoaDon);
}
