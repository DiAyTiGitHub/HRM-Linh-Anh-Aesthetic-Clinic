package com.globits.salary.dto.search;

import java.util.UUID;

public class CalculateSalaryRequest {
    private UUID staffId;
    private UUID salaryPeriodId;
    private UUID salaryTemplateId;

    public CalculateSalaryRequest() {
    }

    public CalculateSalaryRequest(UUID staffId, UUID salaryPeriodId, UUID salaryTemplateId) {
        this.staffId = staffId;
        this.salaryPeriodId = salaryPeriodId;
        this.salaryTemplateId = salaryTemplateId;
    }

    public UUID getStaffId() {
        return staffId;
    }

    public void setStaffId(UUID staffId) {
        this.staffId = staffId;
    }

    public UUID getSalaryPeriodId() {
        return salaryPeriodId;
    }

    public void setSalaryPeriodId(UUID salaryPeriodId) {
        this.salaryPeriodId = salaryPeriodId;
    }

    public UUID getSalaryTemplateId() {
        return salaryTemplateId;
    }

    public void setSalaryTemplateId(UUID salaryTemplateId) {
        this.salaryTemplateId = salaryTemplateId;
    }
}
