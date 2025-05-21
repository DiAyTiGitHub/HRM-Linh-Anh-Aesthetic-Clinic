package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.hr.dto.StaffLabourAgreementDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.SalaryUnit;
import com.globits.salary.dto.SalaryConfigItemDto;
import com.globits.salary.dto.SalaryIncrementTypeDto;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.SalaryUnitDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface SalaryUnitService extends GenericService<SalaryUnit, UUID> {

    SalaryUnitDto saveOrUpdate(SalaryUnitDto dto);
    Boolean deleteSalaryUnit(UUID id);

    Boolean deleteMultipleSalaryUnit(List<UUID>listIds);

    SalaryUnitDto getById(UUID id);

    Boolean checkCode(UUID id, String code);

    Page<SalaryUnitDto> searchByPage(SearchDto dto);

}
