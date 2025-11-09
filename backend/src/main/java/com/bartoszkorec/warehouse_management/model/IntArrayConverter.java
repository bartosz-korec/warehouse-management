package com.bartoszkorec.warehouse_management.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class IntArrayConverter implements AttributeConverter<int[][], String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(int[][] ints) {
        try {
            return ints == null ? null : objectMapper.writeValueAsString(ints);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting array to JSON", e);
        }
    }

    @Override
    public int[][] convertToEntityAttribute(String s) {
        try {
            return s == null ? null : objectMapper.readValue(s, int[][].class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting JSON to array", e);
        }
    }
}
