package com.bartoszkorec.warehouse_management.service.route;

import com.bartoszkorec.warehouse_management.dto.RouteResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component("heldkarp")
@Slf4j
public class HeldKarpRouteAlgorithm implements RouteAlgorithm {

    @Override
    public RouteResultDto calculate(RouteComputationContext context) {
        long[][] subMatrix = context.subMatrix();
        int size = subMatrix.length;
        if (size <= 1) {
            return RouteResultAssembler.empty();
        }

        int start = context.depotIndex();
        int subsetCount = 1 << size;
        long inf = Long.MAX_VALUE / 4;

        long[][] dp = new long[subsetCount][size];
        int[][] parent = new int[subsetCount][size];
        for (int mask = 0; mask < subsetCount; mask++) {
            Arrays.fill(dp[mask], inf);
            Arrays.fill(parent[mask], -1);
        }
        int startMask = 1 << start;
        dp[startMask][start] = 0;

        for (int mask = 0; mask < subsetCount; mask++) {
            if ((mask & startMask) == 0) {
                continue;
            }
            for (int last = 0; last < size; last++) {
                if ((mask & (1 << last)) == 0) {
                    continue;
                }
                long currentCost = dp[mask][last];
                if (currentCost >= inf) {
                    continue;
                }
                for (int next = 0; next < size; next++) {
                    if ((mask & (1 << next)) != 0) {
                        continue;
                    }
                    int nextMask = mask | (1 << next);
                    long newCost = currentCost + subMatrix[last][next];
                    if (newCost < dp[nextMask][next]) {
                        dp[nextMask][next] = newCost;
                        parent[nextMask][next] = last;
                    }
                }
            }
        }

        int fullMask = subsetCount - 1;
        long bestCost = inf;
        int lastCity = start;
        for (int city = 0; city < size; city++) {
            if (city == start) {
                continue;
            }
            long tourCost = dp[fullMask][city] + subMatrix[city][start];
            if (tourCost < bestCost) {
                bestCost = tourCost;
                lastCity = city;
            }
        }
        if (bestCost >= inf) {
            log.warn("Held-Karp algorithm could not produce a valid tour.");
            return RouteResultAssembler.empty();
        }

        List<Integer> route = buildRoute(parent, fullMask, start, lastCity);
        log.info("Held-Karp route length: {}", computeLength(subMatrix, route));
        return RouteResultAssembler.assemble(context, route);
    }

    private static List<Integer> buildRoute(int[][] parent, int mask, int start, int lastCity) {
        List<Integer> route = new ArrayList<>();
        route.add(start);

        List<Integer> stack = new ArrayList<>();
        int currentMask = mask;
        int node = lastCity;
        while (node != start && node != -1) {
            stack.add(node);
            int prev = parent[currentMask][node];
            currentMask ^= (1 << node);
            node = prev;
        }

        Collections.reverse(stack);
        route.addAll(stack);
        route.add(start);
        return route;
    }

    private long computeLength(long[][] matrix, List<Integer> route) {
        long total = 0L;
        for (int i = 0; i < route.size() - 1; i++) {
            total += matrix[route.get(i)][route.get(i + 1)];
        }
        return total;
    }
}
