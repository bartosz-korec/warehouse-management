package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.dto.ConnectorDto;
import com.bartoszkorec.warehouse_management.dto.LocationDto;
import com.bartoszkorec.warehouse_management.dto.RouteResultDto;
import com.bartoszkorec.warehouse_management.dto.response.OrderResponse;
import com.bartoszkorec.warehouse_management.model.Point;
import com.bartoszkorec.warehouse_management.utils.ConnectorHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final RouteService routeService;
    private final GridService gridService;
    private final LocationService locationService;
    private final ConnectorService connectorService;

    public OrderResponse generateOrderRoute() {
        int n = Math.toIntExact(locationService.getLocationCount());
        if (n < 1) {
            return new OrderResponse(List.of(), List.of());
        }

        Random random = new Random();
        int subsetSize = 1 + random.nextInt(n); // 1..n

        Set<Integer> indices = new HashSet<>();
        while (indices.size() < subsetSize) {
            int candidate = 1 + random.nextInt(n);
            indices.add(candidate);
        }

        return computeOrderRoute(indices);
    }

    public OrderResponse computeOrderRoute(Set<Integer> indices) {
        if (indices == null || indices.isEmpty()) {
            return new OrderResponse(List.of(), List.of());
        }

        log.info("Order location ids: {}", indices);
        RouteResultDto routeResult = routeService.calculateSolution(indices, locationService.getStartingLocationDto());
        log.debug("Route total distance: {}", routeResult.totalDistance());

        Map<Integer, LocationDto> locationsById = locationService.getAllLocations()
                .stream()
                .collect(Collectors.toMap(LocationDto::id, Function.identity(), (a, b) -> a, HashMap::new));

        List<Integer> ordered = routeResult.orderedLocationIds();
        List<List<Integer>> legsConnectors = routeResult.connectorsPerLeg();

        List<OrderResponse.OrderLocationDto> locations = new ArrayList<>();

        for (int i = 0; i < ordered.size(); i++) {
            Integer locId = ordered.get(i);
            LocationDto loc = locationsById.get(locId);
            Point locPoint = (loc != null) ? loc.point() : null;

            locations.add(new OrderResponse.OrderLocationDto("L" + locId, locPoint));

            if (locPoint == null || legsConnectors == null || i >= legsConnectors.size()) {
                continue;
            }

            int currentGrid = locPoint.gridIndex();
            List<Integer> connectorsForLeg = legsConnectors.get(i);
            if (connectorsForLeg == null || connectorsForLeg.isEmpty()) {
                continue;
            }

            for (Integer cellValue : connectorsForLeg) {
                try {
                    ConnectorDto c = connectorService.getConnectorByCellValue(cellValue);
                    if (c == null) continue;
                    currentGrid = appendConnectorPointsInTraversalOrder(locations, c, currentGrid);
                } catch (IllegalArgumentException e) {
                    log.warn("Connector {} not found: {}", cellValue, e.getMessage());
                }
            }
        }

        return new OrderResponse(gridService.getAllGrids(), locations);
    }

    private int appendConnectorPointsInTraversalOrder(List<OrderResponse.OrderLocationDto> out,
                                                      ConnectorDto c,
                                                      int currentGrid) {
        if (c == null) return currentGrid;

        String label = "C" + c.cellValue();

        // Both points present
        if (ConnectorHelper.isConnectorReady(c)) {
            List<Point> pts = ConnectorHelper.getPointsFromConnector(c); // [p1, p2]
            Point a = pts.get(0);
            Point b = pts.get(1);

            boolean aMatches = a.gridIndex() == currentGrid;
            boolean bMatches = b.gridIndex() == currentGrid;

            // Decide traversal order
            Point first = (bMatches && !aMatches) ? b : a;
            Point second = (bMatches && !aMatches) ? a : b;

            out.add(new OrderResponse.OrderLocationDto(label, first));
            out.add(new OrderResponse.OrderLocationDto(label, second));
            return second.gridIndex();
        }

        // Only one point present
        Point single = (c.p1() != null) ? c.p1() : c.p2();
        if (single != null) {
            out.add(new OrderResponse.OrderLocationDto(label, single));
            return single.gridIndex();
        }
        return currentGrid;
    }
}