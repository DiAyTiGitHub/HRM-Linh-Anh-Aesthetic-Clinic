package com.globits.hr.service;

import java.util.List;
import java.util.UUID;

import com.globits.hr.domain.Staff;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.importExcel.StaffBankAccountImport;
import com.globits.hr.dto.importExcel.StaffFamilyRelationshipImport;
import com.globits.hr.dto.importExcel.StaffLAImportResult;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffFamilyRelationship;
import com.globits.hr.dto.StaffFamilyRelationshipDto;
import com.globits.hr.dto.function.StaffFamilyRelationshipFunctionDto;

public interface StaffFamilyRelationshipService extends GenericService<StaffFamilyRelationship, UUID> {
    Page<StaffFamilyRelationshipDto> getPages(int pageIndex, int pageSize);

    List<StaffFamilyRelationshipDto> getAll(UUID id);

    StaffFamilyRelationshipDto getFamilyById(UUID id);

    StaffFamilyRelationshipDto saveFamily(StaffFamilyRelationshipDto familyDto, UUID id);

    StaffFamilyRelationshipDto removeFamily(UUID id);

    Boolean removeLists(List<UUID> ids);


    // import/export excel
    List<StaffFamilyRelationshipDto> saveStaffFamilyRelationshipImportFromExcel(List<StaffFamilyRelationshipImport> importData);

    int exportExcelStaffFamilyRelationship(List<StaffFamilyRelationshipImport> exportData, Sheet sheet, int rowIndex);

    List<StaffFamilyRelationshipImport> importExcelStaffFamilyRelationship(Sheet datatypeSheet);

    List<StaffFamilyRelationshipImport> getIEStaffFamilyRelationship(Staff staff);
}
