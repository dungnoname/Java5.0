package com.poly.assigment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.assigment.entity.TrangThaiDonHang;

@Repository
public interface TrangThaiDonHangDAO extends JpaRepository<TrangThaiDonHang, Integer> {
}