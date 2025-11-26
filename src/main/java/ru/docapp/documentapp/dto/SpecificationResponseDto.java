package ru.docapp.documentapp.dto;

import java.math.BigDecimal;

public record SpecificationResponseDto(
        Long id,
        String name,
        BigDecimal amount
) {}