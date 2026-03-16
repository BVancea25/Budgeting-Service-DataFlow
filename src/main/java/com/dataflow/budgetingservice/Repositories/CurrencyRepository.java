package com.dataflow.budgetingservice.Repositories;

import com.dataflow.budgetingservice.Models.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String> {
    Currency findByCodeContainingIgnoreCase(String code);
}
