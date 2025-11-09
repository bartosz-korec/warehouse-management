package com.bartoszkorec.warehouse_management.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record CalculateRouteRequest(@NotEmpty Set<Integer> locationIds) {
}
