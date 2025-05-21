package com.globits.timesheet.dto.importExcel;

import com.globits.hr.domain.Staff;

import java.util.Date;
import java.util.List;

public class OvertimeRequestImport {
    // 0. STT
    private Integer importOrder;
    // 1. Mã NV yêu cầu
    private String staffCode;
    // 2. Tên NV yêu cầu
    private String staffName;
    // 3. Ngày làm việc (dd-mm-yyyy)
    private Date workingDate;
    // 4. Mã ca làm việc
    private String shiftWorkCode;
    // 5. Tên ca làm việc
    private String shiftWorkName;
    // 6. Số giờ YC tính trước ca
    private Double requestOTHoursBeforeShift;  // Số giờ làm thêm trước ca làm việc được yêu cầu tính OT
    // 7. Số giờ YC tính sau ca
    private Double requestOTHoursAfterShift; // Số giờ làm thêm sau ca làm việc được yêu cầu tính OT
    // 8. Trạng thái phê duyệt YC (Chưa duyệt, Đã duyệt, Không duyệt)
    private Integer approvalStatus; // Trạng thái phê duyệt. Chi tiết: HrConstants.OvertimeRequestApprovalStatus
    // 9. Mã NV phê duyệt
    private String otEndorserCode; // Người xác nhận OT cho nhân viên
    //10. Tên NV phê duyệt
    private String otEndorserName; // Người xác nhận OT cho nhân viên
    //11. Số giờ trước ca được duyệt
    private Double confirmedOTHoursBeforeShift = 0D; // Số giờ làm thêm trước ca làm việc đã được xác nhận
    //12. Số giờ sau ca được duyệt
    private Double confirmedOTHoursAfterShift = 0D; // Số giờ làm thêm sau ca làm việc đã được xác nhận

    private List<String> errorMessages;

    public OvertimeRequestImport() {
    }


    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public Integer getImportOrder() {
        return importOrder;
    }

    public void setImportOrder(Integer importOrder) {
        this.importOrder = importOrder;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Date getWorkingDate() {
        return workingDate;
    }

    public void setWorkingDate(Date workingDate) {
        this.workingDate = workingDate;
    }

    public String getShiftWorkCode() {
        return shiftWorkCode;
    }

    public void setShiftWorkCode(String shiftWorkCode) {
        this.shiftWorkCode = shiftWorkCode;
    }

    public String getShiftWorkName() {
        return shiftWorkName;
    }

    public void setShiftWorkName(String shiftWorkName) {
        this.shiftWorkName = shiftWorkName;
    }

    public Double getRequestOTHoursBeforeShift() {
        return requestOTHoursBeforeShift;
    }

    public void setRequestOTHoursBeforeShift(Double requestOTHoursBeforeShift) {
        this.requestOTHoursBeforeShift = requestOTHoursBeforeShift;
    }

    public Double getRequestOTHoursAfterShift() {
        return requestOTHoursAfterShift;
    }

    public void setRequestOTHoursAfterShift(Double requestOTHoursAfterShift) {
        this.requestOTHoursAfterShift = requestOTHoursAfterShift;
    }

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getOtEndorserCode() {
        return otEndorserCode;
    }

    public void setOtEndorserCode(String otEndorserCode) {
        this.otEndorserCode = otEndorserCode;
    }

    public String getOtEndorserName() {
        return otEndorserName;
    }

    public void setOtEndorserName(String otEndorserName) {
        this.otEndorserName = otEndorserName;
    }

    public Double getConfirmedOTHoursBeforeShift() {
        return confirmedOTHoursBeforeShift;
    }

    public void setConfirmedOTHoursBeforeShift(Double confirmedOTHoursBeforeShift) {
        this.confirmedOTHoursBeforeShift = confirmedOTHoursBeforeShift;
    }

    public Double getConfirmedOTHoursAfterShift() {
        return confirmedOTHoursAfterShift;
    }

    public void setConfirmedOTHoursAfterShift(Double confirmedOTHoursAfterShift) {
        this.confirmedOTHoursAfterShift = confirmedOTHoursAfterShift;
    }
}
