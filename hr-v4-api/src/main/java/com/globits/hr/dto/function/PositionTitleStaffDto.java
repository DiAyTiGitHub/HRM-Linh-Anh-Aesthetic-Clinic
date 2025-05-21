package com.globits.hr.dto.function;


import java.util.Date;

public class PositionTitleStaffDto {
    private String staffCode;
    private String positionTitleCode;
    private String decisionCode;
    private Double allowanceCoefficient;
    private Boolean current;
    private Date fromDate;
    private Date toDate;
    private String departmentStr;
    private String note;
    private Date decisionDate;
    private Boolean connectedAllowanceProcess;
    private Boolean mainPosition;
    private String departmentCode;

    public PositionTitleStaffDto() {
        super();
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getPositionTitleCode() {
        return positionTitleCode;
    }

    public void setPositionTitleCode(String positionTitleCode) {
        this.positionTitleCode = positionTitleCode;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDecisionCode() {
        return decisionCode;
    }

    public void setDecisionCode(String decisionCode) {
        this.decisionCode = decisionCode;
    }

    public Double getAllowanceCoefficient() {
        return allowanceCoefficient;
    }

    public void setAllowanceCoefficient(Double allowanceCoefficient) {
        this.allowanceCoefficient = allowanceCoefficient;
    }

    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public String getDepartmentStr() {
        return departmentStr;
    }

    public void setDepartmentStr(String departmentStr) {
        this.departmentStr = departmentStr;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getDecisionDate() {
        return decisionDate;
    }

    public void setDecisionDate(Date decisionDate) {
        this.decisionDate = decisionDate;
    }

    public Boolean getConnectedAllowanceProcess() {
        return connectedAllowanceProcess;
    }

    public void setConnectedAllowanceProcess(Boolean connectedAllowanceProcess) {
        this.connectedAllowanceProcess = connectedAllowanceProcess;
    }

    public Boolean getMainPosition() {
        return mainPosition;
    }

    public void setMainPosition(Boolean mainPosition) {
        this.mainPosition = mainPosition;
    }
}
