package com.globits.hr.service;

/*
 * author Giang-Tuan Anh
 */
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.EducationDegree;
import com.globits.hr.dto.EducationDegreeDto;
import com.globits.hr.dto.search.SearchDto;

public interface EducationDegreeService extends GenericService<EducationDegree, UUID> {
	EducationDegreeDto saveOrUpdate(UUID id, EducationDegreeDto dto);

	Boolean deleteEducationDegree(UUID id);

	EducationDegreeDto getEducationDegree(UUID id);

	Page<EducationDegreeDto> searchByPage(SearchDto dto);

	Boolean checkCode(UUID id, String code);

	Boolean checkName(UUID id, String name);

	EducationDegreeDto findByCode(String code);
}
