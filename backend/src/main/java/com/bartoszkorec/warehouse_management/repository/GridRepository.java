package com.bartoszkorec.warehouse_management.repository;

import com.bartoszkorec.warehouse_management.model.Grid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GridRepository extends JpaRepository<Grid, Integer> {
}
