package com.globits.hr.dto.function;

import java.util.List;

import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.importExcel.StaffBankAccountImport;
import com.globits.hr.dto.importExcel.StaffFamilyRelationshipImport;
import com.globits.hr.dto.importExcel.StaffLAImport;
import com.globits.hr.dto.importExcel.StaffWorkingLocationImport;

public class ImportStaffDto {
    List<StaffDto> listStaff;
    List<StaffLAImport> staffImports;
    List<StaffWorkingLocationImport> staffWorkingLocationImports;
    List<ImportExcelMessageDto> listMessage;
    List<StaffFamilyRelationshipImport> staffFamilyRelationshipImports;
    List<StaffBankAccountImport> staffBankAccountImports;


    public List<StaffLAImport> getStaffImports() {
        return staffImports;
    }

    public void setStaffImports(List<StaffLAImport> staffImports) {
        this.staffImports = staffImports;
    }

    public List<StaffWorkingLocationImport> getStaffWorkingLocationImports() {
        return staffWorkingLocationImports;
    }

    public void setStaffWorkingLocationImports(List<StaffWorkingLocationImport> staffWorkingLocationImports) {
        this.staffWorkingLocationImports = staffWorkingLocationImports;
    }

    public List<StaffFamilyRelationshipImport> getStaffFamilyRelationshipImports() {
        return staffFamilyRelationshipImports;
    }

    public void setStaffFamilyRelationshipImports(List<StaffFamilyRelationshipImport> staffFamilyRelationshipImports) {
        this.staffFamilyRelationshipImports = staffFamilyRelationshipImports;
    }

    public List<StaffBankAccountImport> getStaffBankAccountImports() {
        return staffBankAccountImports;
    }

    public void setStaffBankAccountImports(List<StaffBankAccountImport> staffBankAccountImports) {
        this.staffBankAccountImports = staffBankAccountImports;
    }

    public List<StaffDto> getListStaff() {
        return listStaff;
    }

    public void setListStaff(List<StaffDto> listStaff) {
        this.listStaff = listStaff;
    }

    public List<ImportExcelMessageDto> getListMessage() {
        return listMessage;
    }

    public void setListMessage(List<ImportExcelMessageDto> listMessage) {
        this.listMessage = listMessage;
    }
}
