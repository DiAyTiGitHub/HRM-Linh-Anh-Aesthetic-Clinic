package com.globits.hr.dto.search;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AbsenceRequestSearchDto extends SearchDto {
    //staffId trong SearchDto
    private String absenceReason; // Lý do yêu cầu
    private Integer approvalStatus; // Trạng thái phê duyệt. Chi tiết: HrConstants.AbsenceRequestApprovalStatus
    private Date requestDate; // ngày tạo yêu cầu nghỉ
    private List<UUID> chosenIds;
    
    public String getAbsenceReason() {
        return absenceReason;
    }

    public void setAbsenceReason(String absenceReason) {
        this.absenceReason = absenceReason;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

	public List<UUID> getChosenIds() {
		return chosenIds;
	}

	public void setChosenIds(List<UUID> chosenIds) {
		this.chosenIds = chosenIds;
	}
    
    
}
