package com.poly.assignment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "Hang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maHang;

    @Column(nullable = false)
    private String tenHang;

    @OneToMany(mappedBy = "hang")
    @JsonIgnore
    private List<SanPham> sanPhams;
}
