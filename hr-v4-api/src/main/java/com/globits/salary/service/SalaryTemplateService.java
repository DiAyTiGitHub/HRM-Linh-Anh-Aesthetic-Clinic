package com.globits.salary.service;

import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.search.SearchSalaryTemplateDto;
import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.salary.dto.SalaryTemplateDto;

public interface SalaryTemplateService extends GenericService<SalaryTemplate, UUID> {
	SalaryTemplateDto saveSalaryTemplate(SalaryTemplateDto dto);

	Boolean deleteSalaryTemplate(UUID id);

	SalaryTemplateDto getSalaryTemplate(UUID id);

	Page<SalaryTemplateDto> searchByPage(SearchSalaryTemplateDto dto);

	Boolean deleteMultiple(List<UUID> ids);

	Boolean isValidCode(SalaryTemplateDto dto);

	SalaryTemplateDto findByCode(String code);
	
	SalaryTemplateDto clonSalaryTemplate(SalaryTemplateDto dto);
}
