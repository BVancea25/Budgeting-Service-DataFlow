package com.dataflow.budgetingservice.DTO;

import java.math.BigDecimal;

public record BudgetUpdateRequestDTO(
        BigDecimal limitAmount,
        Boolean isActive,
        String currencyId
) {}
