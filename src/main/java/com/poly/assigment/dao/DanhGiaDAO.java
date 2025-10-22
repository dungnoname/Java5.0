package com.poly.assigment.dao;

import com.poly.assigment.entity.HoaDon;
import com.poly.assigment.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.poly.assigment.entity.DanhGia;

import java.util.List;

@Repository
public interface DanhGiaDAO extends JpaRepository<DanhGia, Integer> {
    List<DanhGia> findBySanPham(SanPham sanPham);
    @Query("SELECT dg FROM DanhGia dg " +
            "JOIN dg.sanPham sp " +
            "JOIN sp.chiTietHoaDonList cthd " +
            "WHERE cthd.hoaDon = :hoaDon")
    List<DanhGia> findByHoaDon(@Param("hoaDon") HoaDon hoaDon);

    // Lấy tất cả đánh giá của một sản phẩm
    List<DanhGia> findBySanPhamMaSP(Integer maSP);
}
