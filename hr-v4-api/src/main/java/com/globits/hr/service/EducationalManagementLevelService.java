package com.globits.hr.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.EducationalManagementLevel;
import com.globits.hr.dto.EducationalManagementLevelDto;
import com.globits.hr.dto.search.SearchDto;

public interface EducationalManagementLevelService extends GenericService<EducationalManagementLevel, UUID> {
	EducationalManagementLevelDto saveEducationalManagementLevel(EducationalManagementLevelDto dto);

    Boolean deleteEducationalManagementLevel(UUID id);

    EducationalManagementLevelDto getEducationalManagementLevel(UUID id);

    EducationalManagementLevelDto updateEducationalManagementLevel(EducationalManagementLevelDto dto, UUID id);

    Page<EducationalManagementLevelDto> searchByPage(SearchDto dto);

    Boolean checkCode(UUID id, String code);

}
