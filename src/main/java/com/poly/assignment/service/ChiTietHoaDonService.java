package com.poly.assignment.service;

import com.poly.assignment.entity.ChiTietHoaDon;
import com.poly.assignment.entity.HoaDon;

import java.util.List;
import java.util.Optional;

public interface ChiTietHoaDonService {
    List<ChiTietHoaDon> findAll();
    Optional<ChiTietHoaDon> findById(Integer id);
    ChiTietHoaDon save(ChiTietHoaDon cthd);
    void deleteById(Integer id);

    List<ChiTietHoaDon> findByHoaDon(HoaDon hoaDon);

}//
