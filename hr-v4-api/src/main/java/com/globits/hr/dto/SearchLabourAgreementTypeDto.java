package com.globits.hr.dto;

import java.io.Serializable;

public class SearchLabourAgreementTypeDto implements Serializable {
    private static final long serialVersionUID = -3224707227221291260L;
    private String name;
    private String code;

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
}
