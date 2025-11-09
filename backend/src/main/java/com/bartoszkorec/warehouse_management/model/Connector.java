package com.bartoszkorec.warehouse_management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "connectors")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Connector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cell_value", nullable = false, unique = true)
    private Integer cellValue;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "gridIndex", column = @Column(name = "p1_grid_index")),
            @AttributeOverride(name = "x", column = @Column(name = "p1_x")),
            @AttributeOverride(name = "y", column = @Column(name = "p1_y"))
    })
    private Point p1;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "gridIndex", column = @Column(name = "p2_grid_index")),
            @AttributeOverride(name = "x", column = @Column(name = "p2_x")),
            @AttributeOverride(name = "y", column = @Column(name = "p2_y"))
    })
    private Point p2;
}
