package com.globits.salary.dto;

import com.globits.core.dto.BaseObjectDto;
import com.globits.hr.domain.HRDepartment;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.salary.domain.SalaryConfig;

public class SalaryConfigDepartmentDto extends BaseObjectDto {
    private SalaryConfigDto salaryConfig;
    private HRDepartmentDto department;

    public SalaryConfigDto getSalaryConfig() {
        return salaryConfig;
    }

    public HRDepartmentDto getDepartment() {
        return department;
    }

    public void setSalaryConfig(SalaryConfigDto salaryConfig) {
        this.salaryConfig = salaryConfig;
    }

    public void setDepartment(HRDepartmentDto department) {
        this.department = department;
    }
}
