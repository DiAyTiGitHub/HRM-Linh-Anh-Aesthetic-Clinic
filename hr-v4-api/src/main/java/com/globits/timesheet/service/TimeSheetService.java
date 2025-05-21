package com.globits.timesheet.service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.globits.timesheet.dto.*;
import com.globits.timesheet.dto.search.SearchTimeSheetDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.hr.dto.SearchReportDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.SynthesisReportOfStaffDto;
import com.globits.timesheet.domain.TimeSheet;

public interface TimeSheetService extends GenericService<TimeSheet, UUID> {
    TimeSheetDto findTimeSheetById(UUID id);

    TimeSheet getEntityById(UUID id);

    Boolean deleteTimeSheetById(UUID id);

    Boolean deleteTimeSheets(List<TimeSheetDto> list);

    Page<StaffDto> findPageByName(String textSearch, int pageIndex, int pageSize);

    Page<TimeSheetDto> getAllByWorkingDate(Date workingDate, int pageIndex, int pageSize);

    Page<TimeSheetDetailDto> getTimeSheetDetailByTimeSheetID(UUID id, int pageIndex, int pageSize);

    Boolean confirmTimeSheets(List<TimeSheetDto> list);

    Page<TimeSheetDto> findPageByStaff(String textSearch, int pageIndex, int pageSize);

    Page<TimeSheetDto> searchByDto(SearchTimeSheetDto searchTimeSheetDto, int pageIndex, int pageSize);

    Page<SynthesisReportOfStaffDto> reportWorkingStatus(SearchReportDto searchReportDto, int pageIndex, int pageSize);

    Page<TimeSheetDto> searchByPage(SearchTimeSheetDto dto);

    TimeSheetDto saveOrUpdate(UUID id, TimeSheetDto dto);

    String updateStatus(UUID id, UUID workingStatusId);

    List<TimeSheetDto> getListTimeSheetByProjectActivityId(UUID id);

    TimeSheetDto timekeepingService(TimekeepingItemDto dto, UUID id);

    TimekeepingItemDto doTimekeeping(TimekeepingItemDto dto, UUID id);

    List<TimeSheetDto> getTimeSheetByTime(TimekeepingItemDto dto);

    Boolean convertTimeSheet();

    List<TimeSheetDto> getListTimeSheetByStaffId(UUID id);

    Page<TimeSheetDto> getPageTimeSheetByStaffId(SearchTimeSheetDto dto);

    Page<StaffDto> checkTimeKeeping(TimekeepingItemDto dto);

    Page<StaffDto> checkTimeSheetDetail(TimekeepingItemDto dto);

    TimeSheetDto timekeepingServicePlan(TimekeepingItemDto dto);

    List<TimeSheetDto> getTimeSheetByTimeAndStaffId(UUID staffId, Date workingDate);

    List<TimeSheetDto> getTimeSheetByTime(UUID staffId, Date fromDate, Date toDate);

    List<TimekeepingItemDto> getTimeKeepingByMonth(int month, int year, UUID staffId);

    List<TimekeepingSummaryDto> getListTimekeepingSummary(SearchTimeSheetDto dto);

    void handleImportExcel(InputStream is);

    TimeSheetDto getByDate(TimeSheetDto dto);

    TimeSheetDto checkTimeSheet(TimeSheetDto dto, HttpServletRequest request);

    String getClientIp(HttpServletRequest request);

    String getClientIpV2(HttpServletRequest request);

    String saveTimekeeping(TimeSheetStaffDto dto, HttpServletRequest request);

    // Lấy dữ liệu chấm công hiện thời của nhân viên
    TimeSheetStaffDto getCurrentTimekeepingData();
}
