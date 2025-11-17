package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.dto.LocationDto;
import com.bartoszkorec.warehouse_management.dto.RouteResultDto;
import com.bartoszkorec.warehouse_management.dto.response.OrderResponse;
import com.bartoszkorec.warehouse_management.model.LocationType;
import com.bartoszkorec.warehouse_management.model.Point;
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

    public OrderResponse generateOrderRoute() {
        int n = Math.toIntExact(locationService.getLocationCount());
        if (n < 1) {
            return new OrderResponse(List.of(), List.of());
        }

        Random random = new Random();
        int subsetSize = 1 + random.nextInt(n);

        Set<Integer> indices = new HashSet<>();
        while (indices.size() < subsetSize) {
            int candidate = 1 + random.nextInt(n);
            if (candidate == locationService.getStartingLocationDto().id()) {
                continue;
            }
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

        Map<Point, Integer> locationIdByPoint = locationsById.values()
                .stream()
                .filter(loc -> loc.point() != null)
                .collect(Collectors.toMap(LocationDto::point, LocationDto::id, (a, b) -> a, HashMap::new));

        List<Integer> ordered = routeResult.orderedLocationIds();
        List<List<Point>> legsPath = routeResult.pathPerLeg();

        List<OrderResponse.OrderLocationDto> locations = new ArrayList<>();

        for (int i = 0; i < ordered.size(); i++) {
            Integer locId = ordered.get(i);
            LocationDto loc = locationsById.get(locId);
            if (loc == null) {
                continue;
            }

            locations.add(new OrderResponse.OrderLocationDto("L" + locId, loc.point()));

            if (legsPath == null || i >= legsPath.size()) {
                continue;
            }

            List<Point> path = legsPath.get(i);
            if (path == null || path.size() <= 1) {
                continue;
            }

            for (int j = 1; j < path.size() - 1; j++) {
                Point intermediate = path.get(j);
                locations.add(mapPointToOrderLocation(intermediate, locationIdByPoint));
            }
        }

        return new OrderResponse(gridService.getAllGrids(), locations);
    }

    private OrderResponse.OrderLocationDto mapPointToOrderLocation(Point point,
                                                                   Map<Point, Integer> locationIdByPoint) {
        if (point == null) {
            return new OrderResponse.OrderLocationDto("P(?,?)", null);
        }

        Integer locationId = locationIdByPoint.get(point);
        if (locationId != null) {
            return new OrderResponse.OrderLocationDto("L" + locationId, point);
        }

        try {
            int cellValue = gridService.getCellValueFromGrid(point.gridIndex(), point.x(), point.y());
            if (cellValue >= LocationType.CONNECTOR_MIN_VALUE.getLabel()) {
                return new OrderResponse.OrderLocationDto("C" + cellValue, point);
            }
        } catch (IllegalArgumentException ignored) {
        }

        return new OrderResponse.OrderLocationDto("P(" + point.x() + "," + point.y() + ")", point);
    }
}