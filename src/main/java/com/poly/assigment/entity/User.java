package com.poly.assigment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

// Import các annotation validation
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được quá 100 ký tự")
    @Column(nullable = false)
    private String hoTen;

    @NotNull(message = "Ngày sinh không được để trống")
    private LocalDate ngaySinh;

    @NotNull(message = "Giới tính không được để trống")
    private Boolean gioiTinh; // true = Nam, false = Nữ

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không hợp lệ") // Ví dụ: định dạng SĐT Việt Nam
    private String soDienThoai;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Column(unique = true) // Vẫn giữ unique ở DB, nhưng sẽ kiểm tra thêm ở service
    private String email;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 255, message = "Địa chỉ không được quá 255 ký tự")
    private String diaChi;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 5, max = 50, message = "Tên đăng nhập phải từ 5 đến 50 ký tự")
    @Column(unique = true) // Vẫn giữ unique ở DB, nhưng sẽ kiểm tra thêm ở service
    private String tenDangNhap;

    // Mật khẩu sẽ được xử lý logic riêng trong controller/service
    @Column(nullable = false) // Mật khẩu bắt buộc ở DB
    private String matKhau;

    @ManyToOne
    @JoinColumn(name = "roleId")
    @NotNull(message = "Vai trò không được để trống") // Role là bắt buộc
    private Role role;
    ///    update 22/10
    @Column(name = "reset_password_token")
    private String resetPasswordToken;
    ///    update 22/10
    // Quan hệ 2 chiều
    @OneToMany(mappedBy = "nguoiDung")
    private List<HoaDon> hoaDonNguoiDung;

    @OneToMany(mappedBy = "nhanVien")
    private List<HoaDon> hoaDonNhanVien;

    @OneToMany(mappedBy = "user")
    private List<GioHang> gioHangList;

    @OneToMany(mappedBy = "user")
    private List<DanhGia> danhGiaList;
}//