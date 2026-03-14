package com.dataflow.budgetingservice.DTO;

import java.math.BigDecimal;

public record BudgetStatusDTO(
        String budgetId,
        String categoryName,
        BigDecimal limitAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        double progressPercentage,
        String status // "ON_TRACK", "WARNING", "EXCEEDED"
) {}
