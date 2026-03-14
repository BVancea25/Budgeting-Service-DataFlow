package com.dataflow.budgetingservice.Services;

import com.dataflow.budgetingservice.DTO.BudgetRequestDTO;
import com.dataflow.budgetingservice.DTO.BudgetStatusDTO;
import com.dataflow.budgetingservice.DTO.BudgetUpdateRequestDTO;
import com.dataflow.budgetingservice.Models.Budget;
import com.dataflow.budgetingservice.Models.Category;
import com.dataflow.budgetingservice.Models.Currency;
import com.dataflow.budgetingservice.Repositories.BudgetRepository;
import com.dataflow.budgetingservice.Repositories.CategoryRepository;
import com.dataflow.budgetingservice.Repositories.CurrencyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final CurrencyRepository currencyRepository;

    public List<BudgetStatusDTO> getUserBudgetStatus(String userId){
        List<Budget> budgets = budgetRepository.findByUserIdAndIsActiveTrue(userId);

        // Define the time window (Current Month)
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        return budgets.stream().map(budget -> {
            BigDecimal spent = budgetRepository.calculateSpentAmount(
                    userId,
                    budget.getCategory().getId(),
                    budget.getCurrency().getId(),
                    startOfMonth,
                    now
            );

            return calculateStatus(budget, spent);
        }).toList();
    }

    private BudgetStatusDTO calculateStatus(Budget budget, BigDecimal spent) {
        BigDecimal remaining = budget.getLimitAmount().subtract(spent);
        double progress = spent.divide(budget.getLimitAmount(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();

        String status = "ON_TRACK";
        if (progress >= 100) status = "EXCEEDED";
        else if (progress >= 85) status = "WARNING";

        return new BudgetStatusDTO(
                budget.getId(),
                budget.getCategory().getName(),
                budget.getLimitAmount(),
                spent,
                remaining,
                progress,
                status
        );
    }

    public void createBudget(BudgetRequestDTO dto) {
        Budget budget = new Budget();
        budget.setId(UUID.randomUUID().toString());
        budget.setUserId(dto.userId());
        budget.setLimitAmount(dto.limitAmount());

        Category category = categoryRepository.getReferenceById(dto.categoryId());
        Currency currency = currencyRepository.getReferenceById(dto.currencyId());

        budget.setCategory(category);
        budget.setCurrency(currency);

        budgetRepository.save(budget);
    }

    @Transactional
    public void updateBudget(String id, BudgetUpdateRequestDTO dto) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found with id: " + id));

        if (dto.limitAmount() != null) {
            budget.setLimitAmount(dto.limitAmount());
        }
        if (dto.isActive() != null) {
            budget.setIsActive(dto.isActive());
        }

        budgetRepository.save(budget);
    }

    @Transactional
    public void archiveBudget(String id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found with id: " + id));

        budget.setIsActive(false);
        budgetRepository.save(budget);
    }
}
