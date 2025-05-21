package com.globits.salary.domain;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.globits.core.domain.BaseObject;

@Table(name = "tbl_salary_config")
@Entity
public class SalaryConfig extends BaseObject {
    private static final long serialVersionUID = 7438693553216933636L;
    @Column(name = "name")
    private String name;// ten

    @Column(name = "other_name")
    private String otherName;// ten khac

    @Column(name = "code", unique = true)
    private String code;// ma

    @Column(name = "description")
    private String description;// mo ta

    @Column(name = "default_value")
    private Integer defaultValue;// gia tri mac dinh

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_type_id")
    private SalaryType salaryType;// nhom du lieu

    @OneToMany(mappedBy = "salaryConfig", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SalaryConfigDepartment> departments = new HashSet<SalaryConfigDepartment>();// cac phong ban ap dung

    @OneToMany(mappedBy = "salaryConfig", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SalaryConfigItem> salaryConfigItems = new HashSet<SalaryConfigItem>();

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

    public Set<SalaryConfigItem> getSalaryConfigItems() {
        return salaryConfigItems;
    }

    public void setSalaryConfigItems(Set<SalaryConfigItem> salaryConfigItems) {
        this.salaryConfigItems = salaryConfigItems;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Integer defaultValue) {
        this.defaultValue = defaultValue;
    }

    public SalaryType getSalaryType() {
        return salaryType;
    }

    public void setSalaryType(SalaryType salaryType) {
        this.salaryType = salaryType;
    }

    public Set<SalaryConfigDepartment> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<SalaryConfigDepartment> departments) {
        this.departments = departments;
    }

    public SalaryConfig() {

    }

    public SalaryConfig(String name, String otherName, String code, String description, Integer defaultValue) {
        this.name = name;
        this.otherName = otherName;
        this.code = code;
        this.description = description;
        this.defaultValue = defaultValue;
    }
}
