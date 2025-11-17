package com.bartoszkorec.warehouse_management.service.route;

import com.bartoszkorec.warehouse_management.dto.RouteResultDto;

public interface RouteAlgorithm {
    RouteResultDto calculate(RouteComputationContext context);
}
