package com.poly.assigment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.poly.assigment.entity.DanhGia;

@Repository
public interface DanhGiaDAO extends JpaRepository<DanhGia, Integer> {
}
