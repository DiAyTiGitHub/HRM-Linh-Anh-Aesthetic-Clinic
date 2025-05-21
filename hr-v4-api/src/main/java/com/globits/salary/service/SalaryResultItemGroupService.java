package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.salary.domain.SalaryResult;
import com.globits.salary.domain.SalaryResultItemGroup;
import com.globits.salary.domain.SalaryTemplateItemGroup;
import com.globits.salary.dto.SalaryResultDto;

import java.util.UUID;

public interface SalaryResultItemGroupService extends GenericService<SalaryResultItemGroup, UUID> {
    void copyFromSalaryTemplateItemGroup(SalaryResult result);

    void handleSetSalaryResultItemGroupsFromConfig(SalaryResult entity, SalaryResultDto dto);

}
