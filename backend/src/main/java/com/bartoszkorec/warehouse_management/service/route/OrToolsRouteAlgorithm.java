package com.bartoszkorec.warehouse_management.service.route;

import com.bartoszkorec.warehouse_management.dto.RouteResultDto;
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.google.ortools.constraintsolver.main.defaultRoutingSearchParameters;

@Component("ortools")
@Slf4j
public class OrToolsRouteAlgorithm implements RouteAlgorithm {

    @Override
    public RouteResultDto calculate(RouteComputationContext context) {
        if (context.subMatrix().length <= 1) {
            return RouteResultAssembler.empty();
        }

        Loader.loadNativeLibraries();
        RoutingIndexManager manager =
                new RoutingIndexManager(context.subMatrix().length, 1, context.depotIndex());

        RoutingModel routing = new RoutingModel(manager);
        final int transitCallbackIndex =
                routing.registerTransitCallback((long fromIndex, long toIndex) -> {
                    int fromNode = manager.indexToNode(fromIndex);
                    int toNode = manager.indexToNode(toIndex);
                    return context.subMatrix()[fromNode][toNode];
                });
        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);

        RoutingSearchParameters searchParameters =
                defaultRoutingSearchParameters()
                        .toBuilder()
                        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
                        .build();

        Assignment solution = routing.solveWithParameters(searchParameters);
        if (solution == null) {
            log.warn("No OR-Tools route solution found.");
            return RouteResultAssembler.empty();
        }

        List<Integer> subRoute = new ArrayList<>();
        long index = routing.start(0);
        subRoute.add(manager.indexToNode(index));
        while (!routing.isEnd(index)) {
            index = solution.value(routing.nextVar(index));
            subRoute.add(manager.indexToNode(index));
        }

        log.info("OR-Tools objective value: {}", solution.objectiveValue());
        return RouteResultAssembler.assemble(context, subRoute);
    }
}
