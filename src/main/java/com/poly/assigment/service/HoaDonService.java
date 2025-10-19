package com.poly.assigment.service;

import com.poly.assigment.entity.HoaDon;
import java.util.List;
import java.util.Optional;

public interface HoaDonService {
    List<HoaDon> findAll();
    Optional<HoaDon> findById(Integer id);
    HoaDon save(HoaDon hd);
    void deleteById(Integer id);
}
