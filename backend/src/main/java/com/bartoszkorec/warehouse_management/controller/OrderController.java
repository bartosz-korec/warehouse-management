package com.bartoszkorec.warehouse_management.controller;

import com.bartoszkorec.warehouse_management.annotation.HasRole;
import com.bartoszkorec.warehouse_management.dto.request.OrderRequest;
import com.bartoszkorec.warehouse_management.dto.response.OrderResponse;
import com.bartoszkorec.warehouse_management.model.Role;
import com.bartoszkorec.warehouse_management.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/order")
@HasRole(Role.ROLE_EMPLOYEE)
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("generate")
    public OrderResponse generateRandomizedOrder() {
        return orderService.generateOrderRoute();
    }

    @PostMapping("calculate")
    public OrderResponse calculateRouteForLocationIds(@RequestBody @Valid OrderRequest request) {

        return orderService.computeOrderRoute(request.locationIds());
    }
}
