package com.bartoszkorec.warehouse_management.controller;

import com.bartoszkorec.warehouse_management.annotation.HasRole;
import com.bartoszkorec.warehouse_management.model.Role;
import com.bartoszkorec.warehouse_management.service.OrderService;
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

    @PostMapping("generate")
    public String generateRandomizedOrder() {
        return orderService.generateRandomizedOrder();
    }
}
