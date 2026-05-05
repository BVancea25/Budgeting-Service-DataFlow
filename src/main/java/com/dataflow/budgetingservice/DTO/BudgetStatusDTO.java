package com.dataflow.budgetingservice.DTO;

import com.dataflow.budgetingservice.Models.BudgetPeriod;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BudgetStatusDTO(
        String budgetId,
        String categoryName,
        BigDecimal limitAmount,
        BigDecimal spentAmount,
        BigDecimal remainingAmount,
        double progressPercentage,
        String status,
        BudgetPeriod period,
        String currencyCode,
        LocalDate startDate
) {}
