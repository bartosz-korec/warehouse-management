package com.bartoszkorec.warehouse_management.service.route;

import com.bartoszkorec.warehouse_management.dto.RouteResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component("twoopt")
@Slf4j
public class TwoOptRouteAlgorithm implements RouteAlgorithm {

    private final NearestNeighborHeuristic heuristic = new NearestNeighborHeuristic();

    @Override
    public RouteResultDto calculate(RouteComputationContext context) {
        long[][] matrix = context.subMatrix();
        if (matrix.length <= 1) {
            return RouteResultAssembler.empty();
        }

        List<Integer> initial = heuristic.buildRoute(matrix, context.depotIndex());
        List<Integer> improved = improveWithTwoOpt(initial, matrix);
        log.info("2-opt route length: {}", computeLength(matrix, improved));
        return RouteResultAssembler.assemble(context, improved);
    }

    private List<Integer> improveWithTwoOpt(List<Integer> seed, long[][] matrix) {
        List<Integer> best = new ArrayList<>(seed);
        boolean improved = true;

        while (improved) {
            improved = false;
            for (int i = 1; i < best.size() - 2; i++) {
                for (int k = i + 1; k < best.size() - 1; k++) {
                    int a = best.get(i - 1);
                    int b = best.get(i);
                    int c = best.get(k);
                    int d = best.get(k + 1);

                    long current = matrix[a][b] + matrix[c][d];
                    long alternative = matrix[a][c] + matrix[b][d];
                    if (alternative < current) {
                        Collections.reverse(best.subList(i, k + 1));
                        improved = true;
                    }
                }
            }
        }
        return best;
    }

    private long computeLength(long[][] matrix, List<Integer> route) {
        long total = 0L;
        for (int i = 0; i < route.size() - 1; i++) {
            total += matrix[route.get(i)][route.get(i + 1)];
        }
        return total;
    }
}
