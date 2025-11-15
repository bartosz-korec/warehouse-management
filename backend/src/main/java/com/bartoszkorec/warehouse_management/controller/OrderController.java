package com.bartoszkorec.warehouse_management.controller;

import com.bartoszkorec.warehouse_management.annotation.HasRole;
import com.bartoszkorec.warehouse_management.dto.request.OrderRequest;
import com.bartoszkorec.warehouse_management.dto.response.OrderResponse;
import com.bartoszkorec.warehouse_management.model.Role;
import com.bartoszkorec.warehouse_management.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Order", description = "Endpoints for computing order pick routes")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("generate")
    @Operation(
            summary = "Generate a randomized order route",
            description = "Generates a randomized set of pick locations and returns the computed picking route."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Route generated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public OrderResponse generateRandomizedOrder() {
        return orderService.generateOrderRoute();
    }

    @PostMapping("calculate")
    @Operation(
            summary = "Calculate route for provided location IDs",
            description = "Computes an optimal picking route for the given list of location IDs."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Payload with the list of location IDs to visit",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = OrderRequest.class))
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Route computed",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public OrderResponse calculateRouteForLocationIds(@RequestBody @Valid OrderRequest request) {

        return orderService.computeOrderRoute(request.locationIds());
    }
}
