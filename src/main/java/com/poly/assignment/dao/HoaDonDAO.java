package com.poly.assignment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.assignment.entity.HoaDon;

@Repository
public interface HoaDonDAO extends JpaRepository<HoaDon, Integer> {
}
