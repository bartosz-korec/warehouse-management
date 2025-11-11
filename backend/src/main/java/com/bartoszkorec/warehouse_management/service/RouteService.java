package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.dto.LocationDto;
import com.bartoszkorec.warehouse_management.dto.RouteResultDto;
import com.bartoszkorec.warehouse_management.model.Distance;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class RouteService {

    private final DistanceMatrixService distanceMatrixService;

    public RouteResultDto calculateSolution(Set<Integer> locations, LocationDto startingLocation) {

        Distance[][] distanceMatrix = distanceMatrixService.getDistanceMatrix().getMatrix();

        if (locations.stream().noneMatch(locationId -> locationId.equals(startingLocation.id()))) {
            locations.add(startingLocation.id());
        }

        int[] trackingArray = locations.stream().sorted().mapToInt(Integer::intValue).map(integer -> integer - 1).toArray();

        // Find the position of starting location in the submatrix
        int depotIndex = -1;
        for (int i = 0; i < trackingArray.length; i++) {
            if (trackingArray[i] == startingLocation.id() - 1) {
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
        if (solution == null) {
            log.warn("No route solution found.");
            return new RouteResultDto(List.of(), List.of(), 0L);
        }

        // Solution cost
        log.info("Objective: {} units", solution.objectiveValue());

        // Inspect solution
        long routeDistance = 0L;
        List<Integer> ordered = new ArrayList<>();
        List<List<Integer>> connectorsPerLeg = new ArrayList<>();
        StringBuilder routeLog = new StringBuilder();

        long index = routing.start(0);
        int startNode = manager.indexToNode(index);
        int startOriginal = trackingArray[startNode];
        ordered.add(startOriginal + 1);
        routeLog.append(startOriginal + 1);

        while (!routing.isEnd(index)) {
            long prevIndex = index;
            index = solution.value(routing.nextVar(index));

            int fromNode = manager.indexToNode(prevIndex);
            int toNode = manager.indexToNode(index);
            int fromOriginal = trackingArray[fromNode];
            int toOriginal = trackingArray[toNode];

            Distance leg = distanceMatrix[fromOriginal][toOriginal];
            int[] connectors = leg.connectors();

            List<Integer> connectorList = new ArrayList<>();
            if (connectors != null && connectors.length > 0) {
                routeLog.append(" --[");
                for (int i = 0; i < connectors.length; i++) {
                    if (i > 0) routeLog.append(",");
                    routeLog.append("C").append(connectors[i]);
                    connectorList.add(connectors[i]);
                }
                routeLog.append("]--> ");
            } else {
                routeLog.append(" --> ");
            }
            routeLog.append(toOriginal + 1);

            connectorsPerLeg.add(connectorList);
            ordered.add(toOriginal + 1);

            routeDistance += routing.getArcCostForVehicle(prevIndex, index, 0);
        }

        log.info("Route: {}", routeLog);
        log.info("Route distance: {} units", routeDistance);

        return new RouteResultDto(ordered, connectorsPerLeg, routeDistance);
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
