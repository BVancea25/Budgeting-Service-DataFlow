package com.dataflow.budgetingservice.DTO;

import java.math.BigDecimal;

public record BudgetRequestDTO(
        String userId,
        String categoryId,
        BigDecimal limitAmount,
        String currencyId,
        String period // e.g., "MONTHLY"
) {}
