package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.HrEducationType;
import com.globits.hr.dto.HrEducationTypeDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface HrEducationTypeService extends GenericService<HrEducationType, UUID> {
	HrEducationTypeDto saveOrUpdate(HrEducationTypeDto dto, UUID id);

	void remove(UUID id);

	HrEducationTypeDto getHrEducationType(UUID id);

	Page<HrEducationTypeDto> searchByPage(SearchDto dto);

	Boolean checkCode(UUID id, String code);

	HrEducationTypeDto findByCode(String code);
}
