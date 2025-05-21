package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.salary.domain.SalaryItem;
import com.globits.salary.domain.SalaryItemThreshold;
import com.globits.salary.domain.SalaryResult;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.SalaryResultDto;
import com.globits.salary.dto.search.SearchSalaryItemDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface SalaryItemThresholdService extends GenericService<SalaryItemThreshold, UUID> {
    void handleSetInSalaryItem(SalaryItem entity, SalaryItemDto dto);
}
