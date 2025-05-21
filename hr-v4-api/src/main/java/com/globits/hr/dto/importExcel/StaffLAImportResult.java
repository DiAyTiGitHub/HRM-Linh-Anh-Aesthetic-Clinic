package com.globits.hr.dto.importExcel;

import com.globits.hr.dto.StaffFamilyRelationshipDto;

import java.util.Date;
import java.util.List;

public class StaffLAImportResult {
    List<StaffLAImport> successStaffImports;
    List<StaffLAImport> errorStaffImports;

    List<StaffFamilyRelationshipImport> successStaffRelationshipsImports;
    List<StaffFamilyRelationshipImport> errorStaffRelationshipsImports;

    List<StaffBankAccountImport> successStaffBankAccountsImports;
    List<StaffBankAccountImport> errorStaffBankAccountsImports;


    public List<StaffLAImport> getSuccessStaffImports() {
        return successStaffImports;
    }

    public void setSuccessStaffImports(List<StaffLAImport> successStaffImports) {
        this.successStaffImports = successStaffImports;
    }

    public List<StaffLAImport> getErrorStaffImports() {
        return errorStaffImports;
    }

    public void setErrorStaffImports(List<StaffLAImport> errorStaffImports) {
        this.errorStaffImports = errorStaffImports;
    }

    public List<StaffFamilyRelationshipImport> getSuccessStaffRelationshipsImports() {
        return successStaffRelationshipsImports;
    }

    public void setSuccessStaffRelationshipsImports(List<StaffFamilyRelationshipImport> successStaffRelationshipsImports) {
        this.successStaffRelationshipsImports = successStaffRelationshipsImports;
    }

    public List<StaffFamilyRelationshipImport> getErrorStaffRelationshipsImports() {
        return errorStaffRelationshipsImports;
    }

    public void setErrorStaffRelationshipsImports(List<StaffFamilyRelationshipImport> errorStaffRelationshipsImports) {
        this.errorStaffRelationshipsImports = errorStaffRelationshipsImports;
    }

    public List<StaffBankAccountImport> getSuccessStaffBankAccountsImports() {
        return successStaffBankAccountsImports;
    }

    public void setSuccessStaffBankAccountsImports(List<StaffBankAccountImport> successStaffBankAccountsImports) {
        this.successStaffBankAccountsImports = successStaffBankAccountsImports;
    }

    public List<StaffBankAccountImport> getErrorStaffBankAccountsImports() {
        return errorStaffBankAccountsImports;
    }

    public void setErrorStaffBankAccountsImports(List<StaffBankAccountImport> errorStaffBankAccountsImports) {
        this.errorStaffBankAccountsImports = errorStaffBankAccountsImports;
    }
}
