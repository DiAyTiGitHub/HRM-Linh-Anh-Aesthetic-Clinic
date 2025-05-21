package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.RewardForm;

public class RewardFormDto extends BaseObjectDto {
    private String name;
    private String languageKey;
    private String code;
    private Integer rewardType;
    private Integer formal;
    private String description;
    private Integer evaluateYear;
    private Integer evaluateLevel;

    public RewardFormDto() {
        super();
    }

    public RewardFormDto(String name, String languageKey, String code, Integer rewardType, Integer formal, String description, Integer evaluateYear, Integer evaluateLevel) {
        this.name = name;
        this.languageKey = languageKey;
        this.code = code;
        this.rewardType = rewardType;
        this.formal = formal;
        this.description = description;
        this.evaluateYear = evaluateYear;
        this.evaluateLevel = evaluateLevel;
    }

    public RewardFormDto(RewardForm entity) {
        super();
        if (entity != null) {
            this.setId(entity.getId());
            this.setName(entity.getName());
            this.setLanguageKey(entity.getLanguageKey());
            this.setCode(entity.getCode());
            this.setRewardType(entity.getRewardType());
            this.setFormal(entity.getFormal());
            this.setDescription(entity.getDescription());
            this.setEvaluateYear(entity.getEvaluateYear());
            this.setEvaluateLevel(entity.getEvaluateLevel());
        }
    }

    public RewardForm toEntity() {
        RewardForm entity = new RewardForm();
        if (this.id != null) {
            entity.setId(this.id);
        }
        entity.setName(this.name);
        entity.setLanguageKey(this.languageKey);
        entity.setCode(this.code);
        entity.setRewardType(this.rewardType);
        entity.setFormal(this.formal);
        entity.setDescription(this.description);
        entity.setEvaluateYear(this.evaluateYear);
        entity.setEvaluateLevel(this.evaluateLevel);
        return entity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguageKey() {
        return languageKey;
    }

    public void setLanguageKey(String languageKey) {
        this.languageKey = languageKey;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getRewardType() {
        return rewardType;
    }

    public void setRewardType(Integer rewardType) {
        this.rewardType = rewardType;
    }

    public Integer getFormal() {
        return formal;
    }

    public void setFormal(Integer formal) {
        this.formal = formal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getEvaluateYear() {
        return evaluateYear;
    }

    public void setEvaluateYear(Integer evaluateYear) {
        this.evaluateYear = evaluateYear;
    }

    public Integer getEvaluateLevel() {
        return evaluateLevel;
    }

    public void setEvaluateLevel(Integer evaluateLevel) {
        this.evaluateLevel = evaluateLevel;
    }

}
