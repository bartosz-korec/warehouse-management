package com.bartoszkorec.warehouse_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "distance_matrix")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DistanceMatrix {

    @Id
    private Integer id = 1;

    @Convert(converter = DistanceMatrixConverter.class)
    @Column(columnDefinition = "TEXT")
    private Distance[][] matrix;
}
