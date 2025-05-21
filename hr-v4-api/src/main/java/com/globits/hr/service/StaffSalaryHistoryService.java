package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.KPI;
import com.globits.hr.domain.StaffSalaryHistory;
import com.globits.hr.dto.KPIDto;
import com.globits.hr.dto.StaffSalaryHistoryDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StaffSalaryHistoryService extends GenericService<StaffSalaryHistory, UUID> {
    StaffSalaryHistoryDto getById(UUID id);

    StaffSalaryHistoryDto saveOrUpdate(StaffSalaryHistoryDto dto);

    Boolean deleteById(UUID id);

    Page<StaffSalaryHistoryDto> paging(SearchDto dto);

    List<StaffSalaryHistoryDto> getAllByStaff(UUID id);
}
