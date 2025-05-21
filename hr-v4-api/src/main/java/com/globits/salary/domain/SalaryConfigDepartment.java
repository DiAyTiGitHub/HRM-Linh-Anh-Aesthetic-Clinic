package com.globits.salary.domain;

import com.globits.core.domain.BaseObject;
import com.globits.hr.domain.HRDepartment;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Table(name = "tbl_salary_config_department")
@Entity
public class SalaryConfigDepartment extends BaseObject {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "salary_config_id")
    private SalaryConfig salaryConfig;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private HRDepartment department;

    public HRDepartment getDepartment() {
        return department;
    }

    public void setDepartment(HRDepartment department) {
        this.department = department;
    }

    public SalaryConfig getSalaryConfig() {
        return salaryConfig;
    }

    public void setSalaryConfig(SalaryConfig salaryConfig) {
        this.salaryConfig = salaryConfig;
    }

}
