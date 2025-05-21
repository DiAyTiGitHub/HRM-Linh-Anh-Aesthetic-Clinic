package com.globits.hr.dto;

import com.globits.hr.domain.HRDiscipline;

public class HRDisciplineDto extends DisciplineDto {
    private String name;
    private String code;
    private String description;
    private String languageKey;
    private Integer disciplineType;
    private Integer formal;
    private Integer evaluateYear;

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

    public HRDisciplineDto() {
        super();
    }

    public HRDisciplineDto(HRDiscipline entity) {
        super();
        if (entity != null) {
            this.setId(entity.getId());
            this.setLanguageKey(entity.getLanguageKey());
            this.setDisciplineType(entity.getDisciplineType());
            this.setFormal(entity.getFormal());
            this.setEvaluateYear(entity.getEvaluateYear());
            this.setLevel(entity.getLevel());
            this.setCode(entity.getCode());
            this.setName(entity.getName());
            this.setDescription(entity.getDescription());
        }
    }
}
