package com.poly.assigment.service;

import com.poly.assigment.entity.SanPham;
import java.util.List;
import java.util.Optional;

public interface SanPhamService {
    List<SanPham> findAll();
    Optional<SanPham> findById(Integer id);
    SanPham save(SanPham sp);
    void deleteById(Integer id);
}
