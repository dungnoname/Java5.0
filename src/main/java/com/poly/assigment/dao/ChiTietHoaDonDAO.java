package com.poly.assigment.dao;

import com.poly.assigment.dto.ThongKeDoanhThuLoaiHangDTO;
import com.poly.assigment.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.poly.assigment.entity.ChiTietHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.List;

@Repository
public interface ChiTietHoaDonDAO extends JpaRepository<ChiTietHoaDon, Integer> {


    @Query("SELECT l.tenLoai AS tenLoai, " +
            "SUM(cthd.soLuongBan * cthd.donGia) AS tongDoanhThu, " +
            "SUM(cthd.soLuongBan) AS tongSoLuong, " +
            "MAX(cthd.donGia) AS giaCaoNhat, " +
            "MIN(cthd.donGia) AS giaThapNhat, " +
            "AVG(cthd.donGia) AS giaTrungBinh " +
            "FROM ChiTietHoaDon cthd " +
            "JOIN cthd.sanPham sp " +
            "JOIN sp.loaiSanPham l " +
            "GROUP BY l.tenLoai " +
            "ORDER BY SUM(cthd.soLuongBan * cthd.donGia) DESC")
    List<ThongKeDoanhThuLoaiHangDTO> getDoanhThuByLoaiSanPham();
    List<ChiTietHoaDon> findByHoaDon(HoaDon hoaDon);
}//

