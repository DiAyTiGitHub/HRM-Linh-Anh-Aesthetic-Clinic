package com.globits.hr.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.RankTitle;
import jakarta.persistence.Column;

public class RankTitleDto extends BaseObjectDto {
    private static final long serialVersionUID = 1L;

    private Integer level;
    private Integer subLevel;
    private String name;
    private String otherName;
    private String shortName;
    private String description;
    private Double socialInsuranceSalary; // Lương đóng BHXH
    private Double referralFeeLevel; // Mức hưởng chi phí giới thiệu
    private String errorMessage;

    public RankTitleDto() {
    }

    public RankTitleDto(Integer level, Integer subLevel, String name, String otherName, String shortName, String description) {
        this.level = level;
        this.subLevel = subLevel;
        this.name = name;
        this.otherName = otherName;
        this.shortName = shortName;
        this.description = description;

    }

    public RankTitleDto(RankTitle entity) {
        if (entity == null) return;

        this.id = entity.getId();
        this.level = entity.getLevel();
        this.subLevel = entity.getSubLevel();
        this.name = entity.getName();
        this.otherName = entity.getOtherName();
        this.shortName = entity.getShortName();
        this.description = entity.getDescription();
        this.socialInsuranceSalary = entity.getSocialInsuranceSalary();
        this.referralFeeLevel = entity.getReferralFeeLevel();
    }


    public Double getReferralFeeLevel() {
        return referralFeeLevel;
    }

    public void setReferralFeeLevel(Double referralFeeLevel) {
        this.referralFeeLevel = referralFeeLevel;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getSubLevel() {
        return subLevel;
    }

    public void setSubLevel(Integer subLevel) {
        this.subLevel = subLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getSocialInsuranceSalary() {
        return socialInsuranceSalary;
    }

    public void setSocialInsuranceSalary(Double socialInsuranceSalary) {
        this.socialInsuranceSalary = socialInsuranceSalary;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
