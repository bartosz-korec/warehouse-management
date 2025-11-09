package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.model.DistanceMatrix;
import com.bartoszkorec.warehouse_management.repository.DistanceMatrixRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistanceMatrixService {

    private final DistanceMatrixRepository distanceMatrixRepository;

    public DistanceMatrix getDistanceMatrix() {
        return distanceMatrixRepository.findById(1)
                .orElseThrow(() -> new IllegalArgumentException("DistanceMatrix not found"));
    }

    public DistanceMatrix createDistanceMatrix(DistanceMatrix distanceMatrix) {
        return distanceMatrixRepository.save(distanceMatrix);
    }
}
