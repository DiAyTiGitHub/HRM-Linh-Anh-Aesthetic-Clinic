package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Candidate;
import com.globits.hr.domain.CandidateRecruitmentRound;
import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.CandidateDto;
import com.globits.hr.dto.CandidateRecruitmentRoundDto;
import com.globits.hr.dto.RecruitmentDto;
import com.globits.hr.dto.search.SearchCandidateDto;
import com.globits.hr.dto.search.SearchCandidateRecruitmentRoundDto;
import com.globits.hr.dto.search.SearchRecruitmentDto;
import com.globits.salary.dto.SalaryResultDto;
import com.globits.salary.dto.search.SearchSalaryResultDto;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface CandidateRecruitmentRoundService extends GenericService<CandidateRecruitmentRound, UUID> {
    void handleSetCandidateRecruitmentRoundList(CandidateDto dto, Candidate entity);

    Boolean isValid(CandidateRecruitmentRoundDto dto);

    CandidateRecruitmentRoundDto saveCandidateRecruitmentRound(CandidateRecruitmentRoundDto dto);
    CandidateRecruitmentRound convertDtoToEntity(CandidateRecruitmentRoundDto dto);

    Page<CandidateRecruitmentRoundDto> pagingCandidateRecruitmentRound(SearchCandidateRecruitmentRoundDto dto);

    CandidateRecruitmentRoundDto getById(UUID id);

    Boolean remove(UUID id);

    Boolean removeMultiple(List<UUID> ids);

    // update candidate's result in recruitment round
    Boolean updateCandidateRecruitmentRoundResult(SearchCandidateRecruitmentRoundDto dto) throws Exception;

    // Phân bổ/Sắp xếp ứng viên cho các vòng tuyển dụng tiếp theo
    Boolean moveToNextRecruitmentRound(SearchCandidateRecruitmentRoundDto dto) throws Exception;

    // Phân bổ/Sắp xếp ứng viên cho vòng tuyển dụng đầu tiên
    Boolean distributeCandidatesForFirstRecruitmentRound(SearchCandidateRecruitmentRoundDto dto) throws Exception;

    Boolean checkExistCandidateRecruitmentRound(UUID candidateId, UUID recruitmentRoundId);

    ApiResponse<List<CandidateRecruitmentRoundDto>> getListCandiDateByPlainAndRound(UUID planId, UUID roundId);

    ApiResponse<Boolean> confirmInterview(UUID candidateId, HrConstants.CandidateRecruitmentRoundStatus desition);

    ApiResponse<Boolean> doActionAssignment(UUID crrId, HrConstants.CandidateStatus status);
//    ApiResponse<List<CandidateRecruitmentRoundDto>> approveCandidateRound(SearchCandidateRecruitmentRoundDto dto);

    ApiResponse<List<CandidateRecruitmentRoundDto>> getByIdRecruitmentRound(UUID roundId);

    ApiResponse<Boolean> passToNextRound(UUID crrId);

    ApiResponse<Boolean> rejectCandidateRound(UUID crrId);

    List<CandidateRecruitmentRoundDto> getCandidateRoundByCandidateId(UUID candidateId);

    ApiResponse<HashMap<UUID, HashMap<String, Object>>> passListToNextRound(List<UUID> crrIds);

    ApiResponse<Boolean> rejectListCandidateRound(List<UUID> crrIds);
}
