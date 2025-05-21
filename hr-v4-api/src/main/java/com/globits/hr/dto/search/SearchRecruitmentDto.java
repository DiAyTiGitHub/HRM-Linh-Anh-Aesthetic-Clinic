package com.globits.hr.dto.search;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.globits.hr.HrConstants;
import com.globits.hr.domain.HRDepartment;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.HrOrganizationDto;
import com.globits.hr.dto.PositionDto;

public class SearchRecruitmentDto extends SearchDto {
    private Integer status;
    private List<UUID> chosenIds;
    private UUID recruitmentRequestId;
    private UUID positionTitleId;
    private UUID recruitmentPlanId;
    private UUID organizationId;
    private UUID personInChargeId;
    private Boolean isPersonInCharge = false;
    private List<HrConstants.RecruitmentRequestStatus> recruitmentRequestStatus;
    private Date recruitingStartDate;
    private Date fromEndDate;
    private Date toEndDate;

    public Boolean getIsPersonInCharge;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<UUID> getChosenIds() {
        return chosenIds;
    }

    public void setChosenIds(List<UUID> chosenIds) {
        this.chosenIds = chosenIds;
    }

    public UUID getRecruitmentRequestId() {
        return recruitmentRequestId;
    }

    public void setRecruitmentRequestId(UUID recruitmentRequestId) {
        this.recruitmentRequestId = recruitmentRequestId;
    }

    public UUID getRecruitmentPlanId() {
        return recruitmentPlanId;
    }

    public void setRecruitmentPlanId(UUID recruitmentPlanId) {
        this.recruitmentPlanId = recruitmentPlanId;
    }

    @Override
    public UUID getOrganizationId() {
        return organizationId;
    }

    @Override
    public void setOrganizationId(UUID organizationId) {
        this.organizationId = organizationId;
    }

    public UUID getPositionTitleId() {
        return positionTitleId;
    }

    public void setPositionTitleId(UUID positionTitleId) {
        this.positionTitleId = positionTitleId;
    }

    public UUID getPersonInChargeId() {
        return personInChargeId;
    }

    public void setPersonInChargeId(UUID personInChargeId) {
        this.personInChargeId = personInChargeId;
    }

    public Boolean getPersonInCharge() {
        return isPersonInCharge;
    }

    public void setPersonInCharge(Boolean personInCharge) {
        isPersonInCharge = personInCharge;
    }

    public List<HrConstants.RecruitmentRequestStatus> getRecruitmentRequestStatus() {
        return recruitmentRequestStatus;
    }

    public void setRecruitmentRequestStatus(List<HrConstants.RecruitmentRequestStatus> recruitmentRequestStatus) {
        this.recruitmentRequestStatus = recruitmentRequestStatus;
    }

    public Date getRecruitingStartDate() {
        return recruitingStartDate;
    }

    public void setRecruitingStartDate(Date recruitingStartDate) {
        this.recruitingStartDate = recruitingStartDate;
    }

    public Date getFromEndDate() {
        return fromEndDate;
    }

    public void setFromEndDate(Date fromEndDate) {
        this.fromEndDate = fromEndDate;
    }

    public Date getToEndDate() {
        return toEndDate;
    }

    public void setToEndDate(Date toEndDate) {
        this.toEndDate = toEndDate;
    }
}
