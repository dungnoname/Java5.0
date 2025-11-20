package com.poly.assignment.dao;

import com.poly.assignment.entity.SanPham;
import com.poly.assignment.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.assignment.entity.GioHang;

import java.util.List;

@Repository
public interface GioHangDAO extends JpaRepository<GioHang, Integer> {
    List<GioHang> findByUser(User user);
    GioHang findByUserAndSanPham(User user, SanPham sanPham);

    @Transactional
    void deleteAllByUser(User user);


}
