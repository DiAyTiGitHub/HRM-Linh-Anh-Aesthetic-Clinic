package com.globits.hr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;

/*
 * Loại hợp đồng
 */

@Table(name = "tbl_contract_type")
@Entity
public class ContractType extends BaseObject {
    private static final long serialVersionUID = 1L;
    @Column(name = "code")
    private String code; //mã loại hợp đồng. chi tiết HrConstants.ContractTypeCode
    @Column(name = "name")
    private String name;
    @Column(name = "language_key")
    private String languageKey;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; //mo ta

    private Integer duration; // thoi han (thang)


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
