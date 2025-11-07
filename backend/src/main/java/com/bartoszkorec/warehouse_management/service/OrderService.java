package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.model.Distance;
import com.bartoszkorec.warehouse_management.model.Location;
import com.bartoszkorec.warehouse_management.utils.DistanceMatrixHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final RouteService routeService;
    private final DistanceMatrixCalculator distanceMatrixCalculator;

    public String generateRandomizedOrder() {
        Distance[][] distanceMatrix = DistanceMatrixHelper.distanceMatrix;
        Location startingLocation = distanceMatrixCalculator.getStartingLocation();
        int startingLocationIndex = startingLocation.label();

        // Generate random indices excluding the starting location
        Set<Integer> randomIndices = new HashSet<>();
        randomIndices.add(startingLocationIndex);

        Random random = new Random();
        int randomCount = random.nextInt(distanceMatrix.length - 1) + 1;

        while (randomIndices.size() < randomCount) {
            int randomIndex = random.nextInt(distanceMatrix.length);
            randomIndices.add(randomIndex);
        }

        // Convert to comma-separated string
        String input = randomIndices.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return routeService.calculateSolution(input);
    }
}
