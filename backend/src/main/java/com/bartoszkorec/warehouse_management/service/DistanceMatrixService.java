package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.model.DistanceMatrix;
import com.bartoszkorec.warehouse_management.repository.DistanceMatrixRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistanceMatrixService {

//    public static final String DISTANCE_MATRIX_CACHE = "distanceMatrix";
    private final DistanceMatrixRepository distanceMatrixRepository;

//    @Cacheable(value = DISTANCE_MATRIX_CACHE, key = "1")
    public DistanceMatrix getDistanceMatrix() {
        return distanceMatrixRepository.findById(1)
                .orElseThrow(() -> new IllegalArgumentException("DistanceMatrix not found"));
    }

//    @CachePut(value = DISTANCE_MATRIX_CACHE, key = "1")
    public DistanceMatrix createDistanceMatrix(DistanceMatrix distanceMatrix) {
        return distanceMatrixRepository.save(distanceMatrix);
    }
}
