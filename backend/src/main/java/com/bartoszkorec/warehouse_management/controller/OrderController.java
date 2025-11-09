package com.bartoszkorec.warehouse_management.controller;

import com.bartoszkorec.warehouse_management.annotation.HasRole;
import com.bartoszkorec.warehouse_management.dto.LocationDto;
import com.bartoszkorec.warehouse_management.dto.request.CalculateRouteRequest;
import com.bartoszkorec.warehouse_management.model.Distance;
import com.bartoszkorec.warehouse_management.model.Role;
import com.bartoszkorec.warehouse_management.service.DistanceMatrixService;
import com.bartoszkorec.warehouse_management.service.LocationService;
import com.bartoszkorec.warehouse_management.service.OrderService;
import com.bartoszkorec.warehouse_management.utils.LocationHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("api/v1/order")
@HasRole(Role.ROLE_EMPLOYEE)
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final DistanceMatrixService distanceMatrixService;
    private final LocationService locationService;

    @PostMapping("generate")
    public String generateRandomizedOrder() {
        return orderService.generateRandomizedOrder(distanceMatrixService.getDistanceMatrix().getMatrix(),
                LocationHelper.toDto(locationService.getStartingLocation().orElseThrow(
                () -> new IllegalStateException("Starting location not found")
        )));
    }

    @PostMapping("calculate")
    public String calculateRouteForLocationIds(@RequestBody @Valid CalculateRouteRequest request) {
        Distance[][] matrix = distanceMatrixService.getDistanceMatrix().getMatrix();
        LocationDto starting = LocationHelper.toDto(locationService.getStartingLocation().orElseThrow(
                () -> new IllegalStateException("Starting location not found")
        ));

        Set<Integer> indices = new HashSet<>(request.locationIds());
        indices.add(starting.id());

        return orderService.calculateRouteForLocationIds(indices, matrix, starting);
    }
}
