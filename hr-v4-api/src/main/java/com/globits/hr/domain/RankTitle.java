package com.globits.hr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.globits.core.domain.BaseObject;

/*
 * Cấp bậc
 */
@Table(name = "tbl_rank_title")
@Entity
public class RankTitle extends BaseObject {
    private static final long serialVersionUID = 1L;

    @Column(name = "level")
    private Integer level;// level

    @Column(name = "sub_level")
    private Integer subLevel;// sub-level

    @Column(name = "name")
    private String name; // Tên

    @Column(name = "other_name")
    private String otherName;// ten khac

    @Column(name = "short_name")
    private String shortName;// ten viet tat

    @Column(name = "description")
    private String description; // mo ta

    @Column(name = "social_insurance_salary")
    private Double socialInsuranceSalary; // Lương đóng BHXH

    @Column(name = "referral_fee_level")
    private Double referralFeeLevel; // Mức hưởng chi phí giới thiệu


    public Double getReferralFeeLevel() {
        return referralFeeLevel;
    }

    public void setReferralFeeLevel(Double referralFeeLevel) {
        this.referralFeeLevel = referralFeeLevel;
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

    public Double getSocialInsuranceSalary() {
        return socialInsuranceSalary;
    }

    public void setSocialInsuranceSalary(Double socialInsuranceSalary) {
        this.socialInsuranceSalary = socialInsuranceSalary;
    }
}
