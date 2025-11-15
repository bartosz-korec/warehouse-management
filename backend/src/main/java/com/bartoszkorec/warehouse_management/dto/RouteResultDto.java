package com.bartoszkorec.warehouse_management.dto;

import com.bartoszkorec.warehouse_management.model.Point;

import java.util.List;

public record RouteResultDto(
        List<Integer> orderedLocationIds,        // 1-based ids in visit order
        List<List<Integer>> connectorsPerLeg,    // connectors between successive locations
        List<List<Point>> pathPerLeg,
        long totalDistance
) {}
