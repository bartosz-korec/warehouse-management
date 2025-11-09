package com.bartoszkorec.warehouse_management.model;

import java.io.Serial;
import java.io.Serializable;

public record Distance(long distance, int[] connectors) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
