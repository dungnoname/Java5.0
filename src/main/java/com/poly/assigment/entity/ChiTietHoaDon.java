package com.poly.assigment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ChiTietHoaDon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietHoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maCTHD;

    @ManyToOne
    @JoinColumn(name = "maHD")
    private HoaDon hoaDon;

    @ManyToOne
    @JoinColumn(name = "maSP")
    private SanPham sanPham;

    private Integer soLuongBan;
    private BigDecimal donGia;
}
