package com.globits.hr.service;

import com.globits.core.domain.Profession;
import com.globits.core.dto.ProfessionDto;
import com.globits.core.service.GenericService;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface HrProfessionService extends GenericService<Profession, UUID> {
	Page<ProfessionDto> searchByPage(SearchDto dto);

	ProfessionDto saveOne(ProfessionDto dto, UUID id);

	void deleteOne(UUID id);

	ProfessionDto getById(UUID id);

	Boolean checkCode(UUID id,String code);
}

