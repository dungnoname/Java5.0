package com.poly.assigment.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    private Integer roleId;

    @Column(nullable = false, length = 50)
    private String roleName;
}
