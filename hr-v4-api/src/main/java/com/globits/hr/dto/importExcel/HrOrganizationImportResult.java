package com.globits.hr.dto.importExcel;

import com.globits.hr.domain.PersonBankAccount;
import com.globits.hr.domain.Staff;

import java.util.List;

public class HrOrganizationImportResult {
    List<HrOrganizationImport> successImportRows;
    List<HrOrganizationImport> errorImportRows;


    public List<HrOrganizationImport> getSuccessImportRows() {
        return successImportRows;
    }

    public void setSuccessImportRows(List<HrOrganizationImport> successImportRows) {
        this.successImportRows = successImportRows;
    }

    public List<HrOrganizationImport> getErrorImportRows() {
        return errorImportRows;
    }

    public void setErrorImportRows(List<HrOrganizationImport> errorImportRows) {
        this.errorImportRows = errorImportRows;
    }
}
