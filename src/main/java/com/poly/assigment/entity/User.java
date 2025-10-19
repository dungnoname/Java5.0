package com.poly.assigment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(nullable = false)
    private String hoTen;

    private LocalDate ngaySinh;
    private Boolean gioiTinh; // true = Nam, false = Nữ
    private String soDienThoai;
    private String email;
    private String diaChi;

    @Column(unique = true)
    private String tenDangNhap;

    private String matKhau;

    @ManyToOne
    @JoinColumn(name = "roleId")
    private Role role;

    // Quan hệ 2 chiều
    @OneToMany(mappedBy = "nguoiDung")
    private List<HoaDon> hoaDonNguoiDung;

    @OneToMany(mappedBy = "nhanVien")
    private List<HoaDon> hoaDonNhanVien;

    @OneToMany(mappedBy = "user")
    private List<GioHang> gioHangList;

    @OneToMany(mappedBy = "user")
    private List<DanhGia> danhGiaList;
}
