package com.poly.assignment.service;

import com.poly.assignment.entity.LoaiSanPham;
import java.util.List;
import java.util.Optional;

public interface LoaiSanPhamService {
    List<LoaiSanPham> findAll();
    Optional<LoaiSanPham> findById(Integer id);
    LoaiSanPham save(LoaiSanPham loai);
    void deleteById(Integer id);
}
