package com.bartoszkorec.warehouse_management.service.route;

import com.bartoszkorec.warehouse_management.dto.RouteResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("nearestneighbor")
@Slf4j
public class NearestNeighborRouteAlgorithm implements RouteAlgorithm {

    private final NearestNeighborHeuristic heuristic = new NearestNeighborHeuristic();

    @Override
    public RouteResultDto calculate(RouteComputationContext context) {
        long[][] matrix = context.subMatrix();
        if (matrix.length <= 1) {
            return RouteResultAssembler.empty();
        }

        List<Integer> route = heuristic.buildRoute(matrix, context.depotIndex());
        log.info("Nearest neighbor route length: {}", computeLength(matrix, route));
        return RouteResultAssembler.assemble(context, route);
    }

    private long computeLength(long[][] matrix, List<Integer> route) {
        long total = 0L;
        for (int i = 0; i < route.size() - 1; i++) {
            total += matrix[route.get(i)][route.get(i + 1)];
        }
        return total;
    }
}
