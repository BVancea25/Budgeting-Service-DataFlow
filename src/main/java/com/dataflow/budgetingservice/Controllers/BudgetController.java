package com.dataflow.budgetingservice.Controllers;

import com.dataflow.budgetingservice.DTO.BudgetRequestDTO;
import com.dataflow.budgetingservice.DTO.BudgetStatusDTO;
import com.dataflow.budgetingservice.DTO.BudgetUpdateRequestDTO;
import com.dataflow.budgetingservice.Services.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<Void> createBudget(@RequestBody BudgetRequestDTO request) {
        budgetService.createBudget(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("/status/{userId}")
    public ResponseEntity<List<BudgetStatusDTO>> getBudgetStatus(@PathVariable String userId) {
        return ResponseEntity.ok(budgetService.getUserBudgetStatus(userId));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBudget(
            @PathVariable String id,
            @RequestBody BudgetUpdateRequestDTO request) {
        budgetService.updateBudget(id, request);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable String id) {
        budgetService.archiveBudget(id);
        return ResponseEntity.noContent().build();
    }
}
