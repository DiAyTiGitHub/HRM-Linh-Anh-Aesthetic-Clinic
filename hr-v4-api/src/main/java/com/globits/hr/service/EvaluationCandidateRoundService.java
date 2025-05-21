package com.globits.hr.service;

import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.EvaluationCandidateRoundDto;
import com.globits.hr.dto.search.EvaluationCandidateRoundSearchDto;
import com.globits.hr.dto.search.SearchDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface EvaluationCandidateRoundService {
    ApiResponse<Page<EvaluationCandidateRoundDto>> searchByPage(EvaluationCandidateRoundSearchDto dto);

    ApiResponse<EvaluationCandidateRoundDto> saveOrUpdate(EvaluationCandidateRoundDto dto);

    ApiResponse<EvaluationCandidateRoundDto> getOne(UUID id);

    ApiResponse<Boolean> deleteOne(UUID id);

    ApiResponse<Boolean> checkCode(UUID id, String code);

    ApiResponse<EvaluationCandidateRoundDto> getByCandidateRoundId(UUID candidateRoundId);

    ApiResponse<EvaluationCandidateRoundDto> saveAndUpdate(EvaluationCandidateRoundDto dto);

}
