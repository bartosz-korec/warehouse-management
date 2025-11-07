package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.model.Location;
import com.bartoszkorec.warehouse_management.model.LocationType;
import com.bartoszkorec.warehouse_management.utils.LocationHelper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LocationService {

    private final AtomicInteger labelCounter = new AtomicInteger(0);
    private final Map<Integer, Location> locationMap = new HashMap<>();
    private Location startingLocation;

    public Location getStartingLocation() {
        if (startingLocation == null) {
            throw new IllegalStateException("Starting location not set");
        }
        return startingLocation;
    }

    public void addLocation(int x, int y, int gridIndex, LocationType type) {
        int label = labelCounter.getAndIncrement();
        Location location = LocationHelper.getNewLocation(gridIndex, x, y, label, type);
        locationMap.put(label, location);
        if (type == LocationType.STARTING_POINT) {
            if (startingLocation != null) {
                throw new IllegalStateException("Starting location already set");
            }
            startingLocation = location;
        }
    }

    public Map<Integer, Location> getLocationMap() {
        Map<Integer, Location> copy = new HashMap<>();
        for (Map.Entry<Integer, Location> entry : locationMap.entrySet()) {
            copy.put(entry.getKey(), LocationHelper.getNewLocation(entry.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }
}
