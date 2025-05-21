package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.Candidate;
import com.globits.hr.domain.CandidateAttachment;
import com.globits.hr.dto.CandidateDto;

import java.util.UUID;

public interface CandidateAttachmentService extends GenericService<CandidateAttachment, UUID> {
    void handleSetAttachmentList(CandidateDto dto, Candidate entity);
}
