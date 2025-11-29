package com.bartoszkorec.warehouse_management.service;

import com.bartoszkorec.warehouse_management.dto.LocationDto;
import com.bartoszkorec.warehouse_management.dto.RouteResultDto;
import com.bartoszkorec.warehouse_management.model.Distance;
import com.bartoszkorec.warehouse_management.model.DistanceMatrix;
import com.bartoszkorec.warehouse_management.model.LocationType;
import com.bartoszkorec.warehouse_management.service.route.RouteAlgorithm;
import com.bartoszkorec.warehouse_management.service.route.RouteComputationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@ConditionalOnProperty(name = "benchmark.routing.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class BenchmarkService implements ApplicationRunner {

  private static final Comparator<BenchmarkResult> WINNER_ORDER =
    Comparator.comparingLong(BenchmarkResult::routeLength)
      .thenComparingLong(BenchmarkResult::durationMillis);

  private final DistanceMatrixService distanceMatrixService;
  private final Map<String, RouteAlgorithm> algorithms;

  @Value("${benchmark.routing.iterations:3}")
  private int iterations;

  @Value("${benchmark.routing.sample-sizes:8,12,16,24}")
  private String configuredSampleSizes;

  @Value("${benchmark.routing.algorithms:heldkarp,twoopt,ortools,nearestneighbor}")
  private String configuredAlgorithms;

  @Value("${benchmark.routing.depot-index:0}")
  private int depotPreference;

  @Override
  public void run(ApplicationArguments args) {
    DistanceMatrix storedMatrix = distanceMatrixService.getDistanceMatrix();
    DistanceMatrix nonNullMatrix = storedMatrix == null ? new DistanceMatrix() : storedMatrix;
    Distance[][] matrix = nonNullMatrix.getMatrix();
    if (matrix == null || matrix.length == 0) {
      log.warn("Distance matrix is empty, skipping routing benchmark.");
      return;
    }

    List<Integer> sampleSizes = resolveSampleSizes(matrix.length);
    List<String> algorithmOrder = resolveAlgorithmOrder();

    for (int sampleSize : sampleSizes) {
      runBenchmark(matrix, sampleSize, algorithmOrder);
    }
  }

  private void runBenchmark(Distance[][] matrix, int sampleSize, List<String> algorithmOrder) {
    if (sampleSize < 2) {
      log.warn("Sample size {} is too small, skipping.", sampleSize);
      return;
    }

    LocationDto depot = buildSyntheticDepot(sampleSize);
    Set<Integer> sampleNodeIds = buildSampleNodeIds(sampleSize);

    List<BenchmarkResult> results = new ArrayList<>();
    for (String algorithmKey : algorithmOrder) {
      RouteAlgorithm algorithm = algorithms.get(algorithmKey);
      if (algorithm == null) {
        log.warn("Routing algorithm '{}' not registered, skipping.", algorithmKey);
        continue;
      }
      results.add(measure(algorithmKey, algorithm, matrix, sampleNodeIds, depot));
    }

    if (results.isEmpty()) {
      log.warn("No routing algorithms produced results for sample size {}", sampleSize);
      return;
    }

    logSummary(results);
    BenchmarkResult winner = results.stream().min(WINNER_ORDER).orElseThrow();
    log.info("Routing benchmark winner: {} (length={}, time={}ms)",
      winner.algorithm(), winner.routeLength(), winner.durationMillis());
    if (!"ortools".equalsIgnoreCase(winner.algorithm())
      && algorithmOrder.stream().anyMatch("ortools"::equalsIgnoreCase)) {
      log.warn("Expected OR-Tools to dominate, but {} performed better.", winner.algorithm());
    }
  }

  private BenchmarkResult measure(String name,
                                  RouteAlgorithm algorithm,
                                  Distance[][] distanceMatrix,
                                  Set<Integer> sampleNodeIds,
                                  LocationDto depot) {
    long totalLength = 0L;
    long totalTimeNanos = 0L;
    int safeIterations = Math.max(1, iterations);

    for (int i = 0; i < safeIterations; i++) {
      RouteComputationContext context = RouteComputationContext.from(sampleNodeIds, depot, distanceMatrix);
      long start = System.nanoTime();
      RouteResultDto result = algorithm.calculate(context);
      totalTimeNanos += System.nanoTime() - start;
      totalLength += result.totalDistance();
    }

    long avgLength = totalLength / safeIterations;
    long avgTimeMs = TimeUnit.NANOSECONDS.toMillis(totalTimeNanos / safeIterations);
    log.info("Algorithm {} -> avg length={}, avg time={}ms", name, avgLength, avgTimeMs);
    return new BenchmarkResult(name, avgLength, avgTimeMs);
  }

  private void logSummary(List<BenchmarkResult> results) {
    StringBuilder builder = new StringBuilder(System.lineSeparator());
    builder.append(" | Algorithm            | Avg length   | Avg time [ms] |").append(System.lineSeparator());
    builder.append(" |----------------------|--------------|---------------|").append(System.lineSeparator());
    for (BenchmarkResult result : results) {
      builder.append(String.format(" | %-20s | %-12d | %-13d |%n",
        result.algorithm(), result.routeLength(), result.durationMillis()));
    }
    log.info(builder.toString());
  }

  private List<Integer> resolveSampleSizes(int maxSize) {
    List<Integer> sizes = Arrays.stream(configuredSampleSizes.split(","))
      .map(String::trim)
      .filter(s -> !s.isEmpty())
      .mapToInt(Integer::parseInt)
      .map(size -> Math.min(size, maxSize))
      .filter(size -> size >= 2)
      .distinct()
      .boxed()
      .toList();
    return sizes.isEmpty() ? List.of(Math.min(8, maxSize)) : sizes;
  }

  private List<String> resolveAlgorithmOrder() {
    List<String> configured = Arrays.stream(configuredAlgorithms.split(","))
      .map(String::trim)
      .map(String::toLowerCase)
      .filter(s -> !s.isEmpty())
      .toList();
    return configured.isEmpty() ? new ArrayList<>(algorithms.keySet()) : configured;
  }

  private Set<Integer> buildSampleNodeIds(int sampleCount) {
    return IntStream.rangeClosed(1, sampleCount)
      .boxed()
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private LocationDto buildSyntheticDepot(int sampleCount) {
    return new LocationDto(normalizeDepotIndex(sampleCount), null, LocationType.STARTING_POINT);
  }

  private int normalizeDepotIndex(int sampleCount) {
    if (sampleCount <= 0) {
      return 1;
    }
    int normalized = depotPreference % sampleCount;
    if (normalized < 0) {
      normalized += sampleCount;
    }
    return normalized + 1;
  }

  private record BenchmarkResult(String algorithm, long routeLength, long durationMillis) {
  }
}
