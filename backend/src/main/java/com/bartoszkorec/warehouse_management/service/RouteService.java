package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.dto.LocationDto;
import com.bartoszkorec.warehouse_management.model.Distance;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class RouteService {

    public String calculateSolution(Set<Integer> locations, Distance[][] distanceMatrix, LocationDto startingLocation) {

        if (locations.stream().noneMatch(locationId -> locationId.equals(startingLocation.id()))) {
            locations.add(startingLocation.id());
        }

        int[] trackingArray = locations.stream().sorted().mapToInt(Integer::intValue).toArray();

        // Find the position of starting location in the submatrix
        int depotIndex = -1;
        for (int i = 0; i < trackingArray.length; i++) {
            if (trackingArray[i] == startingLocation.id()) {
                depotIndex = i;
                break;
            }
        }

        // Create sub-matrix
        int subMatrixSize = trackingArray.length;
        long[][] subMatrix = new long[subMatrixSize][subMatrixSize];

        // Fill the sub-matrix based on selected indices
        for (int i = 0; i < subMatrixSize; i++) {
            for (int j = 0; j < subMatrixSize; j++) {
                int originalI = trackingArray[i];
                int originalJ = trackingArray[j];
                subMatrix[i][j] = distanceMatrix[originalI][originalJ].distance();
            }
        }

        // OR-Tools setup
        Loader.loadNativeLibraries();
        // Create Routing Index Manager
        RoutingIndexManager manager =
                new RoutingIndexManager(subMatrix.length, 1, depotIndex);

        // Create Routing Model.
        RoutingModel routing = getRoutingModel(manager, subMatrix);
        RoutingSearchParameters searchParameters =
                main.defaultRoutingSearchParameters()
                        .toBuilder()
                        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                        .build();

        // Solve the problem.
        Assignment solution = routing.solveWithParameters(searchParameters);

        // Solution cost
        System.out.println("Objective: " + solution.objectiveValue() + " units");

        // Inspect solution
        System.out.println("Route (with connectors):");
        long routeDistance = 0;
        StringBuilder detailedRoute = new StringBuilder();

        long index = routing.start(0);
        long previousIndex = index;
        int previousNodeIndex = manager.indexToNode(previousIndex);
        int previousOriginalIndex = trackingArray[previousNodeIndex];

        detailedRoute.append(previousOriginalIndex);

        while (!routing.isEnd(index)) {
            previousIndex = index;
            previousNodeIndex = manager.indexToNode(previousIndex);
            previousOriginalIndex = trackingArray[previousNodeIndex];

            index = solution.value(routing.nextVar(index));
            int nodeIndex = manager.indexToNode(index);
            int originalIndex = trackingArray[nodeIndex];

            // Get connector information from original distance matrix
            int[] connectors = distanceMatrix[previousOriginalIndex][originalIndex].connectors();

            // Add connector info to output if connectors are used
            if (connectors.length > 0) {
                detailedRoute.append(" --[");
                for (int i = 0; i < connectors.length; i++) {
                    if (i > 0) detailedRoute.append(",");
                    detailedRoute.append("C").append(connectors[i]);
                }
                detailedRoute.append("]--> ");
            } else {
                detailedRoute.append(" --> ");
            }
            detailedRoute.append(originalIndex);

            routeDistance += routing.getArcCostForVehicle(previousIndex, index, 0);
        }

        log.info("{}", detailedRoute);
        log.info("Route distance: {} units", routeDistance);
        return detailedRoute.toString();
    }

    private static RoutingModel getRoutingModel(RoutingIndexManager manager, long[][] subMatrix) {
        RoutingModel routing = new RoutingModel(manager);

        // Create and register a transit callback.
        final int transitCallbackIndex =
                routing.registerTransitCallback((long fromIndex, long toIndex) -> {
                    // Convert from routing variable Index to user NodeIndex.
                    int fromNode = manager.indexToNode(fromIndex);
                    int toNode = manager.indexToNode(toIndex);
                    return subMatrix[fromNode][toNode];
                });

        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);
        return routing;
    }
}
