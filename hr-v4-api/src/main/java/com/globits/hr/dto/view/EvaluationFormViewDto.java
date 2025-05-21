package com.globits.hr.dto.view;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.EvaluationForm;
import com.globits.hr.utils.Const;

import java.util.Date;
import java.util.UUID;

public class EvaluationFormViewDto extends BaseObjectDto {
    private UUID staffId;
    private String staffName;
    private String staffCode;
    private String position;
    private String department;
    private String division;
    private String team;
    private String directManagerName;
    private Date hireDate;
    private Date previousContractDuration;
    private String contractTypeName;
    private Const.EVALUATION status;
    private Const.EVALUATION_TRANSFER_STATUS_ENUM evaluationTransferStatus;

    public EvaluationFormViewDto() {
    }

    public EvaluationFormViewDto(EvaluationForm entity) {
        super(entity);
        if (entity != null) {
            this.staffId = entity.getStaff() != null ? entity.getStaff().getId() : null;
            this.staffName = entity.getStaffName();
            this.staffCode = entity.getStaffCode();
            this.position = entity.getPosition();
            this.department = entity.getDepartment();
            this.division = entity.getDivision();
            this.team = entity.getTeam();
            this.directManagerName = entity.getDirectManagerName();
            this.hireDate = entity.getHireDate();
            this.previousContractDuration = entity.getPreviousContractDuration();
            this.contractTypeName = entity.getContractTypeName();
            this.status = entity.getStatus();
            this.evaluationTransferStatus = entity.getEvaluationTransferStatus();
        }
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getDirectManagerName() {
        return directManagerName;
    }

    public void setDirectManagerName(String directManagerName) {
        this.directManagerName = directManagerName;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public Date getPreviousContractDuration() {
        return previousContractDuration;
    }

    public void setPreviousContractDuration(Date previousContractDuration) {
        this.previousContractDuration = previousContractDuration;
    }

    public String getContractTypeName() {
        return contractTypeName;
    }

    public void setContractTypeName(String contractTypeName) {
        this.contractTypeName = contractTypeName;
    }

    public Const.EVALUATION getStatus() {
        return status;
    }

    public void setStatus(Const.EVALUATION status) {
        this.status = status;
    }

    public Const.EVALUATION_TRANSFER_STATUS_ENUM getEvaluationTransferStatus() {
        return evaluationTransferStatus;
    }

    public void setEvaluationTransferStatus(Const.EVALUATION_TRANSFER_STATUS_ENUM evaluationTransferStatus) {
        this.evaluationTransferStatus = evaluationTransferStatus;
    }
}
