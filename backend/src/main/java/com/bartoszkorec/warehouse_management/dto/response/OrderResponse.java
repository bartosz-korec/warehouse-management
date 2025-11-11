package com.bartoszkorec.warehouse_management.dto.response;

import com.bartoszkorec.warehouse_management.dto.GridDto;
import com.bartoszkorec.warehouse_management.model.Point;

import java.util.List;

public record OrderResponse(List<GridDto> warehouses, List<OrderLocationDto> locations) {
    public record OrderLocationDto(String label, Point point) {
    }
}
