package com.globits.hr.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import com.globits.core.service.GenericService;
import com.globits.hr.domain.CivilServantCategory;
import com.globits.hr.dto.CivilServantCategoryDto;
import com.globits.hr.dto.search.SearchDto;

public interface CivilServantCategoryService extends GenericService<CivilServantCategory, UUID> {
	CivilServantCategoryDto getCivilServantCategory(UUID id);

	Boolean removeCivilServantCategory(UUID long1);

	Page<CivilServantCategoryDto> searchByPage(SearchDto dto);
	
	Boolean checkCode (UUID id, String code);
	
	Boolean checkName (UUID id, String name);
	
	CivilServantCategoryDto saveOrUpdate(UUID id,CivilServantCategoryDto dto);
	
}
