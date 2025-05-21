package com.globits.budget.repository;

import com.globits.budget.domain.BudgetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory, UUID> {
    @Query("select l FROM BudgetCategory l where l.code = ?1")
    List<BudgetCategory> findByCode(String code);
}
