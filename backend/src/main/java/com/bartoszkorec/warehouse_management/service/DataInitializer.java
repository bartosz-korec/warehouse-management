package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.model.Authority;
import com.bartoszkorec.warehouse_management.model.Role;
import com.bartoszkorec.warehouse_management.model.User;
import com.bartoszkorec.warehouse_management.repository.UserRepository;
import com.bartoszkorec.warehouse_management.utils.DistanceMatrixHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CsvReader csvReader;
    private final DistanceMatrixCalculator distanceMatrixCalculator;
    private final ResourceLoader resourceLoader;


    @Override
    public void run(String... args) throws Exception {

        System.out.println("Scanning warehouse directory...");
        Resource resource = resourceLoader.getResource("classpath:warehouses");

        if (resource.exists()) {
            List<Path> csvFiles = csvReader.scanFolder(resource.getFile().getPath());
            if (csvFiles != null) {
                System.out.println("Found " + csvFiles.size() + " warehouse grid files");

                // Load each CSV file
                for (Path csvFile : csvFiles) {
                    System.out.println("Loading: " + csvFile.getFileName());
                    int[][] grid = csvReader.read(csvFile);
                    if (grid != null) {
                        distanceMatrixCalculator.addGrid(grid);
                    }
                }

                // Process all grids to identify locations
                distanceMatrixCalculator.convertAllToLocations();

                // Display all locations with their grid index
                System.out.println("\nAll locations:");
                distanceMatrixCalculator.getLocationMap().values().forEach(System.out::println);

                // Calculate distance matrix across all grids
                System.out.println("\nDistance matrix:");
                DistanceMatrixHelper.distanceMatrix = distanceMatrixCalculator.calculateDistanceMatrix();
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

}
