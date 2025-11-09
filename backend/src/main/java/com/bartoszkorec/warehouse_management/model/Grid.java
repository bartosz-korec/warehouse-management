package com.bartoszkorec.warehouse_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "grids")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Grid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Convert(converter = IntArrayConverter.class)
    @Column(columnDefinition = "TEXT")
    private int[][] layout;
}
