package com.bartoszkorec.warehouse_management.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DistanceMatrixConverter implements AttributeConverter<Distance[][], String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Distance[][] distances) {
        try {
            return distances == null ? null : objectMapper.writeValueAsString(distances);
        } catch (Exception e) {
            throw new RuntimeException("Error converting Distance[][] to JSON", e);
        }
    }

    @Override
    public Distance[][] convertToEntityAttribute(String s) {
        try {
            return s == null ? null : objectMapper.readValue(s, Distance[][].class);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to Distance[][]", e);
        }
    }
}
