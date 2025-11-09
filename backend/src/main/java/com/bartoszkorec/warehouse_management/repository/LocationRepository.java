package com.bartoszkorec.warehouse_management.repository;

import com.bartoszkorec.warehouse_management.model.Location;
import com.bartoszkorec.warehouse_management.model.LocationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    List<Location> findByTypeEquals(LocationType type);
}
