package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.AllowancePolicy;
import com.globits.hr.dto.AllowancePolicyDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface AllowancePolicyService extends GenericService<AllowancePolicy, UUID> {
	AllowancePolicyDto saveOrUpdate(AllowancePolicyDto dto , UUID id);
    void deleteAllowancePolicy(UUID id);
    AllowancePolicyDto getAllowancePolicyById(UUID id);
    Page<AllowancePolicyDto> searchByPage(SearchDto dto);
}
