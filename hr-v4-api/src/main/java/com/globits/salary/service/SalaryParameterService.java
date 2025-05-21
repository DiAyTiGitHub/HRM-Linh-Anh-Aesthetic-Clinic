package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.SalaryParameter;
import com.globits.salary.dto.SalaryParameterDto;
import org.springframework.data.domain.Page;


import java.util.List;
import java.util.UUID;

public interface SalaryParameterService extends GenericService<SalaryParameter, UUID> {
     SalaryParameterDto saveSalaryParameter(SalaryParameterDto dto);

     SalaryParameterDto getById(UUID id);

     Boolean deleteSalaryParameter(UUID id);

     Boolean deleteMultipleSalaryParameters(List<UUID> ids);

     Page<SalaryParameterDto>  pagingSalaryParameters(SearchDto dto);
}
