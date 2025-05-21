package com.globits.hr.dto;

import com.globits.hr.HrConstants;

import java.util.List;
import java.util.UUID;

public class SendMailCandidateDto {
    private List<UUID> candidateIds;
    private HrConstants.CandidateStatus status;
    private UUID templateId;
    private String templateCode;
    private UUID candidateId;
    private List<CandidateDto> candidate;

    public SendMailCandidateDto() {
    }

    public SendMailCandidateDto(List<UUID> candidateIds, HrConstants.CandidateStatus status, UUID templateId) {
        this.candidateIds = candidateIds;
        this.status = status;
        this.templateId = templateId;
    }

    public List<UUID> getCandidateIds() {
        return candidateIds;
    }

    public void setCandidateIds(List<UUID> candidateIds) {
        this.candidateIds = candidateIds;
    }

    public HrConstants.CandidateStatus getStatus() {
        return status;
    }

    public void setStatus(HrConstants.CandidateStatus status) {
        this.status = status;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public void setTemplateId(UUID templateId) {
        this.templateId = templateId;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public UUID getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(UUID candidateId) {
        this.candidateId = candidateId;
    }

    public List<CandidateDto> getCandidate() {
        return candidate;
    }

    public void setCandidate(List<CandidateDto> candidate) {
        this.candidate = candidate;
    }
}
