package com.bartoszkorec.warehouse_management.dto;

import com.bartoszkorec.warehouse_management.model.Point;

public record ConnectorDto(Integer id, Integer cellValue, Point p1, Point p2) {
}
