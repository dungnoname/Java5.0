package com.poly.assignment.dao;

import com.poly.assignment.dto.ThongKeDoanhThuLoaiHangDTO;
import com.poly.assignment.dto.TopKhachHangVipDTO;
import com.poly.assignment.entity.ChiTietHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThongKeDAO extends JpaRepository<ChiTietHoaDon, Integer> {

    @Query(value = "EXEC sp_ThongKeDoanhThu", nativeQuery = true)
    List<ThongKeDoanhThuLoaiHangDTO> getDoanhThuTheoLoai();

    // Gọi Stored Procedure 2
    @Query(value = "EXEC sp_TopKhachHangVIP", nativeQuery = true)
    List<TopKhachHangVipDTO> getTopKhachHang();
}