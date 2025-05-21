package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

// ly do nghi viec
@Table(name = "tbl_leaving_job_reason")
@Entity
public class LeavingJobReason extends BaseObject {
    private static final long serialVersionUID = 991992518344617174L;

    private String code; // ma ly do
    private String name; // ly do nghi viec

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // ghi chu

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

