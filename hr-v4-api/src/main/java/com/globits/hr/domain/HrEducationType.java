package com.globits.hr.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;

/**
 * @author dunghq Loại hình đào tạo
 */
@Entity
@Table(name = "tbl_hr_education_type")

public class HrEducationType extends BaseObject {
    private static final long serialVersionUID = 6175741140832897751L;
    @Column(name = "name")
    private String name;

    @Column(name = "name_eng")
    private String nameEng; // tên tiếng anh

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

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

    public String getNameEng() {
        return nameEng;
    }

    public void setNameEng(String nameEng) {
        this.nameEng = nameEng;
    }

}
