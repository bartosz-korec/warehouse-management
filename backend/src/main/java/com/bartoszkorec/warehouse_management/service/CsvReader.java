package com.bartoszkorec.warehouse_management.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Component
public class CsvReader {

    public List<Path> scanFolder(String folderPath) {

        Path path = Path.of(folderPath);
        if (!Files.exists(path)) {
            System.out.println("Folder not found: " + folderPath);
            return null;
        }

        if (!Files.isDirectory(path)) {
            System.out.println("Path is not a directory: " + folderPath);
            return null;
        }

        List<Path> filePaths;
        try (Stream<Path> paths = Files.list(path)) {
            filePaths = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".csv"))
                    .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                    .toList();
        } catch (IOException e) {
            System.out.println("Error scanning folder: " + e.getMessage());
            return null;
        }
        return filePaths;
    }

    public int[][] read(Path path) {

        if (!Files.exists(path)) {
            System.out.println("File not found: " + path);
            return null;
        }

        int[][] data = new int[0][];

        try (Stream<String> lines = Files.lines(path)) {
            data = lines
                    .map(line -> line.split(","))
                    .map(array -> Arrays.stream(array)
                            .map(String::trim)
                            .filter(s -> s.matches("\\d+"))
                            .mapToInt(Integer::parseInt)
                            .toArray())
                    .toArray(int[][]::new);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return data;
    }
}
