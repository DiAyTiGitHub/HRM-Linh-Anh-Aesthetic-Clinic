package com.globits.hr.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.globits.hr.HrConstants;

public class CandidateRecruitmentReportDto {
	private UUID candidateId;
	private String candidateCode;
	private String positionTitleName;
	private String departmentName;
	private Date proposalReceiptDate;
	private UUID planId;
	private String planName;

	// Vòng hiện tại
	private String roundName;
	private Integer roundOrder;
	private Date takePlaceDate;
	private String judgePersonDisplayName;
	private String note;
	private HrConstants.ResultStatus resultStatus;

	private List<RecruitmentRoundResultDto> results;

	public CandidateRecruitmentReportDto() {

	}

	public CandidateRecruitmentReportDto(UUID candidateId, String candidateCode, String positionTitleName,
			String departmentName, Date proposalReceiptDate, UUID planId, String planName, Integer roundOrder,
			Date takePlaceDate, String judgePersonDisplayName, String roundName, String note,
			HrConstants.ResultStatus resultStatus) {
		
		this.candidateId = candidateId;
		this.candidateCode = candidateCode;
		this.positionTitleName = positionTitleName;
		this.departmentName = departmentName;
		this.proposalReceiptDate = proposalReceiptDate;
		this.planId = planId;
		this.planName = planName;
		this.roundOrder = roundOrder;
		this.takePlaceDate = takePlaceDate;
		this.judgePersonDisplayName = judgePersonDisplayName;
		this.roundName = roundName;
		this.note = note;
		this.resultStatus = resultStatus;
	}

	public UUID getCandidateId() {
		return candidateId;
	}

	public void setCandidateId(UUID candidateId) {
		this.candidateId = candidateId;
	}

	public String getCandidateCode() {
		return candidateCode;
	}

	public void setCandidateCode(String candidateCode) {
		this.candidateCode = candidateCode;
	}

	public String getPositionTitleName() {
		return positionTitleName;
	}

	public void setPositionTitleName(String positionTitleName) {
		this.positionTitleName = positionTitleName;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public Date getProposalReceiptDate() {
		return proposalReceiptDate;
	}

	public void setProposalReceiptDate(Date proposalReceiptDate) {
		this.proposalReceiptDate = proposalReceiptDate;
	}

	public UUID getPlanId() {
		return planId;
	}

	public void setPlanId(UUID planId) {
		this.planId = planId;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public Integer getRoundOrder() {
		return roundOrder;
	}

	public void setRoundOrder(Integer roundOrder) {
		this.roundOrder = roundOrder;
	}

	public Date getTakePlaceDate() {
		return takePlaceDate;
	}

	public void setTakePlaceDate(Date takePlaceDate) {
		this.takePlaceDate = takePlaceDate;
	}

	public String getJudgePersonDisplayName() {
		return judgePersonDisplayName;
	}

	public void setJudgePersonDisplayName(String judgePersonDisplayName) {
		this.judgePersonDisplayName = judgePersonDisplayName;
	}

	public String getRoundName() {
		return roundName;
	}

	public void setRoundName(String roundName) {
		this.roundName = roundName;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public HrConstants.ResultStatus getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(HrConstants.ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}

	public List<RecruitmentRoundResultDto> getResults() {
		return results;
	}

	public void setResults(List<RecruitmentRoundResultDto> results) {
		this.results = results;
	}

}
