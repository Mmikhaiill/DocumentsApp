package ru.docapp.documentapp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DocumentResponseDto(
        Long id,
        String number,
        LocalDate date,
        BigDecimal amount,
        String note,
        List<SpecificationResponseDto> specifications
) {}