package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.RecruitmentExamType;
import com.globits.hr.dto.RecruitmentExamTypeDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface RecruitmentExamTypeService extends GenericService<RecruitmentExamType, UUID> {

    Page<RecruitmentExamTypeDto> searchByPage(SearchDto dto);

    RecruitmentExamTypeDto saveOne(RecruitmentExamTypeDto dto, UUID id);

    Boolean deleteRecruitmentExamType(UUID id);

    Boolean checkCode(UUID id, String code);

    RecruitmentExamTypeDto getItemById(UUID id);

    Boolean deleteMultiple(List<UUID> recruitmentExamTypeIds);
    
}
