package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.dto.LocationDto;
import com.bartoszkorec.warehouse_management.dto.RouteResultDto;
import com.bartoszkorec.warehouse_management.model.Distance;
import com.bartoszkorec.warehouse_management.service.route.RouteAlgorithm;
import com.bartoszkorec.warehouse_management.service.route.RouteComputationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class RouteService {

    private final DistanceMatrixService distanceMatrixService;
    private final Map<String, RouteAlgorithm> algorithms;

    @Value("${routing.algorithm:ortools}")
    private String algorithmKey;

    public RouteResultDto calculateSolution(Set<Integer> locations, LocationDto startingLocation) {
        Distance[][] distanceMatrix = distanceMatrixService.getDistanceMatrix().getMatrix();
        RouteComputationContext context =
                RouteComputationContext.from(locations, startingLocation, distanceMatrix);

        RouteAlgorithm algorithm = algorithms.getOrDefault(algorithmKey, algorithms.get("ortools"));
        if (algorithm == null) {
            throw new IllegalStateException("No routing algorithm registered for key: " + algorithmKey);
        }

        log.info("Using routing algorithm: {}", algorithmKey);
        return algorithm.calculate(context);
    }
}