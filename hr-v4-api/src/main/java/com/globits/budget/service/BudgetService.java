package com.globits.budget.service;

import com.globits.budget.dto.budget.*;
import com.globits.core.service.GenericService;
import com.globits.budget.domain.Budget;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface BudgetService extends GenericService<Budget, UUID> {
    Page<BudgetDto> pagingBudget(BudgetSearchDto dto);

    BudgetDto saveOrUpdate(BudgetDto dto);

    BudgetDto getById(UUID id);

    Boolean deleteById(UUID id);

    Integer deleteMultiple(List<UUID> ids);

    List<BudgetSummaryDto> getBudgetSummary(BudgetSummaryDto dto);

    List<BudgetSummaryYearDto> getBudgetSummaryYear(BudgetSummaryDto dto);

    BudgetSummaryBalanceDto getBudgetSummaryBalance(BudgetSummaryDto dto);

    Boolean checkCode(BudgetDto dto);
}
