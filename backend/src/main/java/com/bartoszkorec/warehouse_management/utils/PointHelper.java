package com.bartoszkorec.warehouse_management.utils;

import com.bartoszkorec.warehouse_management.model.Point;

public final class PointHelper {
    private PointHelper() {
    }

    public static Point getNewPoint(int gridIndex, int x, int y) {
        if (gridIndex < 0 || x < 0 || y < 0) {
            throw new IllegalArgumentException("Grid index, x, and y must be non-negative.");
        }
        return new Point(gridIndex, x, y);
    }
}
