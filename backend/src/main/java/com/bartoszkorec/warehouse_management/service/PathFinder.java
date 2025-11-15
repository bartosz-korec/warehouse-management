package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.dto.ConnectorDto;
import com.bartoszkorec.warehouse_management.dto.LocationDto;
import com.bartoszkorec.warehouse_management.model.Distance;
import com.bartoszkorec.warehouse_management.model.LocationType;
import com.bartoszkorec.warehouse_management.model.Point;
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

    public Distance findShortestPath(LocationDto start, LocationDto end) {
        Queue<Point> frontier = new LinkedList<>();
        Set<Point> visited = new HashSet<>();
        Map<Point, Long> distances = new HashMap<>();
        Map<Point, List<Integer>> usedConnectors = new HashMap<>();
        Map<Point, Point> predecessors = new HashMap<>();

        Point startPoint = start.point();
        frontier.add(startPoint);
        visited.add(startPoint);
        distances.put(startPoint, 0L);
        usedConnectors.put(startPoint, new ArrayList<>());
        predecessors.put(startPoint, null);

        while (!frontier.isEmpty()) {
            Point current = frontier.poll();

            if (current.equals(end.point())) {
                List<Integer> connectorIds = usedConnectors.get(current);
                int[] connectorArray = connectorIds.stream().mapToInt(Integer::intValue).toArray();
                List<Point> path = reconstructPath(current, predecessors);
                return new Distance(distances.get(current), connectorArray, List.copyOf(path));
            }

            processNeighbors(current, distances, usedConnectors, visited, frontier, predecessors);
            processConnectors(current, distances, usedConnectors, visited, frontier, predecessors);
        }

        return new Distance(Long.MAX_VALUE, new int[0], List.of());
    }

    private void processNeighbors(Point current, Map<Point, Long> distances,
                                  Map<Point, List<Integer>> usedConnectors, Set<Point> visited,
                                  Queue<Point> frontier, Map<Point, Point> predecessors) {
        for (int[] dir : DIRECTIONS) {
            int newX = current.x() + dir[0];
            int newY = current.y() + dir[1];

            try {
                Point neighbor = new Point(current.gridIndex(), newX, newY);

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
                    usedConnectors.put(neighbor, new ArrayList<>(usedConnectors.get(current)));
                    predecessors.put(neighbor, current);
                    frontier.add(neighbor);
                } catch (IllegalArgumentException ignored) {
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private void processConnectors(Point current, Map<Point, Long> distances,
                                   Map<Point, List<Integer>> usedConnectors, Set<Point> visited,
                                   Queue<Point> frontier, Map<Point, Point> predecessors) {
        try {
            int cellValue = gridService.getCellValueFromGrid(current.gridIndex(), current.x(), current.y());

            if (cellValue >= LocationType.CONNECTOR_MIN_VALUE.getLabel()) {
                ConnectorDto connector = connectorService.getConnectorByCellValue(cellValue);

                if (connector != null && ConnectorHelper.isConnectorReady(connector)) {
                    for (Point otherPoint : ConnectorHelper.getPointsFromConnector(connector)) {
                        if (!otherPoint.equals(current) && !visited.contains(otherPoint)) {
                            visited.add(otherPoint);
                            distances.put(otherPoint, distances.get(current) + 1);

                            List<Integer> newConnectors = new ArrayList<>(usedConnectors.get(current));
                            newConnectors.add(connector.cellValue());
                            usedConnectors.put(otherPoint, newConnectors);

                            predecessors.put(otherPoint, current);
                            frontier.add(otherPoint);
                        }
                    }
                }
            }
        } catch (IllegalArgumentException ignored) {
        }
    }

    private List<Point> reconstructPath(Point target, Map<Point, Point> predecessors) {
        LinkedList<Point> path = new LinkedList<>();
        Point current = target;
        while (current != null) {
            path.addFirst(current);
            current = predecessors.get(current);
        }
        return path;
    }
}
