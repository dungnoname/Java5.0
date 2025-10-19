package com.poly.assigment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "LoaiSanPham")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoaiSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maLoai;

    @Column(nullable = false)
    private String tenLoai;

    @OneToMany(mappedBy = "loaiSanPham")
    private List<SanPham> sanPhams;
}
