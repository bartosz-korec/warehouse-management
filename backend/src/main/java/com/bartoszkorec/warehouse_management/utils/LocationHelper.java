package com.bartoszkorec.warehouse_management.utils;

import com.bartoszkorec.warehouse_management.model.Location;
import com.bartoszkorec.warehouse_management.model.LocationType;
import com.bartoszkorec.warehouse_management.model.Point;

public final class LocationHelper {
    private LocationHelper() {
    }

    public static Location getNewLocation(Point point, int label, LocationType type) {
        if (point == null) {
            throw new IllegalArgumentException("Point cannot be null");
        }
        if (label < 0) {
            throw new IllegalArgumentException("Label must be non-negative");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        return new Location(point, label, type);
    }

    public static Location getNewLocation(int gridIndex, int x, int y, int label, LocationType type) {
        return getNewLocation(PointHelper.getNewPoint(gridIndex, x, y), label, type);
    }

    public static Location getNewLocation(Location location) {
        return getNewLocation(location.point(), location.label(), location.type());
    }
}
