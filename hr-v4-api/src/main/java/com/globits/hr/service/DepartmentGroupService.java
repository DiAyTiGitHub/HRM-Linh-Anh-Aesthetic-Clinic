package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.DepartmentGroup;
import com.globits.hr.dto.DepartmentGroupDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface DepartmentGroupService extends GenericService<DepartmentGroup, UUID> {
    public DepartmentGroupDto saveOrUpdate(DepartmentGroupDto dto);
    public Page<DepartmentGroupDto> pageBySearch(SearchDto dto);
    public Boolean deleteDepartmentGroup(UUID id);
    public DepartmentGroupDto getById(UUID id);
    public Boolean deleteMultiple( List<UUID> listIds);

    DepartmentGroupDto findByShortName(String shortName);

}
