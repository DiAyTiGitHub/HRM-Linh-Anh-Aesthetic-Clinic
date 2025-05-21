package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.salary.domain.SalaryResult;
import com.globits.salary.domain.SalaryResultItem;
import com.globits.salary.dto.search.ChooseSalaryResultItemDto;
import com.globits.salary.dto.SalaryResultDto;
import com.globits.salary.dto.SalaryResultItemDto;

import java.util.List;
import java.util.UUID;

public interface SalaryResultItemService extends GenericService<SalaryResultItem, UUID> {
    void copyFromSalaryTemplateItem(SalaryResult result);

    void handleSetSalaryResultItemsFromConfigV2(SalaryResult entity, SalaryResultDto dto);

    List<SalaryResultItemDto> handleChooseResultItems(ChooseSalaryResultItemDto dto);

    void autoGenerateSpecialFormulaForResultItem(SalaryResult entity);
}
