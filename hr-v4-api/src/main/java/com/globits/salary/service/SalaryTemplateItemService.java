package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.salary.domain.SalaryTemplateItem;
import com.globits.salary.domain.SalaryTemplateItemGroup;
import com.globits.salary.dto.RequestSalaryValueDto;
import com.globits.salary.dto.SalaryTemplateDto;
import com.globits.salary.dto.SalaryTemplateItemDto;
import com.globits.salary.dto.search.ChooseSalaryTemplateItemDto;

import java.util.List;
import java.util.UUID;

public interface SalaryTemplateItemService extends GenericService<SalaryTemplateItem, UUID> {
    void handleSetSalaryTemplateItems(SalaryTemplate entity, SalaryTemplateDto dto);

    List<SalaryTemplateItemDto> handleChooseTemplateItems(ChooseSalaryTemplateItemDto dto);


    List<SalaryTemplateItemDto> getListSalaryTemplateItem(RequestSalaryValueDto dto);

    SalaryTemplateItemDto saveSalaryTemplateItemWithSalaryTemplateItemConfig(SalaryTemplateItemDto dto);

}
