package com.globits.hr.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.CivilServantGrade;
import com.globits.hr.dto.CivilServantGradeDto;
import com.globits.hr.dto.search.SearchDto;

public interface CivilServantGradeService extends GenericService<CivilServantGrade, UUID> {
	
	CivilServantGradeDto saveOrUpdate(UUID id,CivilServantGradeDto dto);

	CivilServantGradeDto getCivilServantGrade(UUID id);

	Boolean removeCivilServantGrade(UUID long1);
	
	Page<CivilServantGradeDto> searchByPage(SearchDto dto);
	
	Boolean checkCode (UUID id,String code);
}
