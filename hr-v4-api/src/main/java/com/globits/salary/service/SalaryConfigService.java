package com.globits.salary.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.SalaryConfig;
import com.globits.salary.dto.SalaryConfigDto;

public interface SalaryConfigService extends GenericService<SalaryConfig, UUID> {
    SalaryConfigDto saveSalaryConfig(SalaryConfigDto dto);

    Boolean deleteSalaryConfig(UUID id);

    Boolean deleteSalaryConfig(List<UUID> ids);

    SalaryConfigDto getSalaryConfig(UUID id);

    Page<SalaryConfigDto> searchByPage(SearchDto dto);

    Boolean checkCode(UUID id, String code);
}
