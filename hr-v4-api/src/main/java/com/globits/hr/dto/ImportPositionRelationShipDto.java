package com.globits.hr.dto;

public class ImportPositionRelationShipDto {
    private String code;
    private String name;
    private String supervisorCode;
    private String SupervisorName;
    private int index = 0;

    private Integer relationshipType; // Loại mối quan hệ của các vị trí. HRConstants.PositionRelationshipType

    private String departmentCode;
    private String departmentName;

    public ImportPositionRelationShipDto() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSupervisorCode() {
        return supervisorCode;
    }

    public void setSupervisorCode(String supervisorCode) {
        this.supervisorCode = supervisorCode;
    }

    public String getSupervisorName() {
        return SupervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        SupervisorName = supervisorName;
    }

    public Integer getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(Integer relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
