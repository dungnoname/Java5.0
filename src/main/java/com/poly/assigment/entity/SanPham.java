package com.poly.assigment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "SanPham")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maSP;

    @Column(nullable = false)
    private String tenSP;

    @Column(nullable = false)
    private BigDecimal donGiaBan;

    @Column(nullable = false)
    private Integer soLuongTon;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    private Boolean trangThai;

    @ManyToOne
    @JoinColumn(name = "maLoai")
    private LoaiSanPham loaiSanPham;

    @ManyToOne
    @JoinColumn(name = "maHang")
    private Hang hang;

    @OneToMany(mappedBy = "sanPham")
    private List<GioHang> gioHangList;

    @OneToMany(mappedBy = "sanPham")
    private List<ChiTietHoaDon> chiTietHoaDonList;

    @OneToMany(mappedBy = "sanPham")
    private List<DanhGia> danhGiaList;
}
