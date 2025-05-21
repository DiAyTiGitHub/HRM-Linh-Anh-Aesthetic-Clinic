package com.globits.hr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.HRDepartmentPosition;
import com.globits.hr.domain.PositionTitle;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL) // Bỏ qua các thuộc tính có giá trị null
public class PositionTitleDto extends BaseObjectDto {
    private String name;
    private String code;
    private String shortName;
    private String otherName;
    private String description;
    private Double positionCoefficient;
    private Integer type;
    private PositionTitleDto parent;
    private PositionRoleDto positionRole;
    private RankTitleDto rankTitle;
    private Integer recruitmentDays; // Số ngày tuyển dụng
    private Integer positionTitleType;    // Loại vị trí làm việc. Chi tiết: HrConstants.PositionTitleType
    private List<HRDepartmentDto> departments;

    private Integer workDayCalculationType; // Cách tính ngày công chuẩn trong tháng
    private Double estimatedWorkingDays; // Số ngày làm việc được ước tính
    private String errorMessage;

    public PositionTitleDto() {
    }

    public PositionTitleDto(String name, String code, String shortName, String otherName, String description, Double positionCoefficient, Integer type, PositionTitleDto parent, PositionRoleDto positionRole, RankTitleDto rankTitle) {
        this.name = name;
        this.code = code;
        this.shortName = shortName;
        this.otherName = otherName;
        this.description = description;
        this.positionCoefficient = positionCoefficient;
        this.type = type;
        this.parent = parent;
        this.positionRole = positionRole;
        this.rankTitle = rankTitle;

    }

    public PositionTitleDto(String name, String code, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.otherName = name;
        this.shortName = code;
    }

    public PositionTitleDto(PositionTitle entity, Boolean isGetFull) {
        if (entity == null) return;
        this.setId(entity.getId());
        this.setName(entity.getName());
        this.setCode(entity.getCode());
        this.setDescription(entity.getDescription());
        this.setType(entity.getType());
        this.setPositionCoefficient(entity.getPositionCoefficient());
        this.setShortName(entity.getShortName());
        this.setOtherName(entity.getOtherName());
        this.recruitmentDays = entity.getRecruitmentDays();
//        this.setPositionTitleType(entity.getPositionTitleType());
        this.estimatedWorkingDays = entity.getEstimatedWorkingDays();
        this.workDayCalculationType = entity.getWorkDayCalculationType();

        if (isGetFull) {
            if (entity.getPositionRole() != null) {
                this.positionRole = new PositionRoleDto(entity.getPositionRole());
            }

            if (entity.getRankTitle() != null) {
                this.rankTitle = new RankTitleDto(entity.getRankTitle());
            }

            if (entity.getParent() != null) {
                this.parent = new PositionTitleDto();
                this.getParent().setId(entity.getParent().getId());
                this.getParent().setShortName(entity.getParent().getShortName());
                this.getParent().setCode(entity.getParent().getCode());
                this.getParent().setName(entity.getParent().getName());
            }

            if (entity.getDepartmentPositions() != null && !entity.getDepartmentPositions().isEmpty()) {
                this.departments = new ArrayList<>();
                for (HRDepartmentPosition hrDepartmentPosition : entity.getDepartmentPositions()) {
                    if (hrDepartmentPosition != null && hrDepartmentPosition.getDepartment() != null) {
                        HRDepartmentDto departmentDto = new HRDepartmentDto(hrDepartmentPosition.getDepartment(), false, false);
                        this.departments.add(departmentDto);
                    }
                }
            }
        }
    }

    public PositionTitleDto(PositionTitle entity) {
        this(entity, true);
    }

    public Double getEstimatedWorkingDays() {
        return estimatedWorkingDays;
    }

    public void setEstimatedWorkingDays(Double estimatedWorkingDays) {
        this.estimatedWorkingDays = estimatedWorkingDays;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Double getPositionCoefficient() {
        return positionCoefficient;
    }

    public void setPositionCoefficient(Double positionCoefficient) {
        this.positionCoefficient = positionCoefficient;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public PositionTitleDto getParent() {
        return parent;
    }

    public void setParent(PositionTitleDto parent) {
        this.parent = parent;
    }

    public PositionRoleDto getPositionRole() {
        return positionRole;
    }

    public void setPositionRole(PositionRoleDto positionRole) {
        this.positionRole = positionRole;
    }

    public RankTitleDto getRankTitle() {
        return rankTitle;
    }

    public void setRankTitle(RankTitleDto rankTitle) {
        this.rankTitle = rankTitle;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public Integer getRecruitmentDays() {
        return recruitmentDays;
    }

    public void setRecruitmentDays(Integer recruitmentDays) {
        this.recruitmentDays = recruitmentDays;
    }

    public Integer getPositionTitleType() {
        return positionTitleType;
    }

    public void setPositionTitleType(Integer positionTitleType) {
        this.positionTitleType = positionTitleType;
    }

    public List<HRDepartmentDto> getDepartments() {
        return departments;
    }

    public void setDepartments(List<HRDepartmentDto> departments) {
        this.departments = departments;
    }

    public Integer getWorkDayCalculationType() {
        return workDayCalculationType;
    }

    public void setWorkDayCalculationType(Integer workDayCalculationType) {
        this.workDayCalculationType = workDayCalculationType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}