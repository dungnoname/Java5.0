package com.poly.assignment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private List<HoaDon> hoaDons;
}
