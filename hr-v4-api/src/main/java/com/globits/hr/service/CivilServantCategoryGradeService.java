package com.globits.hr.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.CivilServantCategoryGrade;
import com.globits.hr.dto.CivilServantCategoryGradeDto;
import com.globits.hr.dto.search.SearchDto;

public interface CivilServantCategoryGradeService extends GenericService<CivilServantCategoryGrade, UUID> {
    CivilServantCategoryGradeDto saveOrUpdate(UUID id, CivilServantCategoryGradeDto dto);

    CivilServantCategoryGradeDto getCivilServantCategoryGrade(UUID id);

    Boolean removeCivilServantCategoryGrade(UUID long1);

    Page<CivilServantCategoryGradeDto> searchByPage(SearchDto dto);

    Boolean checkCode(UUID id, String code);
}
