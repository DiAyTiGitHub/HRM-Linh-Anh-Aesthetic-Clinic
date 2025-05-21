package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.PersonBankAccount;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.PersonBankAccountDto;
import com.globits.hr.dto.StaffFamilyRelationshipDto;
import com.globits.hr.dto.importExcel.StaffBankAccountImport;
import com.globits.hr.dto.importExcel.StaffFamilyRelationshipImport;
import com.globits.hr.dto.importExcel.StaffLAImportResult;
import com.globits.hr.dto.search.PersonBankAccountSearchDto;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface PersonBankAccountService extends GenericService<PersonBankAccount, UUID> {
    Page<PersonBankAccountDto> searchByPage(PersonBankAccountSearchDto dto);

    PersonBankAccountDto getById(UUID id);

    PersonBankAccountDto saveOrUpdate(PersonBankAccountDto dto);

    Boolean deleteById(UUID id);

    Boolean deleteMultiple(List<UUID> ids);

    // import/export excel
    List<PersonBankAccountDto> saveStaffBankAccountImportFromExcel(List<StaffBankAccountImport> importData);

    int exportExcelStaffBankAccount(List<StaffBankAccountImport> exportData, Sheet sheet, int rowIndex);

    List<StaffBankAccountImport> importExcelStaffBankAccount(Sheet datatypeSheet);

    List<StaffBankAccountImport> getIEStaffBankAccounts(Staff staff);

}
