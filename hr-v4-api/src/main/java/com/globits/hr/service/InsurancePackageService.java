package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.Bank;
import com.globits.hr.domain.InsurancePackage;
import com.globits.hr.dto.BankDto;
import com.globits.hr.dto.InsurancePackageDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface InsurancePackageService extends GenericService<InsurancePackage, UUID> {
    InsurancePackageDto getById(UUID id);

    Boolean isValidCode(InsurancePackageDto dto);

    InsurancePackageDto saveOrUpdate(InsurancePackageDto dto);

    Boolean deleteById(UUID id);

    Integer deleteMultiple(List<UUID> ids);

    Page<InsurancePackageDto> searchByPage(SearchDto dto);
}
