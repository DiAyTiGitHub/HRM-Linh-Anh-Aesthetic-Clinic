package com.globits.hr.service;

import com.globits.core.domain.Ethnics;
import com.globits.core.dto.EthnicsDto;
import com.globits.core.service.GenericService;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface HrEthinicsService extends GenericService<Ethnics, UUID> {
	Page<EthnicsDto> searchByPage(SearchDto dto);

	EthnicsDto saveOne(EthnicsDto dto, UUID id);

	void remove(UUID id);

	Boolean checkCode(UUID id, String code);

	EthnicsDto getItemById(UUID id);

	EthnicsDto findByCode(String code);
}
