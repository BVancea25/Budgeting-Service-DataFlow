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
import com.dataflow.budgetingservice.Utils.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final CurrencyRepository currencyRepository;

    public List<BudgetStatusDTO> getUserBudgetStatus(){
        String userId = SecurityUtils.getCurrentUserUuid();
        List<Budget> budgets = budgetRepository.findByUserIdAndIsActiveTrue(userId);

        LocalDateTime now = LocalDateTime.now();

        return budgets.stream().map(budget -> {
            BigDecimal spent = budgetRepository.calculateSpentAmount(
                    userId,
                    budget.getCategory().getId(),
                    budget.getCurrency().getId(),
                    budget.getStartDate().atStartOfDay(),
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
                status,
                budget.getPeriod()
        );
    }

    public void createBudget(BudgetRequestDTO dto) {
        Budget budget = new Budget();
        budget.setId(UUID.randomUUID().toString());
        budget.setUserId(SecurityUtils.getCurrentUserUuid());
        budget.setLimitAmount(dto.limitAmount());
        budget.setStartDate(dto.startDate());


        Category category = categoryRepository.getReferenceById(dto.categoryId());
        Currency currency = currencyRepository.findByCodeContainingIgnoreCase(dto.currencyCode());

        budget.setCategory(category);
        budget.setCurrency(currency);

        budgetRepository.save(budget);
    }

    @Transactional
    public void updateBudget(String id, BudgetUpdateRequestDTO dto) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found with id: " + id));

        String userId = SecurityUtils.getCurrentUserUuid();

        if(!Objects.equals(budget.getUserId(), userId)){
            System.out.println("User with id " + userId + " doesn't own the budget with id " + budget.getId());
            return;
        }
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

        String userId = SecurityUtils.getCurrentUserUuid();

        if(!Objects.equals(budget.getUserId(), userId)){
            System.out.println("User with id " + userId + " doesn't own the budget with id " + budget.getId());
            return;
        }

        budget.setIsActive(false);
        budgetRepository.save(budget);
    }
}
