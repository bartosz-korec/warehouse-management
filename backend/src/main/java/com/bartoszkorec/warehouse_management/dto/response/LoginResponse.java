package com.bartoszkorec.warehouse_management.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(
        String token,
        long expiresIn
) {
}
