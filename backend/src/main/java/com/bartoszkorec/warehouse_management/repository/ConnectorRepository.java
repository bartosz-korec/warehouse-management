package com.bartoszkorec.warehouse_management.repository;

import com.bartoszkorec.warehouse_management.model.Connector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConnectorRepository extends JpaRepository<Connector, Integer> {

    Optional<Connector> findByCellValue(Integer cellValue);
}
