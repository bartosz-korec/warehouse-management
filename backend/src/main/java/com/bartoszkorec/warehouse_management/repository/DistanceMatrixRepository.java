package com.bartoszkorec.warehouse_management.repository;

import com.bartoszkorec.warehouse_management.model.DistanceMatrix;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DistanceMatrixRepository extends JpaRepository<DistanceMatrix, Integer> {
}
