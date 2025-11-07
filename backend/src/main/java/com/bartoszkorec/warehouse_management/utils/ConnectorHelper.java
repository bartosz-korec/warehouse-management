package com.bartoszkorec.warehouse_management.utils;

import com.bartoszkorec.warehouse_management.model.Connector;
import com.bartoszkorec.warehouse_management.model.LocationType;
import com.bartoszkorec.warehouse_management.model.Point;

import java.util.List;

public final class ConnectorHelper {
    private ConnectorHelper() {}

    public static Connector getNewConnector(int label) {
        if (label < LocationType.CONNECTOR_MIN_VALUE.getLabel()) {
            throw new IllegalArgumentException("Connector label must be greater than or equal to "
                    + LocationType.CONNECTOR_MIN_VALUE.getLabel());
        }
        return new Connector(label);
    }

    public static Connector addPointToConnector(Connector connector, Point point) {
        if (connector.getP1() == null) {
            connector.setP1(point);
        } else if (connector.getP2() == null) {
            if (connector.getP1().gridIndex() == point.gridIndex()) {
                throw new IllegalArgumentException("Both points must be in different grids."
                        + " p1: " + connector.getP1().gridIndex() + ", p2: " + point.gridIndex());
            }
            connector.setP2(point);
        } else {
            throw new IllegalStateException("Both points are already set.");
        }
        return connector;
    }

    public static boolean isConnectorReady(Connector connector) {
        return connector.getP1() != null && connector.getP2() != null;
    }

    public static List<Point> getPointsFromConnector(Connector connector) {
        if (!isConnectorReady(connector)) {
            throw new IllegalStateException("Connector is not ready. Both points must be set.");
        }
        return List.of(connector.getP1(), connector.getP2());
    }
}
