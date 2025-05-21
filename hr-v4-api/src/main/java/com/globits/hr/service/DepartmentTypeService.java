package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.DepartmentType;
import com.globits.hr.dto.DepartmentTypeDto;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface DepartmentTypeService extends GenericService<DepartmentType, UUID> {

    public DepartmentTypeDto saveOrUpdate(DepartmentTypeDto dto);
    public Page<DepartmentTypeDto>pageBySearch(SearchDto dto);
    public Boolean deleteDepartmentType(UUID id);
    public DepartmentTypeDto getById(UUID id);
    public Boolean deleteMultiple( List<UUID> listIds);

    DepartmentTypeDto findByShortName(String shortName);
	Integer saveListDepartmentType(List<DepartmentTypeDto> listData);

    Boolean isValidCode(DepartmentTypeDto dto);

    String autoGenerateCode(String configKey);
}
