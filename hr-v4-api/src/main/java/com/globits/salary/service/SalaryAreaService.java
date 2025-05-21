package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.SalaryArea;
import com.globits.salary.dto.SalaryAreaDto;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.UUID;

public interface SalaryAreaService extends GenericService<SalaryArea, UUID> {
     SalaryAreaDto saveOrUpdateSalaryArea(SalaryAreaDto salaryAreaDto);
     Boolean deleteSalaryAres(UUID id);
     Boolean deleteMultipleSalaryArea(List<UUID> ids);
     SalaryAreaDto getSalaryAreaById(UUID id);
     Boolean checkCode(UUID id, String code);
     Page<SalaryAreaDto> searchByPage(SearchDto dto);

}
