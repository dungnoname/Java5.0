package com.poly.assignment.service;

import com.poly.assignment.entity.TrangThaiDonHang;
import java.util.List;
import java.util.Optional;

public interface TrangThaiDonHangService {
    List<TrangThaiDonHang> findAll();
    Optional<TrangThaiDonHang> findById(Integer id);
    TrangThaiDonHang save(TrangThaiDonHang tt);
    void deleteById(Integer id);
}
