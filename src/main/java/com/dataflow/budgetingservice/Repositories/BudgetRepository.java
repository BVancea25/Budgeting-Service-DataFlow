package com.dataflow.budgetingservice.Repositories;

import com.dataflow.budgetingservice.Models.Budget;
import com.dataflow.budgetingservice.Repositories.Custom.BudgetRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, String>, BudgetRepositoryCustom {
    List<Budget> findByUserIdAndIsActiveTrue(String userId);
}
