package com.globits.salary.service;

import com.globits.core.dto.SearchDto;
import com.globits.core.service.GenericService;
import com.globits.salary.domain.SalaryPeriod;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.SalaryPeriodDto;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface SalaryPeriodService extends GenericService<SalaryPeriod, UUID> {

    SalaryPeriodDto saveOrUpdate(SalaryPeriodDto dto);

    Page<SalaryPeriodDto> searchByPage(SearchDto searchDto);

    SalaryPeriodDto getById(UUID id);

    Boolean remove(UUID id);

    Boolean removeMultiple(List<UUID> ids);

    SalaryPeriodDto findByCode(String code);

    Boolean isValidCode(SalaryPeriodDto dto);

    List<SalaryPeriodDto> findSalaryPeriodsInRangeTime(Date fromDate, Date toDate);

    List<SalaryPeriodDto> getActivePeriodsByDate(Date requestDate);

    String autoGenerateCode(String configKey);
}