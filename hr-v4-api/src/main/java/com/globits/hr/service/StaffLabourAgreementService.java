package com.globits.hr.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.globits.budget.dto.VoucherDto;
import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.importExcel.HrOrganizationImportResult;
import com.globits.hr.dto.search.SearchStaffLabourAgreementDto;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffLabourAgreement;
import com.globits.hr.dto.LabourAgreementDto;
import com.globits.hr.dto.StaffLabourAgreementDto;

public interface StaffLabourAgreementService extends GenericService<StaffLabourAgreement, UUID> {
    StaffLabourAgreementDto saveAgreement(StaffLabourAgreementDto agreementDto, UUID id);

    StaffLabourAgreementDto getById(UUID id);

    //save labour agreement V2
    StaffLabourAgreementDto saveOrUpdate(StaffLabourAgreementDto dto);

    StaffLabourAgreementDto deleteById(UUID id);

    boolean deleteMultiple(List<UUID> ids);

    Page<StaffLabourAgreementDto> pagingLabourAgreement(SearchStaffLabourAgreementDto searchDto);

    XWPFDocument generateDocx(StaffLabourAgreementDto agreementDto) throws IOException;

    StaffLabourAgreementDto getTotalHasSocialIns(SearchStaffLabourAgreementDto searchDto);

    Workbook handleExcel(SearchStaffLabourAgreementDto dto);

    List<StaffLabourAgreementDto> getAllStaffLabourAgreementWithSearch(SearchStaffLabourAgreementDto dto);

    void exportHICInfoToWord(HttpServletResponse response, UUID staffId) throws IOException;

    List<StaffLabourAgreementDto> getAll();

    Boolean checkOverdueContract(SearchStaffLabourAgreementDto searchDto);

    HashMap<UUID, LabourAgreementDto> getLabourAgreementLatestMap();

    ApiResponse<StaffLabourAgreementDto> getLastLabourAgreement(UUID staffId);

    List<StaffLabourAgreementDto> readDataFromExcel(ByteArrayInputStream bis);

    Integer saveStaffLabourAgreementImportFromExcel(List<StaffLabourAgreementDto> importResults);

    Workbook exportExcelStaffLabourAgreement(SearchStaffLabourAgreementDto searchDto);
}
