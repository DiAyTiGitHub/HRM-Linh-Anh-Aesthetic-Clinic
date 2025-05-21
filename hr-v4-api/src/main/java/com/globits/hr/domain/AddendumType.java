package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

// loai phu lục
@Table(name = "tbl_addendum_type")
@Entity
public class AddendumType extends BaseObject {
    private static final long serialVersionUID = 991992518344617174L;

    private String code; // ma loai phu luc
    private String name; // loai phu luc

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // mo ta

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

