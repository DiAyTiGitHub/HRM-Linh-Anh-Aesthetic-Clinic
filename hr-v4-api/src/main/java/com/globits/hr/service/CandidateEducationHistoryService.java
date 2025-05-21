package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.Candidate;
import com.globits.hr.domain.CandidateEducationHistory;
import com.globits.hr.dto.CandidateDto;

import java.util.UUID;

public interface CandidateEducationHistoryService extends GenericService<CandidateEducationHistory, UUID> {
    void handleSetEducationHistoryList(CandidateDto dto, Candidate entity);
}
