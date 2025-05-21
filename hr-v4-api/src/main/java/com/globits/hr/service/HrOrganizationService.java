package com.globits.hr.service;

import com.globits.hr.dto.HrOrganizationDto;
import com.globits.hr.dto.PersonBankAccountDto;
import com.globits.hr.dto.importExcel.HrOrganizationImport;
import com.globits.hr.dto.importExcel.HrOrganizationImportResult;
import com.globits.hr.dto.importExcel.StaffBankAccountImport;
import com.globits.hr.dto.importExcel.StaffLAImportResult;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchStaffDto;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface HrOrganizationService {
    HrOrganizationDto saveOrUpdate(HrOrganizationDto dto);

    Boolean deleteHrOrganization(UUID id);

    Boolean deleteMultipleHrOrganizations(List<UUID> ids);

    HrOrganizationDto getHROrganization(UUID id);

    Page<HrOrganizationDto> searchByPage(SearchDto dto);

    Boolean checkCode(UUID id, String code);

    Page<HrOrganizationDto> pagingHrOrganizations(SearchDto dto);

    Boolean isValidCode(HrOrganizationDto dto);

    HrOrganizationImportResult readDataFromExcel(InputStream inputStream);

    HrOrganizationImportResult saveHrOrganizationImportFromExcel(HrOrganizationImportResult importResults);

    Workbook exportExcelHrOrganization(SearchDto dto);

    String autoGenerateCode(String configKey);
}