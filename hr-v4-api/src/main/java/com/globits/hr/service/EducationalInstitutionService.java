package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.EducationalInstitution;
import com.globits.hr.dto.EducationalInstitutionDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface EducationalInstitutionService extends GenericService<EducationalInstitution, UUID> {
	EducationalInstitutionDto saveOrUpdate(EducationalInstitutionDto dto, UUID id);

	void remove(UUID id);

	EducationalInstitutionDto getEducationalInstitution(UUID id);

	Page<EducationalInstitutionDto> searchByPage(SearchDto dto);

	Boolean checkCode(UUID id, String code);

	EducationalInstitutionDto findByCode(String code);
}
