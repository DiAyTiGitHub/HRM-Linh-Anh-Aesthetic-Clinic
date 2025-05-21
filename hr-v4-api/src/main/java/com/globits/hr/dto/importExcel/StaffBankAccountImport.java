package com.globits.hr.dto.importExcel;

import com.globits.hr.domain.PersonBankAccount;
import com.globits.hr.domain.Staff;

public class StaffBankAccountImport {
    private Integer stt;
    private String staffCode;
    private String staffDisplayName;
    private String bankAccountNumber; // Số tài khoản ngân hàng
    private String bankCode; // Mã Ngân hàng
    private String bankName; // Ngân hàng
    private String bankBranch; // Chi nhánh ngân hàng
    private Boolean isMain; // Tài khoản chính

    private String errorMessage;

    public StaffBankAccountImport() {
    }

    public StaffBankAccountImport(PersonBankAccount entity, Staff staff) {
        if (entity == null || staff == null)
            return;

        this.staffCode = staff.getStaffCode();
        this.staffDisplayName = staff.getDisplayName();

        this.bankAccountNumber = entity.getBankAccountNumber();
        this.bankBranch = entity.getBankBranch();
        this.isMain = entity.getIsMain();

        if (entity.getBank() != null) {
            this.bankCode = entity.getBank().getCode();
            this.bankName = entity.getBank().getName();
        }

    }


    public Integer getStt() {
        return stt;
    }

    public void setStt(Integer stt) {
        this.stt = stt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getStaffDisplayName() {
        return staffDisplayName;
    }

    public void setStaffDisplayName(String staffDisplayName) {
        this.staffDisplayName = staffDisplayName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public Boolean getMain() {
        return isMain;
    }

    public void setMain(Boolean main) {
        isMain = main;
    }
}
