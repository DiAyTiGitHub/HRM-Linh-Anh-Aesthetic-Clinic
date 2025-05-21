package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

// Hinh thuc ky luat - Xem day du tai entity HrDiscipline
@Entity
@Table(name = "tbl_discipline")
public class Discipline extends BaseObject {
    private static final long serialVersionUID = -2208752009903206352L;
    @Column(name = "name")
    private String name;
    @Column(name = "code")
    private String code;
    @Column(name = "description")
    private String description;
    @Column(name = "level")
    private Integer level; // muc do

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


}
