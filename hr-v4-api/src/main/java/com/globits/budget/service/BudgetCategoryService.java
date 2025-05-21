package com.globits.budget.service;

import com.globits.budget.domain.BudgetCategory;
import com.globits.budget.dto.BudgetCategoryDto;
import com.globits.budget.dto.budget.BudgetSearchDto;
import com.globits.core.service.GenericService;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface BudgetCategoryService extends GenericService<BudgetCategory, UUID> {
    Page<BudgetCategoryDto> pagingBudgetCategory(BudgetSearchDto dto);

    BudgetCategoryDto saveOrUpdate(BudgetCategoryDto dto);

    BudgetCategoryDto getById(UUID id);

    Boolean deleteById(UUID id);

    Integer deleteMultiple(List<UUID> ids);

    Boolean checkCode(BudgetCategoryDto dto);
}
