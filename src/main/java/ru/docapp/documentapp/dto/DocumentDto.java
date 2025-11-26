package ru.docapp.documentapp.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DocumentDto(
        Long id,

        @NotBlank @Size(max = 50)
        String number,

        @NotNull
        LocalDate date,

        BigDecimal amount,

        String note,

        @NotEmpty
        List<SpecificationDto> specifications
) {}