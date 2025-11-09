package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.dto.LocationDto;
import com.bartoszkorec.warehouse_management.model.Distance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final RouteService routeService;

    public String generateRandomizedOrder(Distance[][] distanceMatrix, LocationDto startingLocation) {
        // Generate random indices excluding the starting location
        Set<Integer> randomIndices = new HashSet<>();
        randomIndices.add(startingLocation.id());

        Random random = new Random();
        int randomCount = random.nextInt(distanceMatrix.length - 1) + 1;

        while (randomIndices.size() < randomCount) {
            int randomIndex = random.nextInt(distanceMatrix.length - 1);
            randomIndices.add(randomIndex);
        }

        return calculateRouteForLocationIds(randomIndices, distanceMatrix, startingLocation);
    }

    public String calculateRouteForLocationIds(Set<Integer> indices, Distance[][] distanceMatrix, LocationDto startingLocation) {

        log.info("Generated randomized order with {} locations: {}", indices.size(), indices);
        String result = routeService.calculateSolution(indices, distanceMatrix, startingLocation);
        log.debug("Calculated solution for order: {}", result);

        return result;
    }
}
