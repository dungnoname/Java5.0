package com.poly.assigment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "HoaDon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maHD;

    private LocalDateTime ngayLap;

    @ManyToOne
    @JoinColumn(name = "nguoiDungId")
    private User nguoiDung;

    @ManyToOne
    @JoinColumn(name = "nhanVienId")
    private User nhanVien;

    @ManyToOne
    @JoinColumn(name = "maTT")
    private TrangThaiDonHang trangThaiDonHang;

    @OneToMany(mappedBy = "hoaDon")
    private List<ChiTietHoaDon> chiTietHoaDonList;
}
