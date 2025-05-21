package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.Workplace;
import com.globits.hr.dto.WorkplaceDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface WorkplaceService extends GenericService<Workplace , UUID> {

    void deleteWorkplace(UUID id);
    Boolean deleteMultiple(List<UUID> ids);
    Page<WorkplaceDto> searchByPage(SearchDto dto);
	Boolean isValidCode(WorkplaceDto dto);
	WorkplaceDto saveOrUpdate(WorkplaceDto dto);
	WorkplaceDto getWorkplaceById(UUID id);
	
}
