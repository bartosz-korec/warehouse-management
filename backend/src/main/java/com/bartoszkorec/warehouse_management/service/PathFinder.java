package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.dto.ConnectorDto;
import com.bartoszkorec.warehouse_management.dto.LocationDto;
import com.bartoszkorec.warehouse_management.model.*;
import com.bartoszkorec.warehouse_management.utils.ConnectorHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class PathFinder {

    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private final ConnectorService connectorService;
    private final GridService gridService;

    /**
     * Find the shortest path between two locations using BFS.
     */
    public Distance findShortestPath(LocationDto start, LocationDto end) {
        Queue<Point> frontier = new LinkedList<>();
        Set<Point> visited = new HashSet<>();
        Map<Point, Long> distances = new HashMap<>();
        Map<Point, List<Integer>> usedConnectors = new HashMap<>(); // Track list of connector IDs

        // Initialize search
        Point startPoint = start.point();
        frontier.add(startPoint);
        visited.add(startPoint);
        distances.put(startPoint, 0L);
        usedConnectors.put(startPoint, new ArrayList<>()); // Start with empty list of connectors

        while (!frontier.isEmpty()) {
            Point current = frontier.poll();

            // Check if reached destination
            if (current.equals(end.point())) {
                List<Integer> connectorIds = usedConnectors.get(current);
                int[] connectorArray = connectorIds.stream().mapToInt(Integer::intValue).toArray();
                return new Distance(distances.get(current), connectorArray);
            }

            // Process current node
            processNeighbors(current, distances, usedConnectors, visited, frontier);
            processConnectors(current, distances, usedConnectors, visited, frontier);
        }

        // If no path is found, return a very large distance value
        return new Distance(Long.MAX_VALUE, new int[0]);
    }

    /**
     * Process adjacent neighbors of current point.
     */
    private void processNeighbors(Point current, Map<Point, Long> distances,
                                  Map<Point, List<Integer>> usedConnectors, Set<Point> visited,
                                  Queue<Point> frontier) {
        for (int[] dir : DIRECTIONS) {
            int newX = current.x() + dir[0];
            int newY = current.y() + dir[1];

            try {
                Point neighbor = new Point(current.gridIndex(), newX, newY);

                // Skip if already visited
                if (visited.contains(neighbor)) {
                    continue;
                }

                try {
                    int cellValue = gridService.getCellValueFromGrid(neighbor.gridIndex(), neighbor.x(), neighbor.y());
                    if (cellValue == LocationType.WALL.getLabel()) {
                        continue;
                    }

                    visited.add(neighbor);
                    distances.put(neighbor, distances.get(current) + 1);
                    // Copy connectors from current point
                    usedConnectors.put(neighbor, new ArrayList<>(usedConnectors.get(current)));
                    frontier.add(neighbor);
                } catch (IllegalArgumentException e) {
                    // Skip positions that are out of grid bounds
                }
            } catch (IllegalArgumentException e) {
                // Skip invalid positions (negative coordinates)
            }
        }
    }

    private void processConnectors(Point current, Map<Point, Long> distances,
                                   Map<Point, List<Integer>> usedConnectors, Set<Point> visited,
                                   Queue<Point> frontier) {
        try {
            int cellValue = gridService.getCellValueFromGrid(current.gridIndex(), current.x(), current.y());

            if (cellValue >= LocationType.CONNECTOR_MIN_VALUE.getLabel()) {
                // Get connector from the ConnectorManager directly
                ConnectorDto connector = connectorService.getConnectorByCellValue(cellValue);

                if (connector != null && ConnectorHelper.isConnectorReady(connector)) {
                    for (Point otherPoint : ConnectorHelper.getPointsFromConnector(connector)) {
                        if (!otherPoint.equals(current) && !visited.contains(otherPoint)) {
                            visited.add(otherPoint);
                            distances.put(otherPoint, distances.get(current) + 1);

                            // Create a new list with all previous connectors
                            List<Integer> newConnectors = new ArrayList<>(usedConnectors.get(current));
                            // Add this connector's ID
                            newConnectors.add(connector.cellValue());
                            usedConnectors.put(otherPoint, newConnectors);

                            frontier.add(otherPoint);
                        }
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            // Skip if the position is invalid or out of bounds
        }
    }
}
