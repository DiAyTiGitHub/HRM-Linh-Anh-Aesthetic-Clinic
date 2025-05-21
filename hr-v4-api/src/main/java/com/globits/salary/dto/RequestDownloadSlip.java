package com.globits.salary.dto;

import java.util.UUID;

public class RequestDownloadSlip {
    private UUID staffSignatureId;
    private UUID salaryResultStaffId;

    public RequestDownloadSlip(UUID staffSignatureId, UUID salaryResultStaffId) {
        this.staffSignatureId = staffSignatureId;
        this.salaryResultStaffId = salaryResultStaffId;
    }

    public RequestDownloadSlip() {
    }

    public UUID getStaffSignatureId() {
        return staffSignatureId;
    }

    public void setStaffSignatureId(UUID staffSignatureId) {
        this.staffSignatureId = staffSignatureId;
    }

    public UUID getSalaryResultStaffId() {
        return salaryResultStaffId;
    }

    public void setSalaryResultStaffId(UUID salaryResultStaffId) {
        this.salaryResultStaffId = salaryResultStaffId;
    }
}
