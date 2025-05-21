package com.globits.hr.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.function.ImportStaffDto;
import com.globits.hr.dto.importExcel.StaffLAImport;
import com.globits.hr.dto.importExcel.StaffLAImportResult;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.dto.staff.StaffLabourManagementDto;
import com.globits.hr.dto.staff.StaffLabourUtilReportDto;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.Staff;
import com.globits.hrv3.dto.view.GeneralInformationDto;
import com.globits.hrv3.dto.view.ProfileInformationDto;

@Service
public interface StaffServiceV3 extends GenericService<Staff, UUID> {
    GeneralInformationDto saveOrUpdateGeneralInformation(UUID id, GeneralInformationDto staffDto);

    // sổ quản lý lao động
    Page<StaffLabourManagementDto> pagingStaffLabourManagement(SearchStaffDto dto);

    // báo cáo tính hình sử dụng lao động
    Page<StaffLabourUtilReportDto> pagingStaffLabourUtilReport(SearchStaffDto dto);

    GeneralInformationDto getGeneralInformation(UUID id);

    ProfileInformationDto saveOrUpdateProfileInformation(UUID id, ProfileInformationDto staffDto);

    ProfileInformationDto getProfileInformation(UUID id);

    List<StaffLAImport> saveStaffLAImportFromExcel(List<StaffLAImport> staffImportData, boolean isCreateNew);

    Workbook exportImportStaffLAResults(ImportStaffDto importStaffDto);

    String generateNewStaffCode(SearchStaffDto dto);

    String generateNewStaffCodeV2(SearchStaffDto dto);

    List<UUID> generateFixScheduleForChosenStaffs(SearchStaffWorkScheduleDto dto);

    // Xuất sổ quản lý lao động
    Workbook exportLaborManagementBook(SearchStaffDto searchStaffDto) throws IOException;

    // Xuất báo cáo tính hình sử dụng lao động
    Workbook exportReportLabourUsage(SearchStaffDto searchDto) throws IOException;

}
