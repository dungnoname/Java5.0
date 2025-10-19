package com.poly.assigment.service;

import com.poly.assigment.entity.GioHang;
import java.util.List;
import java.util.Optional;

public interface GioHangService {
    List<GioHang> findAll();
    Optional<GioHang> findById(Integer id);
    GioHang save(GioHang gh);
    void deleteById(Integer id);
}
