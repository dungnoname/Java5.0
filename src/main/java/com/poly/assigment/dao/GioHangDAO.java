package com.poly.assigment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.assigment.entity.GioHang;

@Repository
public interface GioHangDAO extends JpaRepository<GioHang, Integer> {
}
