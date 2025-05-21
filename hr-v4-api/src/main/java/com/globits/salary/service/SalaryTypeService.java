package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.SalaryType;
import com.globits.salary.dto.SalaryTypeDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface SalaryTypeService extends GenericService<SalaryType, UUID> {

    SalaryTypeDto saveSalaryType(SalaryTypeDto dto);

    SalaryTypeDto getById(UUID id);

    Boolean deleteSalaryType(UUID id);

    Boolean deleteMultipleSalaryTypes(List<UUID> ids);

    Page<SalaryTypeDto> pagingSalaryTypes(SearchDto dto);

}
