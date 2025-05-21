package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.salary.domain.SalaryTemplateItemGroup;
import com.globits.salary.dto.SalaryTemplateDto;
import com.globits.salary.dto.SalaryTemplateItemGroupDto;

import java.util.List;
import java.util.UUID;

public interface SalaryTemplateItemGroupService extends GenericService<SalaryTemplateItemGroup, UUID> {
    void handleSetSalaryTemplateItemGroups(SalaryTemplate entity, SalaryTemplateDto dto);
}
