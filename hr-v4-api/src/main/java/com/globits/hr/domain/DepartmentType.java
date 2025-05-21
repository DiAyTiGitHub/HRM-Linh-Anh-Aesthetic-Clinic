package com.globits.hr.domain;

import com.globits.core.domain.BaseObject;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Table(name = "tbl_department_type")
@Entity
public class DepartmentType extends BaseObject {
    @Column(name = "sort_number")
    private Integer sortNumber;// trong so

    @Column(name = "name")
    private String name;// ten phong ban

    @Column(name = "code")
    private String code;// ma phong ban

    @Column(name = "other_name")
    private String otherName;// ten khac

    @Column(name = "short_name")
    private String shortName;// ten viet tat

    @Column(name = "description")
    private String description;// mo ta

    public Integer getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(Integer sortNumber) {
        this.sortNumber = sortNumber;
    }

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

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
