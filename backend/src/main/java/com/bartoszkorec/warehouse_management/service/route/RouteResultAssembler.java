package com.bartoszkorec.warehouse_management.service.route;

import com.bartoszkorec.warehouse_management.dto.RouteResultDto;
import com.bartoszkorec.warehouse_management.model.Distance;
import com.bartoszkorec.warehouse_management.model.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RouteResultAssembler {
    private RouteResultAssembler() {}

    public static RouteResultDto empty() {
        return new RouteResultDto(List.of(), List.of(), List.of(), 0L);
    }

    public static RouteResultDto assemble(RouteComputationContext context, List<Integer> subRoute) {
        if (subRoute == null || subRoute.size() < 2) {
            return empty();
        }

        List<Integer> ordered = new ArrayList<>();
        List<List<Integer>> connectorsPerLeg = new ArrayList<>();
        List<List<Point>> pathPerLeg = new ArrayList<>();
        long totalDistance = 0L;

        for (int subIndex : subRoute) {
            int original = context.toOriginalNode(subIndex);
            ordered.add(original + 1);
        }

        for (int i = 0; i < subRoute.size() - 1; i++) {
            int fromOriginal = context.toOriginalNode(subRoute.get(i));
            int toOriginal = context.toOriginalNode(subRoute.get(i + 1));

            Distance leg = context.distanceMatrix()[fromOriginal][toOriginal];
            connectorsPerLeg.add(toConnectorList(leg.connectors()));
            pathPerLeg.add(leg.path() == null ? List.of() : List.copyOf(leg.path()));
            totalDistance += leg.distance();
        }

        return new RouteResultDto(ordered, connectorsPerLeg, pathPerLeg, totalDistance);
    }

    private static List<Integer> toConnectorList(int[] connectors) {
        if (connectors == null || connectors.length == 0) {
            return Collections.emptyList();
        }
        List<Integer> list = new ArrayList<>(connectors.length);
        for (int connector : connectors) {
            list.add(connector);
        }
        return list;
    }
}
