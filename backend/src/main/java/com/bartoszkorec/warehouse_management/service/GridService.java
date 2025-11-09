package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.dto.GridDto;
import com.bartoszkorec.warehouse_management.model.Grid;
import com.bartoszkorec.warehouse_management.repository.GridRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GridService {

    private final GridRepository gridRepository;

    public GridDto createGrid(GridDto gridDto) {
        if (gridDto == null || gridDto.layout() == null || gridDto.layout().length == 0) {
            throw new IllegalArgumentException("Grid cannot be null or empty");
        }
        Grid grid = new Grid();
        grid.setLayout(gridDto.layout());
        grid = gridRepository.save(grid);
        return new GridDto(grid.getId(), grid.getLayout());
    }

    public GridDto getGridById(Integer id) {
        Grid grid = gridRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grid with id " + id + " not found"));
        return new GridDto(grid.getId(), grid.getLayout());
    }

    public List<GridDto> getAllGrids() {
        List<Grid> grids = gridRepository.findAll();
        return grids.stream()
                .map(grid -> new GridDto(grid.getId(), grid.getLayout()))
                .toList();
    }

    public int getCellValueFromGrid(int id, int x, int y) {
        int[][] grid = getGridById(id).layout();
        if (x < 0 || y < 0 || x >= grid.length || y >= grid[0].length) {
            throw new IllegalArgumentException("Position out of bounds");
        }
        return grid[x][y];
    }
}
