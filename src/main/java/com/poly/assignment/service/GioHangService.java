package com.poly.assignment.service;

import com.poly.assignment.entity.GioHang;
import com.poly.assignment.entity.SanPham;
import com.poly.assignment.entity.User;

import java.util.List;
import java.util.Optional;

public interface GioHangService {
    List<GioHang> findAll();
    Optional<GioHang> findById(Integer id);
    void deleteById(Integer id);

    List<GioHang> getGioHangByUser(User user);
    GioHang findByUserAndSanPham(User user, SanPham sanPham);
    GioHang save(GioHang gioHang);
    void delete(Integer maGH);

    void deleteAll();
    void deleteAllByUser(User user);



}
