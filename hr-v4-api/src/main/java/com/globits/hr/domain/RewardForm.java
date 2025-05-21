package com.globits.hr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;

/*
 * Danh mục hình thức khen thưởng ví dụ :
 * Huân chương lao động hạng nhất, huân chương lao động hạng nhì, ...
 */

@Table(name = "tbl_reward_form")
@Entity
public class RewardForm extends BaseObject {
    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    private String name;
    @Column(name = "language_key")
    private String languageKey;
    @Column(name = "code")
    private String code;
    @Column(name = "reward_type")
    private Integer rewardType;
    @Column(name = "formal")
    private Integer formal;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "evaluate_year")
    private Integer evaluateYear;
    @Column(name = "evaluate_level")
    private Integer evaluateLevel;

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

    public String getLanguageKey() {
        return languageKey;
    }

    public void setLanguageKey(String languageKey) {
        this.languageKey = languageKey;
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
