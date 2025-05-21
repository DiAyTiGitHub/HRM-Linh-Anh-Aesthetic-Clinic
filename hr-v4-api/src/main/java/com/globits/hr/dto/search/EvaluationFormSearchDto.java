package com.globits.hr.dto.search;

import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.HrOrganizationDto;
import com.globits.hr.dto.PositionTitleDto;
import com.globits.hr.utils.Const;

import java.util.UUID;

public class EvaluationFormSearchDto extends SearchDto {
    private UUID directManagerId;
    private Const.EVALUATION status;
    private String contractType;
    private HRDepartmentDto department;
    private PositionTitleDto position;
    private HrOrganizationDto organization;
    private UUID staffDivision;

    public EvaluationFormSearchDto() {
        super();
    }


    public Const.EVALUATION getStatus() {
        return status;
    }

    public void setStatus(Const.EVALUATION status) {
        this.status = status;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public UUID getDirectManagerId() {
        return directManagerId;
    }

    public void setDirectManagerId(UUID directManagerId) {
        this.directManagerId = directManagerId;
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }

    public PositionTitleDto getPosition() {
        return position;
    }

    public void setPosition(PositionTitleDto position) {
        this.position = position;
    }

    public HrOrganizationDto getOrganization() {
        return organization;
    }

    public void setOrganization(HrOrganizationDto organization) {
        this.organization = organization;
    }

    public UUID getStaffDivision() {
        return staffDivision;
    }

    public void setStaffDivision(UUID staffDivision) {
        this.staffDivision = staffDivision;
    }
}
