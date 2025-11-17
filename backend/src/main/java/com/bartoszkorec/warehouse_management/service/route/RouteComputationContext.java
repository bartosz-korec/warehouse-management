package com.bartoszkorec.warehouse_management.service.route;

import com.bartoszkorec.warehouse_management.dto.LocationDto;
import com.bartoszkorec.warehouse_management.model.Distance;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public record RouteComputationContext(
        Distance[][] distanceMatrix,
        long[][] subMatrix,
        int[] trackingArray,
        int depotIndex
) {
    public static RouteComputationContext from(Set<Integer> locations,
                                               LocationDto startingLocation,
                                               Distance[][] distanceMatrix) {
        if (startingLocation == null) {
            throw new IllegalArgumentException("Starting location is required.");
        }
        if (distanceMatrix == null || distanceMatrix.length == 0) {
            throw new IllegalArgumentException("Distance matrix is required.");
        }

        Set<Integer> merged = new HashSet<>(locations);
        merged.add(startingLocation.id());

        int[] trackingArray = merged.stream()
                .sorted()
                .mapToInt(i -> i - 1)
                .toArray();

        int depotIndex = -1;
        for (int i = 0; i < trackingArray.length; i++) {
            if (trackingArray[i] == startingLocation.id() - 1) {
                depotIndex = i;
                break;
            }
        }
        if (depotIndex < 0) {
            throw new IllegalStateException("Depot index could not be resolved.");
        }

        long[][] subMatrix = new long[trackingArray.length][trackingArray.length];
        for (int i = 0; i < trackingArray.length; i++) {
            for (int j = 0; j < trackingArray.length; j++) {
                subMatrix[i][j] = distanceMatrix[trackingArray[i]][trackingArray[j]].distance();
            }
        }

        return new RouteComputationContext(distanceMatrix, subMatrix, trackingArray, depotIndex);
    }

    public int toOriginalNode(int subIndex) {
        return trackingArray[subIndex];
    }

    @NotNull
    @Override
    public String toString() {
        return "RouteComputationContext{" +
                "trackingArray=" + Arrays.toString(trackingArray) +
                ", depotIndex=" + depotIndex +
                '}';
    }
}
