package com.poly.assigment.dao;

import com.poly.assigment.entity.SanPham;
import com.poly.assigment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.assigment.entity.GioHang;

import java.util.List;
import java.util.Optional;

@Repository
public interface GioHangDAO extends JpaRepository<GioHang, Integer> {
    List<GioHang> findByUser(User user);
    GioHang findByUserAndSanPham(User user, SanPham sanPham);

    void deleteAllByUser(User user);


}
