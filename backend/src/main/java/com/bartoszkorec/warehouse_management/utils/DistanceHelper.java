package com.bartoszkorec.warehouse_management.utils;

import com.bartoszkorec.warehouse_management.model.Distance;
import com.bartoszkorec.warehouse_management.model.Point;

import java.util.List;

public final class DistanceHelper {
    private DistanceHelper() {
    }

    public static Distance getNewDistance(long distance, int[] connectors, List<Point> path) {
        if (distance < 0L) {
            throw new IllegalArgumentException("Distance must be non-negative.");
        }
        if (connectors == null) {
            connectors = new int[0];
        }
        if (path == null) {
            path = List.of();
        }
        return new Distance(distance, connectors, path);
    }
}
