package com.bartoszkorec.warehouse_management.controller;

import com.bartoszkorec.warehouse_management.annotation.HasRole;
import com.bartoszkorec.warehouse_management.model.Role;
import com.bartoszkorec.warehouse_management.service.DistanceMatrixService;
import com.bartoszkorec.warehouse_management.service.LocationService;
import com.bartoszkorec.warehouse_management.service.OrderService;
import com.bartoszkorec.warehouse_management.utils.LocationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
