package com.poly.assigment.service;

import com.poly.assigment.entity.TrangThaiDonHang;
import java.util.List;
import java.util.Optional;

public interface TrangThaiDonHangService {
    List<TrangThaiDonHang> findAll();
    Optional<TrangThaiDonHang> findById(Integer id);
    TrangThaiDonHang save(TrangThaiDonHang tt);
    void deleteById(Integer id);
}
