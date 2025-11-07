package com.bartoszkorec.warehouse_management.model;

import lombok.Data;

@Data
public class Connector {

    private final int label;
    private Point p1;
    private Point p2;
}
