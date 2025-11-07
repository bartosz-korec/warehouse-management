package com.bartoszkorec.warehouse_management.model;

public enum LocationType {

    WALL(0),
    TRACK(1),
    PICKUP_POINT(2),
    STARTING_POINT(3),
    CONNECTOR_MIN_VALUE(4),
    NO_CONNECTOR(-1); // Special value to indicate no connector used

    private final int label;

    LocationType(int label) {
        this.label = label;
    }
    public int getLabel() {
        return label;
    }
}
