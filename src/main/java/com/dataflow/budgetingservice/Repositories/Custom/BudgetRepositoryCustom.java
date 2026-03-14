package com.dataflow.budgetingservice.Repositories.Custom;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface BudgetRepositoryCustom {
    BigDecimal calculateSpentAmount(
            String userId,
            String categoryId,
            String currencyId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
