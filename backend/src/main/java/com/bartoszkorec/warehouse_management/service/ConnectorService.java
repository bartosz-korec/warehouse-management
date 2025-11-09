package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.dto.ConnectorDto;
import com.bartoszkorec.warehouse_management.dto.GridDto;
import com.bartoszkorec.warehouse_management.model.Connector;
import com.bartoszkorec.warehouse_management.model.LocationType;
import com.bartoszkorec.warehouse_management.model.Point;
import com.bartoszkorec.warehouse_management.repository.ConnectorRepository;
import com.bartoszkorec.warehouse_management.utils.ConnectorHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectorService {

    private final ConnectorRepository connectorRepository;

    public ConnectorDto getConnectorByCellValue(int cellValue) {
        Connector connector = connectorRepository.findByCellValue(cellValue)
                .orElseThrow(() -> new IllegalArgumentException("Connector with cell value " + cellValue + " not found"));
        return ConnectorHelper.toDto(connector);
    }

    public void createConnectorsFromGrid(GridDto gridDto) {

        int[][] layout = gridDto.layout();
        for (int row = 0; row < layout.length; row++) {
            for (int col = 0; col < layout[0].length; col++) {
                int cellValue = layout[row][col];
                if (cellValue >= LocationType.CONNECTOR_MIN_VALUE.getLabel()) {
                    log.info("Adding connector: {} grid={} at ({}, {})", cellValue, gridDto.id(), row, col);
                    Connector connector = connectorRepository.findByCellValue(cellValue)
                            .orElse(ConnectorHelper.createNewConnector(cellValue));

                    Point point = new Point(gridDto.id(), row, col);

                    try {
                        ConnectorHelper.addPointToConnector(connector, point);
                        connectorRepository.save(connector);
                    } catch (IllegalArgumentException | IllegalStateException e) {
                        log.warn("Warning: {}", e.getMessage());
                    }
                }
            }
        }
    }
}
