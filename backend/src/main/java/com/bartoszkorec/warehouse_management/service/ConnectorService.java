package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.model.Connector;
import com.bartoszkorec.warehouse_management.model.LocationType;
import com.bartoszkorec.warehouse_management.model.Point;
import com.bartoszkorec.warehouse_management.utils.ConnectorHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConnectorService {

    private final Map<Integer, Connector> connectors = new HashMap<>();

    public Connector getConnector(int connectorId) {
        return connectors.get(connectorId);
    }

    public void updateConnectors(int[][] grid, int gridIndex) {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                int cellValue = grid[row][col];
                if (cellValue >= LocationType.CONNECTOR_MIN_VALUE.getLabel()) {
                    System.out.println("Adding connector: " + cellValue + " grid=" + gridIndex + " at (" + row + ", " + col + ")");
                    connectors.values().forEach(System.out::println);
                    Connector connector = connectors.getOrDefault(cellValue, new Connector(cellValue));
                    Point point = new Point(gridIndex, row, col);

                    try {
                        ConnectorHelper.addPointToConnector(connector, point);
                        connectors.put(cellValue, connector);
                    } catch (IllegalArgumentException e) {
                        // This happens when trying to add a point from the same grid
                        System.out.println("Warning: " + e.getMessage());
                    } catch (IllegalStateException e) {
                        // This happens when both points are already set
                        System.out.println("Warning: " + e.getMessage());
                    }
                }
            }
        }
    }
}
