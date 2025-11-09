package com.bartoszkorec.warehouse_management.dto;

import java.io.Serial;
import java.io.Serializable;

public record GridDto(Integer id, int[][] layout) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
