package com.bartoszkorec.warehouse_management.utils;

import com.bartoszkorec.warehouse_management.dto.LocationDto;
import com.bartoszkorec.warehouse_management.model.Location;

public final class LocationHelper {
    private LocationHelper() {
    }

    public static Location toEntity(LocationDto locationDto) {
        return new Location(
                locationDto.id(),
                locationDto.point(),
                locationDto.locationType()
        );
    }

    public static LocationDto toDto(Location location) {
        return new LocationDto(
                location.getId(),
                location.getPoint(),
                location.getType()
        );
    }
}
