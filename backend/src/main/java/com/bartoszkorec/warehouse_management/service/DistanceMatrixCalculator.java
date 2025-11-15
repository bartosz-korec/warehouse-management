package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.dto.LocationDto;
import com.bartoszkorec.warehouse_management.model.Distance;
import com.bartoszkorec.warehouse_management.utils.DistanceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DistanceMatrixCalculator {

    private final PathFinder pathFinder;

    public Distance[][] calculateDistanceMatrix(List<LocationDto> locations) {

        Map<Integer, LocationDto> locationMap = locations.stream()
                .collect(Collectors.toMap(locationDto -> locationDto.id() - 1, Function.identity()));
        int size = locationMap.size();
        Distance[][] distanceMatrix = new Distance[size][size];

        for (int fromIdx = 0; fromIdx < size; fromIdx++) {
            for (int toIdx = 0; toIdx < size; toIdx++) {
                if (fromIdx == toIdx) {
                    distanceMatrix[fromIdx][toIdx] = DistanceHelper.getNewDistance(0, null, null);
                } else {
                    distanceMatrix[fromIdx][toIdx] = pathFinder.findShortestPath(
                            locationMap.get(fromIdx),
                            locationMap.get(toIdx)
                    );
                }
            }
        }

        return distanceMatrix;
    }
}
