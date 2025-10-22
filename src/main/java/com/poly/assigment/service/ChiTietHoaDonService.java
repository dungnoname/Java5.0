package com.poly.assigment.service;

import com.poly.assigment.entity.ChiTietHoaDon;
import java.util.List;
import java.util.Optional;

public interface ChiTietHoaDonService {
    List<ChiTietHoaDon> findAll();
    Optional<ChiTietHoaDon> findById(Integer id);
    ChiTietHoaDon save(ChiTietHoaDon cthd);
    void deleteById(Integer id);
}//
