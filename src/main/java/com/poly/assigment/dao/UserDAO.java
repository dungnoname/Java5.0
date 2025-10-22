package com.poly.assigment.dao;

import com.poly.assigment.dto.TopKhachHangVipDTO;
import com.poly.assigment.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<User, Integer> {
    Optional<User> findByTenDangNhap(String tenDangNhap);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetPasswordToken(String token);

    @Query("SELECT u.hoTen AS tenKhachHang, " +
            "SUM(cthd.soLuongBan * cthd.donGia) AS tongTienDaMua, " +
            "MIN(CAST(hd.ngayLap AS java.sql.Date)) AS ngayMuaDauTien, " + // SỬA Ở ĐÂY
            "MAX(CAST(hd.ngayLap AS java.sql.Date)) AS ngayMuaSauCung " + // SỬA Ở ĐÂY
            "FROM User u " +
            "JOIN u.hoaDonNguoiDung hd " +
            "JOIN hd.chiTietHoaDonList cthd " +
            "GROUP BY u.userId, u.hoTen " +
            "ORDER BY SUM(cthd.soLuongBan * cthd.donGia) DESC")
    List<TopKhachHangVipDTO> getTopKhachHangVip(Pageable pageable);
}