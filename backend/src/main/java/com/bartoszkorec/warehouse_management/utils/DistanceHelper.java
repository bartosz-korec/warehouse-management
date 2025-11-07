package com.bartoszkorec.warehouse_management.utils;

import com.bartoszkorec.warehouse_management.model.Distance;

public final class DistanceHelper {
    private DistanceHelper() {
    }

    public static Distance getNewDistance(long distance) {
        return getNewDistance(distance, null);
    }

    public static Distance getNewDistance(long distance, int[] connectors) {
        if (distance < 0L) {
            throw new IllegalArgumentException("Distance must be non-negative.");
        }
        if (connectors == null) {
            connectors = new int[0];
        }
        return new Distance(distance, connectors);
    }
}
