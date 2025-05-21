package com.globits.hr.service;

import com.globits.core.domain.Religion;
import com.globits.core.dto.ReligionDto;
import com.globits.core.service.GenericService;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.select.ReligionSelectDto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface HrReligionService extends GenericService<Religion, UUID> {
	Page<ReligionDto> searchByPage(SearchDto dto);

	ReligionDto saveOne(ReligionDto dto, UUID id);

	void remove(UUID id);

	Boolean checkCode(UUID id, String code);

	ReligionDto getById(UUID id);

	List<ReligionSelectDto> getAllReligions();

	ReligionDto findByCode(String code);
}
