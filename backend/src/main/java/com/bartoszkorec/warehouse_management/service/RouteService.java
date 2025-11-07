package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.model.Distance;
import com.bartoszkorec.warehouse_management.model.Location;
import com.bartoszkorec.warehouse_management.utils.DistanceMatrixHelper;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final DistanceMatrixCalculator distanceMatrixCalculator;

    public String calculateSolution(String input) {

        // Get starting location and its index in the original matrix
        Location startingLocation = distanceMatrixCalculator.getStartingLocation();
        System.out.println("Starting location: " + startingLocation);
        int startingLocationIndex = startingLocation.label();
        boolean startingLocationIncluded = false;

        Distance[][] distanceMatrix = DistanceMatrixHelper.distanceMatrix;
        String[] selectedIndicesStr = input.split(",");
        Set<Integer> uniqueIndices = new HashSet<>();
        for (String indexStr : selectedIndicesStr) {
            try {
                int index = Integer.parseInt(indexStr.trim());
                if (index >= 0 && index < distanceMatrix.length) {
                    uniqueIndices.add(index);
                    if (index == startingLocationIndex) {
                        startingLocationIncluded = true;
                    }
                } else {
                    System.out.println("Ignoring invalid index: " + index);
                }
            } catch (NumberFormatException e) {
                System.out.println("Ignoring non-integer input: " + indexStr);
            }
        }

        // Add starting location if not already included
        if (!startingLocationIncluded) {
            System.out.println("Adding starting location (index " + startingLocationIndex + ") to selection");
            uniqueIndices.add(startingLocationIndex);
        }

        // Convert set to tracking array
        int[] trackingArray = new int[uniqueIndices.size()];
        int trackingArrayIndex = 0;
        for (Integer index : uniqueIndices) {
            trackingArray[trackingArrayIndex++] = index;
        }

        // Find the position of starting location in the submatrix
        int depotIndex = -1;
        for (int i = 0; i < trackingArray.length; i++) {
            if (trackingArray[i] == startingLocationIndex) {
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

        System.out.println(detailedRoute);
        System.out.println("Route distance: " + routeDistance + " units");
        return detailedRoute.toString();
    }
}
