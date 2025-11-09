package com.bartoszkorec.warehouse_management.utils;

import com.bartoszkorec.warehouse_management.dto.ConnectorDto;
import com.bartoszkorec.warehouse_management.model.Connector;
import com.bartoszkorec.warehouse_management.model.LocationType;
import com.bartoszkorec.warehouse_management.model.Point;

import java.util.List;

public final class ConnectorHelper {
    private ConnectorHelper() {}

    public static Connector createNewConnector(int cellValue) {
        return toEntity(new ConnectorDto(null, cellValue, null, null));
    }

    public static Connector toEntity(ConnectorDto connectorDto) {
        if (connectorDto.cellValue() < LocationType.CONNECTOR_MIN_VALUE.getLabel()) {
            throw new IllegalArgumentException("Connector label must be greater than or equal to "
                    + LocationType.CONNECTOR_MIN_VALUE.getLabel());
        }
        return new Connector(connectorDto.id(), connectorDto.cellValue(),
                connectorDto.p1(), connectorDto.p2());
    }

    public static ConnectorDto toDto(Connector connector) {
        if (connector.getCellValue() < LocationType.CONNECTOR_MIN_VALUE.getLabel()) {
            throw new IllegalArgumentException("Connector label must be greater than or equal to "
                    + LocationType.CONNECTOR_MIN_VALUE.getLabel());
        }
        return new ConnectorDto(connector.getId(), connector.getCellValue(),
                connector.getP1(), connector.getP2());
    }

    public static void addPointToConnector(Connector connector, Point point) {
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
    }

    public static ConnectorDto addPointToConnectorDto(ConnectorDto connectorDto, Point point) {
        if (connectorDto.p1() == null) {
            return new ConnectorDto(connectorDto.id(), connectorDto.cellValue(), point, null);
        } else if (connectorDto.p2() == null) {
            if (connectorDto.p1().gridIndex() == point.gridIndex()) {
                throw new IllegalArgumentException("Both points must be in different grids."
                        + " p1: " + connectorDto.p1().gridIndex() + ", p2: " + point.gridIndex());
            }
            return new ConnectorDto(connectorDto.id(), connectorDto.cellValue(), connectorDto.p1(), point);
        } else {
            throw new IllegalStateException("Both points are already set.");
        }
    }

    public static boolean isConnectorReady(Connector connector) {
        return connector.getP1() != null && connector.getP2() != null;
    }

    public static boolean isConnectorReady(ConnectorDto connectorDto) {
        return connectorDto.p1() != null && connectorDto.p2() != null;
    }

    public static List<Point> getPointsFromConnector(ConnectorDto connectorDto) {
        if (!isConnectorReady(connectorDto)) {
            throw new IllegalStateException("Connector is not ready. Both points must be set.");
        }
        return List.of(connectorDto.p1(), connectorDto.p2());
    }
}
