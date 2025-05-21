package com.globits.hr.service;


import com.globits.hr.dto.RecruitmentDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchRecruitmentDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface RecruitmentService {
    public Page<RecruitmentDto> pagingRecruitment(SearchRecruitmentDto dto);

    RecruitmentDto getById(UUID id);

    RecruitmentDto saveRecruitment(RecruitmentDto dto);

    Boolean deleteRecruitment(UUID id);

    Boolean deleteMultipleRecruitment(List<UUID> ids);

    Boolean isValidCode(RecruitmentDto dto);

}