package com.bartoszkorec.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GridService {

    private final List<int[][]> grids = new ArrayList<>();
    private final ConnectorService connectorService;

    public void addGrid(int[][] grid) {
        if (grid == null || grid.length == 0) {
            throw new IllegalArgumentException("Grid cannot be null or empty");
        }
        grids.add(grid);
        connectorService.updateConnectors(grid, grids.size() - 1);
    }

    public int[][] getGrid(int index) {
        if (index < 0 || index >= grids.size()) {
            throw new IllegalArgumentException("Invalid grid index: " + index);
        }
        return grids.get(index);
    }

    public int getGridsSize() {
        return grids.size();
    }

    public int getCellValue(int gridIndex, int x, int y) {
        int[][] grid = getGrid(gridIndex);
        if (x < 0 || y < 0 || x >= grid.length || y >= grid[0].length) {
            throw new IllegalArgumentException("Position out of bounds");
        }
        return grid[x][y];
    }
}
