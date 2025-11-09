package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.dto.GridDto;
import com.bartoszkorec.warehouse_management.dto.LocationDto;
import com.bartoszkorec.warehouse_management.model.*;
import com.bartoszkorec.warehouse_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CsvReader csvReader;
    private final DistanceMatrixCalculator distanceMatrixCalculator;
    private final ResourceLoader resourceLoader;
    private final GridService gridService;
    private final LocationService locationService;
    private final ConnectorService connectorService;
    private final DistanceMatrixService distanceMatrixService;

    @Override
    public void run(String... args) throws Exception {

        if (!gridService.getAllGrids().isEmpty()) {
            log.info("Grids already initialized, skipping data initialization.");
        } else {
            log.info("Scanning warehouse directory...");
            Resource resource = resourceLoader.getResource("classpath:warehouses");

            if (resource.exists()) {
                List<Path> csvFiles = csvReader.scanFolder(resource.getFile().getPath());
                if (csvFiles != null) {
                    log.info("Found {} warehouse grid files", csvFiles.size());

                    // Load each CSV file
                    for (Path csvFile : csvFiles) {
                        log.info("Loading: {}", csvFile.getFileName());
                        int[][] grid = csvReader.read(csvFile);
                        if (grid != null) {
                            gridService.createGrid(new GridDto(null, grid));
                        }
                    }

                    // Process all grids to identify locations
                    convertAllToLocations();

                    List<LocationDto> locations = locationService.getAllLocations();
                    log.info("All locations:");
                    locations.forEach(l -> log.info("{}", l));

                    // Calculate distance matrix across all grids
                    DistanceMatrix distanceMatrix = new DistanceMatrix(1, distanceMatrixCalculator.calculateDistanceMatrix(locations));
                    log.info("Distance matrix:");
                    for (Distance[] row : distanceMatrix.getMatrix()) {
                        log.info("{}", Arrays.toString(Arrays.stream(row)
                                .map(d -> String.format("(d:%d,c:%s)", d.distance(), Arrays.toString(d.connectors())))
                                .toArray()));
                    }
                    distanceMatrixService.createDistanceMatrix(distanceMatrix);
                }
            }
        }

        String email = "user@test.com";
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setFirstName("Test");
            user.setLastName("User");
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode("test"));
            user.setAuthorities(List.of(new Authority(Role.ROLE_EMPLOYEE)));

            userRepository.save(user);
        }
    }


    public void convertAllToLocations() {
        gridService.getAllGrids().forEach(this::convertToLocations);
    }

    public void convertToLocations(GridDto gridDto) {
        int[][] grid = gridDto.layout();
        int gridIndex = gridDto.id();

        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[0].length; y++) {
                int cellValue = grid[x][y];

                if (cellValue == LocationType.PICKUP_POINT.getLabel()) {
                    locationService.createLocation(new LocationDto(null, new Point(gridIndex, x, y), LocationType.PICKUP_POINT));
                } else if (cellValue == LocationType.STARTING_POINT.getLabel()) {
                    locationService.createLocation(new LocationDto(null, new Point(gridIndex, x, y), LocationType.STARTING_POINT));
                }
            }
        }
        connectorService.createConnectorsFromGrid(gridDto);
    }
}
