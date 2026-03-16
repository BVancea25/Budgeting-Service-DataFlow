package com.dataflow.budgetingservice.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetRequestDTO(
        String categoryId,
        BigDecimal limitAmount,
        String currencyCode,
        String period, // e.g., "MONTHLY"
        LocalDate startDate
) {}
