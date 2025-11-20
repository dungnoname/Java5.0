package com.poly.assignment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column(name = "UserId")
    private Integer userId;

    @Column(name = "HoTen", nullable = false)
    private String hoTen;

    @Column(name = "NgaySinh")
    private LocalDate ngaySinh;

    @Column(name = "GioiTinh")
    private Boolean gioiTinh; // true = Nam, false = Nữ

    @Column(name = "SoDienThoai")
    private String soDienThoai;

    @Column(name = "Email")
    private String email;

    @Column(name = "DiaChi")
    private String diaChi;

    // Map đúng tên cột TenDangNhap trong SQL
    @Column(name = "TenDangNhap", unique = true)
    private String tenDangNhap;

    // Map đúng tên cột MatKhau trong SQL
    @Column(name = "MatKhau")
    private String matKhau;

    // Map đúng tên cột Reset_Password_Token trong SQL
    @Column(name = "Reset_Password_Token")
    private String resetPasswordToken;

    // Quan hệ với bảng Roles
    // Trong SQL cột khóa ngoại là RoleId
    @ManyToOne
    @JoinColumn(name = "RoleId")
    private Role role;
    ///    update 22/10
    // Quan hệ 2 chiều
    @OneToMany(mappedBy = "nguoiDung")
    @JsonIgnore
    private List<HoaDon> hoaDonNguoiDung;

    @OneToMany(mappedBy = "nhanVien")
    @JsonIgnore
    private List<HoaDon> hoaDonNhanVien;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<GioHang> gioHangList;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<DanhGia> danhGiaList;
}
