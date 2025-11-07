package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.model.Distance;
import com.bartoszkorec.warehouse_management.model.Location;
import com.bartoszkorec.warehouse_management.model.LocationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DistanceMatrixCalculator {

    private final GridService gridService;
    private final LocationService locationService;
    private final PathFinder pathFinder;

    public Location getStartingLocation() {
        return locationService.getStartingLocation();
    }

    public void addGrid(int[][] grid) {
        gridService.addGrid(grid);
    }

    public Map<Integer, Location> getLocationMap() {
        return locationService.getLocationMap();
    }

    public Distance[][] calculateDistanceMatrix() {
        Map<Integer, Location> locationMap = locationService.getLocationMap();
        int size = locationMap.size();
        Distance[][] distanceMatrix = new Distance[size][size];

        for (int fromIdx = 0; fromIdx < size; fromIdx++) {
            for (int toIdx = 0; toIdx < size; toIdx++) {
                if (fromIdx == toIdx) {
                    distanceMatrix[fromIdx][toIdx] = new Distance(0, new int[0]);
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

    public void convertAllToLocations() {
        for (int i = 0; i < gridService.getGridsSize(); i++) {
            convertToLocations(i);
        }
    }

    public void convertToLocations(int gridIndex) {
        int[][] grid = gridService.getGrid(gridIndex);

        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                int cellValue = grid[x][y];

                if (cellValue == LocationType.PICKUP_POINT.getLabel()) {
                    locationService.addLocation(x, y, gridIndex, LocationType.PICKUP_POINT);
                } else if (cellValue == LocationType.STARTING_POINT.getLabel()) {
                    locationService.addLocation(x, y, gridIndex, LocationType.STARTING_POINT);
                }
            }
        }
    }
}
