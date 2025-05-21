package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.HrConstants;
import jakarta.persistence.*;

@Entity
@Table(name = "tbl_evaluation_item")
public class EvaluationItem extends BaseObject {
    @Column(name = "name")
    private String name;
    @Column(name = "code")
    private String code;
    @Column(name = "description")
    private String description;

    public EvaluationItem() {
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
