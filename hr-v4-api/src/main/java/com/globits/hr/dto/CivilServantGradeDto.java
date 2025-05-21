package com.globits.hr.dto;

import com.globits.hr.domain.CivilServantGrade;
import com.globits.core.dto.BaseObjectDto;

public class CivilServantGradeDto extends BaseObjectDto {

    private Integer grade;

    private String name;

    private String code;

    private Integer nextGradeId;

    private Integer maxGrade;

    private Double salaryCoefficient;

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
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

    public Integer getNextGradeId() {
        return nextGradeId;
    }

    public void setNextGradeId(Integer nextGradeId) {
        this.nextGradeId = nextGradeId;
    }

    public Integer getMaxGrade() {
        return maxGrade;
    }

    public void setMaxGrade(Integer maxGrade) {
        this.maxGrade = maxGrade;
    }

    public Double getSalaryCoefficient() {
        return salaryCoefficient;
    }

    public void setSalaryCoefficient(Double salaryCoefficient) {
        this.salaryCoefficient = salaryCoefficient;
    }

    public CivilServantGradeDto() {
    }

    public CivilServantGradeDto(CivilServantGrade civilServantGrade) {
        if (civilServantGrade != null) {
            this.setId(civilServantGrade.getId());
            this.setGrade(civilServantGrade.getGrade());
            this.setName(civilServantGrade.getName());
            this.setCode(civilServantGrade.getCode());
            this.setNextGradeId(civilServantGrade.getNextGradeId());
            this.setMaxGrade(civilServantGrade.getMaxGrade());
            this.setSalaryCoefficient(civilServantGrade.getSalaryCoefficient());
            this.setDescription(civilServantGrade.getDescription());
        }
    }
}