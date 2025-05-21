package com.globits.hr.service;


import com.globits.hr.dto.RecruitmentPlanDto;
import com.globits.hr.dto.search.SearchRecruitmentDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface RecruitmentPlanService {
    Page<RecruitmentPlanDto> pagingRecruitmentPlan(SearchRecruitmentDto dto);

    RecruitmentPlanDto getById(UUID id);

    RecruitmentPlanDto saveRecruitmentPlan(RecruitmentPlanDto dto);

    Boolean deleteRecruitmentPlan(UUID id);

    Boolean deleteMultipleRecruitmentPlan(List<UUID> ids);

    List<UUID> updatePlansStatus(SearchRecruitmentDto dto);

    Boolean isValidCode(RecruitmentPlanDto dto);

    Integer saveListRecruitmentPlan(List<RecruitmentPlanDto> list);

    String autoGenerateCode(String configKey);
}