package com.globits.hr.dto;

import java.util.Date;

import com.globits.hr.HrConstants;

public class RecruitmentRoundResultDto {
	
	private String roundName;
	private Integer roundOrder;
	private Date takePlaceDate;
	private String note;
	private String judgePersonDisplayName;
	private HrConstants.ResultStatus resultStatus;
	
	public String getRoundName() {
		return roundName;
	}
	public void setRoundName(String roundName) {
		this.roundName = roundName;
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
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getJudgePersonDisplayName() {
		return judgePersonDisplayName;
	}
	public void setJudgePersonDisplayName(String judgePersonDisplayName) {
		this.judgePersonDisplayName = judgePersonDisplayName;
	}
	public HrConstants.ResultStatus getResultStatus() {
		return resultStatus;
	}
	public void setResultStatus(HrConstants.ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}
	
	
}
