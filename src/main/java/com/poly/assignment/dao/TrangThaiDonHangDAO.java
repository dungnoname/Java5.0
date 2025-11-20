package com.poly.assignment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.assignment.entity.TrangThaiDonHang;

@Repository
public interface TrangThaiDonHangDAO extends JpaRepository<TrangThaiDonHang, Integer> {
}