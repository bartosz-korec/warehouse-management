package com.bartoszkorec.warehouse_management.dto;

import java.util.List;

public record RouteResultDto(
        List<Integer> orderedLocationIds,        // 1-based ids in visit order
        List<List<Integer>> connectorsPerLeg,    // connectors between successive locations
        long totalDistance
) {}
