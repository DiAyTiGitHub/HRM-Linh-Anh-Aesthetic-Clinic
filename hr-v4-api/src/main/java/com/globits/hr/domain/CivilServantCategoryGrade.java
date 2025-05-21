package com.globits.hr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;

/*
 * Bậc công chức
 */

@Table(name = "tbl_civil_servant_category_grade")
@Entity
public class CivilServantCategoryGrade extends BaseObject {
    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "voided")
    private Boolean voided;

    @Column(name = "salary_coefficient")
    private Double salaryCoefficient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "civil_servant_category_id")
    private CivilServantCategory civilServantCategory;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "civil_servant_grade_id")
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

}
