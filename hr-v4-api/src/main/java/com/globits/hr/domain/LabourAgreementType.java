package com.globits.hr.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;

/*
 * Loại hợp đồng lao động
 */

@Table(name = "tbl_labour_agreement_type")
@Entity
public class LabourAgreementType extends BaseObject {
    private static final long serialVersionUID = 1L;
    private String code;
    private String name;
    private String languageKey;

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
}
