package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.Allowance;
import com.globits.hr.dto.AllowanceDto;
import com.globits.hr.dto.SearchAllowanceDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface AllowanceService extends GenericService<Allowance , UUID> {
	AllowanceDto saveOrUpdate(AllowanceDto dto , UUID id);
    void deleteAllowance(UUID id);
    Boolean deleteMultiple(List<UUID> ids);
    AllowanceDto getAllowance(UUID id);
    Page<AllowanceDto> searchByPage(SearchAllowanceDto dto);
	Boolean isValidCode(AllowanceDto dto);
}
