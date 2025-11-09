package com.bartoszkorec.warehouse_management.dto;

import com.bartoszkorec.warehouse_management.model.LocationType;
import com.bartoszkorec.warehouse_management.model.Point;

public record LocationDto(Integer id, Point point, LocationType locationType) {
}
