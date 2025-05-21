package com.globits.hr.dto;

import com.globits.hr.domain.CivilServantCategory;
import com.globits.hr.domain.CivilServantCategoryGrade;
import com.globits.hr.domain.CivilServantGrade;
import com.globits.core.dto.BaseObjectDto;

public class CivilServantCategoryGradeDto extends BaseObjectDto {

    private String name;

    private String code;

    private Boolean voided;

    private Double salaryCoefficient;

    private CivilServantCategory civilServantCategory;

    private CivilServantGrade civilServantGrade;

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

    public Boolean getVoided() {
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public Double getSalaryCoefficient() {
        return salaryCoefficient;
    }

    public void setSalaryCoefficient(Double salaryCoefficient) {
        this.salaryCoefficient = salaryCoefficient;
    }

    public CivilServantCategory getCivilServantCategory() {
        return civilServantCategory;
    }

    public void setCivilServantCategory(CivilServantCategory civilServantCategory) {
        this.civilServantCategory = civilServantCategory;
    }

    public CivilServantGrade getCivilServantGrade() {
        return civilServantGrade;
    }

    public void setCivilServantGrade(CivilServantGrade civilServantGrade) {
        this.civilServantGrade = civilServantGrade;
    }

    public CivilServantCategoryGradeDto() {

    }

    public CivilServantCategoryGradeDto(CivilServantCategoryGrade civilServantCategoryGrade) {
        this.setId(civilServantCategoryGrade.getId());
        this.name = civilServantCategoryGrade.getName();
        this.code = civilServantCategoryGrade.getCode();
        this.voided = civilServantCategoryGrade.getVoided();
        this.salaryCoefficient = civilServantCategoryGrade.getSalaryCoefficient();
        this.civilServantCategory = civilServantCategoryGrade.getCivilServantCategory();
        this.civilServantGrade = civilServantCategoryGrade.getCivilServantGrade();
    }
}