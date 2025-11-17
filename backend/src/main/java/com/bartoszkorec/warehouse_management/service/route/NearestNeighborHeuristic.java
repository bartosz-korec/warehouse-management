package com.bartoszkorec.warehouse_management.service.route;

import java.util.ArrayList;
import java.util.List;

final class NearestNeighborHeuristic {

    List<Integer> buildRoute(long[][] matrix, int depotIndex) {
        int n = matrix.length;
        if (n == 0) {
            return List.of();
        }

        boolean[] visited = new boolean[n];
        List<Integer> route = new ArrayList<>(n + 1);

        int current = depotIndex;
        visited[current] = true;
        route.add(current);

        for (int step = 1; step < n; step++) {
            int next = findNearest(matrix, visited, current);
            if (next == -1) {
                next = pickAnyUnvisited(visited);
                if (next == -1) {
                    break;
                }
            }
            visited[next] = true;
            route.add(next);
            current = next;
        }

        route.add(depotIndex);
        return route;
    }

    private int findNearest(long[][] matrix, boolean[] visited, int current) {
        long bestCost = Long.MAX_VALUE;
        int bestIdx = -1;

        for (int candidate = 0; candidate < matrix.length; candidate++) {
            if (visited[candidate] || candidate == current) {
                continue;
            }
            long cost = matrix[current][candidate];
            if (cost < bestCost) {
                bestCost = cost;
                bestIdx = candidate;
            }
        }
        return bestIdx;
    }

    private int pickAnyUnvisited(boolean[] visited) {
        for (int i = 0; i < visited.length; i++) {
            if (!visited[i]) {
                return i;
            }
        }
        return -1;
    }
}