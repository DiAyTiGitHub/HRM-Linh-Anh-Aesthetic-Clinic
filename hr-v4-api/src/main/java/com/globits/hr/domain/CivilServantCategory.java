package com.globits.hr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;

/*
 * Ngạch công chức
 */

@Table(name = "tbl_civil_servant_category")
@Entity
public class CivilServantCategory extends BaseObject {
    private static final long serialVersionUID = 1L;
    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "voided")
    private Boolean voided;

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

    public Boolean getVoided() {
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

}
