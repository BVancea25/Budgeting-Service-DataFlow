package com.dataflow.budgetingservice.Repositories.Custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class BudgetRepositoryImpl implements BudgetRepositoryCustom{

    private final EntityManager entityManager;

    @Override
    public BigDecimal calculateSpentAmount(String userId, String categoryId, String currencyId, LocalDateTime startDate, LocalDateTime endDate) {
        String sql = """
            SELECT COALESCE(SUM(amount), 0)
            FROM transactions
            WHERE user_id = :userId 
              AND currency_id = :currencyId
              AND category_id = :categoryId 
              AND transaction_date >= :startDate 
              AND transaction_date <= :endDate
              AND type = 'EXPENSE'
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("userId", userId);
        query.setParameter("currencyId", currencyId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        Object result = query.getSingleResult();

        if (result instanceof BigDecimal) {
            return (BigDecimal) result;
        } else if (result instanceof Number) {
            return new BigDecimal(((Number) result).toString());
        }

        return BigDecimal.ZERO;
    }
}
