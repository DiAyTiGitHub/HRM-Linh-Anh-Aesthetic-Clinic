package com.globits.hr.dto.importExcel;

public class StaffWorkingLocationImport {
    // 0. STT
    private Integer stt;
    // 1. Mã nhân viên
    private String staffCode;
    // 2. Họ và tên
    // 3. Mã địa điểm làm việc
    private String workplaceCode;
    // 4. Địa điêm làm việc
    private String workingLocation;
    // 5. Là địa điểm làm việc chính
    private Boolean isMainLocation;
    private String errorMessage;

    public StaffWorkingLocationImport() {
    }

    public Integer getStt() {
        return stt;
    }

    public void setStt(Integer stt) {
        this.stt = stt;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getWorkplaceCode() {
        return workplaceCode;
    }

    public void setWorkplaceCode(String workplaceCode) {
        this.workplaceCode = workplaceCode;
    }

    public String getWorkingLocation() {
        return workingLocation;
    }

    public void setWorkingLocation(String workingLocation) {
        this.workingLocation = workingLocation;
    }

    public Boolean getMainLocation() {
        return isMainLocation;
    }

    public void setMainLocation(Boolean mainLocation) {
        isMainLocation = mainLocation;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
