package com.poly.assigment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.assigment.entity.HoaDon;

@Repository
public interface HoaDonDAO extends JpaRepository<HoaDon, Integer> {
}
