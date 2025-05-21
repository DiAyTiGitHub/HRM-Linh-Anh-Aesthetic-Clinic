package com.globits.hr.service;


import com.globits.core.service.GenericService;
import com.globits.hr.domain.RecruitmentRound;
import com.globits.hr.dto.RecruitmentRoundDto;
import com.globits.hr.dto.search.SearchRecruitmentDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface RecruitmentRoundService extends GenericService<RecruitmentRound, UUID> {
    RecruitmentRound getNextRecruitmentRound(UUID currentRoundId);

    RecruitmentRound getFirstRecruitmentRoundOfRecruitment(UUID recruitmentId);

    void deleteByRecruitmentPlanId(UUID recruitmentPlanId);

    RecruitmentRoundDto saveOrUpdate(RecruitmentRoundDto dto);

    List<RecruitmentRoundDto> getListByRecruitmentPlanId(UUID recruitmentPlanId);

    Page<RecruitmentRoundDto> pagingRecruitmentRound(SearchRecruitmentDto dto);

    RecruitmentRound getNextRound(UUID planId, Integer roundOrder);

    Integer saveListRecruitmentRound(List<RecruitmentRoundDto> list);
}