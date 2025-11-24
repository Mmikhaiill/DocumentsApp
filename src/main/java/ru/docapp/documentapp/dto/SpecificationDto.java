package ru.docapp.documentapp.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record SpecificationDto(
        Long id,

        @NotBlank @Size(max = 255)
        String name,

        @NotNull @DecimalMin(value = "0.01", message = "Amount must be positive")
        BigDecimal amount
) {}