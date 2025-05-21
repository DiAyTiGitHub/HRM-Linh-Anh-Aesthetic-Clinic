package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.AllowanceType;
import com.globits.hr.dto.AllowanceTypeDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface AllowanceTypeService extends GenericService<AllowanceType , UUID> {
    AllowanceTypeDto saveOrUpdate(AllowanceTypeDto dto , UUID id);
    void remove(UUID id);
    AllowanceTypeDto getAllowanceType(UUID id);
    Page<AllowanceTypeDto> searchByPage(SearchDto dto);
    Boolean checkCode(UUID id ,String code);
}
