package com.poly.assigment.service;

import com.poly.assigment.entity.ChiTietHoaDon;
import com.poly.assigment.entity.HoaDon;

import java.util.List;
import java.util.Optional;

public interface ChiTietHoaDonService {
    List<ChiTietHoaDon> findAll();
    Optional<ChiTietHoaDon> findById(Integer id);
    ChiTietHoaDon save(ChiTietHoaDon cthd);
    void deleteById(Integer id);
<<<<<<< HEAD
}//
=======

    List<ChiTietHoaDon> findByHoaDon(HoaDon hoaDon);
}
>>>>>>> 9e5eebe9de1efe62b6fe09e3b1691030c06bacd3
