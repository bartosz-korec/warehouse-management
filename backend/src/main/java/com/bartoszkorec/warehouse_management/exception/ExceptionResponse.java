package com.bartoszkorec.warehouse_management.exception;

import lombok.Builder;

@Builder
public record ExceptionResponse(int status, String message, long timeStamp) {
}
