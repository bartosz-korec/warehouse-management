package com.bartoszkorec.warehouse_management.model;

import jakarta.persistence.Embeddable;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
public record Point(int gridIndex, int x, int y) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;
        return x() == point.x() && y() == point.y() && gridIndex() == point.gridIndex();
    }

    @Override
    public int hashCode() {
        int result = gridIndex();
        result = 31 * result + x();
        result = 31 * result + y();
        return result;
    }
}
