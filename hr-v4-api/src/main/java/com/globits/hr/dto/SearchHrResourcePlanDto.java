package com.globits.hr.dto;

import com.globits.hr.domain.Staff;
import com.globits.hr.dto.search.SearchDto;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.List;
import java.util.UUID;

public class SearchHrResourcePlanDto extends SearchDto {
    private UUID positionTitleId;
    private Boolean getAggregate = false;
    private Integer viceGeneralDirectorStatus; // Trạng thái phó tổng giám đốc duyệt. Chi tiết: HrConstants.HrResourcePlanApprovalStatus
    private Integer generalDirectorStatus; // Trạng thái tổng giám đốc duyệt. Chi tiết: HrConstants.HrResourcePlanApprovalStatus
    private Integer planApprovalStatus;
    private List<UUID> chosenRecordIds;

    public SearchHrResourcePlanDto() {

    }


    public List<UUID> getChosenRecordIds() {
        return chosenRecordIds;
    }

    public void setChosenRecordIds(List<UUID> chosenRecordIds) {
        this.chosenRecordIds = chosenRecordIds;
    }

    public Boolean isGetAggregate() {
        return getAggregate;
    }

    public void setGetAggregate(Boolean getAggregate) {
        this.getAggregate = getAggregate;
    }

    public UUID getPositionTitleId() {
        return positionTitleId;
    }

    public void setPositionTitleId(UUID positionTitleId) {
        this.positionTitleId = positionTitleId;
    }

    public Boolean getGetAggregate() {
        return getAggregate;
    }

    public Integer getViceGeneralDirectorStatus() {
        return viceGeneralDirectorStatus;
    }

    public void setViceGeneralDirectorStatus(Integer viceGeneralDirectorStatus) {
        this.viceGeneralDirectorStatus = viceGeneralDirectorStatus;
    }

    public Integer getGeneralDirectorStatus() {
        return generalDirectorStatus;
    }

    public void setGeneralDirectorStatus(Integer generalDirectorStatus) {
        this.generalDirectorStatus = generalDirectorStatus;
    }

    public Integer getPlanApprovalStatus() {
        return planApprovalStatus;
    }

    public void setPlanApprovalStatus(Integer planApprovalStatus) {
        this.planApprovalStatus = planApprovalStatus;
    }
}
