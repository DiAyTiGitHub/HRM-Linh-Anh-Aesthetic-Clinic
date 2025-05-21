package com.globits.hr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

// Hinh thuc ky luat
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("HRDiscipline")
public class HRDiscipline extends Discipline {
    private static final long serialVersionUID = 1L;

    @Column(name = "language_key")
    private String languageKey; // ngon ngu hien thi (Tieng Viet/ Tieng Anh/ Tieng Trung Quoc,...)

    @Column(name = "discipline_type")
    private Integer disciplineType; // Loại hình thức

    @Column(name = "formal")
    private Integer formal; // Tinh chat hinh thuc

    @Column(name = "evaluate_year")
    private Integer evaluateYear; // Nam hieu luc


    public String getLanguageKey() {
        return languageKey;
    }

    public void setLanguageKey(String languageKey) {
        this.languageKey = languageKey;
    }

    public Integer getDisciplineType() {
        return disciplineType;
    }

    public void setDisciplineType(Integer disciplineType) {
        this.disciplineType = disciplineType;
    }

    public Integer getFormal() {
        return formal;
    }

    public void setFormal(Integer formal) {
        this.formal = formal;
    }

    public Integer getEvaluateYear() {
        return evaluateYear;
    }

    public void setEvaluateYear(Integer evaluateYear) {
        this.evaluateYear = evaluateYear;
    }

}
