package com.poly.assigment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "TrangThaiDonHang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrangThaiDonHang {
    @Id
    private Integer maTT;

    private String tenTT;
    private String moTa;
    private Integer thuTu;

    @OneToMany(mappedBy = "trangThaiDonHang")
    private List<HoaDon> hoaDons;
}
