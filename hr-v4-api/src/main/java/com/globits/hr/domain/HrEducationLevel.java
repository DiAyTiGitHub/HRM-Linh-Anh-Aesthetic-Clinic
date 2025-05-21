package com.globits.hr.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;


import com.globits.core.domain.BaseObject;

/**
 * @author dunghq Bậc đào tạo (đại học, cao đẳng, trung cấp, dạy nghề, ....)
 */
@Entity
@Table(name = "tbl_hr_education_level")

public class HrEducationLevel extends BaseObject {
    private static final long serialVersionUID = 6528630179273861172L;
    @Column(name = "name")
    private String name;
    @Column(name = "code")
    private String code;
    @Column(name = "numberic_code")
    private String numbericCode;
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

    public String getNumbericCode() {
        return numbericCode;
    }

    public void setNumbericCode(String numbericCode) {
        this.numbericCode = numbericCode;
    }

}
