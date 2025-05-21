package com.globits.hr.dto;

import com.globits.hr.domain.LabourAgreementType;
import com.globits.core.dto.BaseObjectDto;

public class LabourAgreementTypeDto extends BaseObjectDto {
    private String name;
    private String code;
    private String languageKey;

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

    public LabourAgreementTypeDto() {
    }

    public LabourAgreementTypeDto(LabourAgreementType labourAgreement) {
        if (labourAgreement != null) {
            this.setId(labourAgreement.getId());
            this.setName(labourAgreement.getName());
            this.setCode(labourAgreement.getCode());
            this.setLanguageKey(labourAgreement.getLanguageKey());
        }
    }
}
