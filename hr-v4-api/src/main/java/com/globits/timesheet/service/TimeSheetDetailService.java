package com.globits.timesheet.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.search.SearchStaffDto;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.globits.core.service.GenericService;
import com.globits.hr.dto.StaffDto;
import com.globits.timesheet.domain.TimeSheetDetail;
import com.globits.timesheet.dto.TimeSheetCalendarItemDto;
import com.globits.timesheet.dto.TimeSheetDetailDto;
import com.globits.timesheet.dto.TimeSheetStaffDto;
import com.globits.timesheet.dto.api.SearchTimeSheetApiDto;
import com.globits.timesheet.dto.api.TimeSheetResponseDto;
import com.globits.timesheet.dto.importExcel.ImportTimesheetDetailDto;
import com.globits.timesheet.dto.search.SearchTimeSheetDto;

import jakarta.servlet.http.HttpServletRequest;

public interface TimeSheetDetailService extends GenericService<TimeSheetDetail, UUID> {
    TimeSheetDetailDto saveTimeSheetDetail(TimeSheetDetailDto dto, UUID id);

    Page<TimeSheetDetailDto> getPage(int pageSize, int pageIndex);

    Boolean deleteTimeSheetDetails(List<TimeSheetDetailDto> list);

    TimeSheetDetailDto findTimeSheetDetailById(UUID id);

    List<TimeSheetDetailDto> getTimeSheetDetailByTimeSheetId(UUID id);

    Page<TimeSheetDetailDto> searchByPage(SearchTimeSheetDto dto);

    List<TimeSheetDetailDto> getListTimeSheetDetailByProjectActivityId(UUID id);

    List<TimeSheetDetailDto> getListTimeSheetDetailByProjectId(UUID id);

    List<TimeSheetDetailDto> getListTimesheetByShift(UUID shiftId, UUID staffId);

    List<TimeSheetDetailDto> getListTimeSheetDetailByTask(UUID staffId, UUID shiftId, UUID taskId);

    List<TimeSheetDetailDto> getListTimeSheetDetailBySubtaskItem(UUID taskId);

    String updateStatus(UUID id, UUID workingStatusId);

    Page<StaffDto> findPageByName(String textSearch, int pageIndex, int pageSize);

    Boolean deleteTimeSheetDetailById(UUID id);

    public boolean updateTimesheetDetail();

    List<TimeSheetDetailDto> getListTimeSheetDetailByTaskId(UUID id);

    List<TimeSheetDetail> getListTimeSheetDetailByTaskIdNew(UUID id);

    List<TimeSheetCalendarItemDto> getListTimesheetDetail(SearchTimeSheetDto dto);

    List<TimeSheetDetailDto> getListTimeSheetDetailByTaskIdAndStaffId(UUID taskId, UUID staffId);

    List<TimeSheetCalendarItemDto> getListTimesheetDetailOfAllStaff(SearchTimeSheetDto dto);

//    List<TimeSheetDetailDto> getListTimeSheetDetailByTaskIdAndStaffIdAndWorkingDate(UUID taskId, UUID staffId, Date workingDate);

    List<TimeSheetDetailDto> getListTimeSheetDetailByTaskIdAndStaffIdAndTimeSheetId(UUID taskId, UUID staffId,
                                                                                    UUID timeSheetId);

//    List<TimeSheetDetailDto> autoGenerateTimeSheetDetailInRangeTime(SearchTimeSheetDto searchTimeSheetDto);

//    public List<TimeSheetDetailDto> autogenerateTimesheetDetailV2(UUID taskId);

//    List<TimeSheetDetailDto> autogenerateTimesheetDetailByTask(UUID taskId);

//    List<TimeSheetDetailDto> autogenerateTimesheetDetailBySubTaskItem(UUID subTaskItemId);

    public List<TimeSheetDetailDto> autoGenerateTimeSheetDetailInRangeTimeV2(SearchTimeSheetDto searchTimeSheetDto);

    TimeSheetDetailDto saveOrUpdate(TimeSheetDetailDto dto, HttpServletRequest request);

    // Kiểm tra xem lần chấm công mới có trùng lặp với các lần chấm công hiện có của
    // ca làm việc hay không
    boolean isValidNewTimesheetDetail(TimeSheetDetailDto dto);

    Boolean deleteMultiple(List<UUID> listId);

    List<TimeSheetStaffDto> importFromInputStream(InputStream is) throws IOException;

    // Xuất dữ liệu chấm công với mẫu của hệ thống
    Workbook exportDataWithSystemTemplate(SearchTimeSheetDto dto);

    // Nhập dữ liệu chấm công với mẫu của hệ thống
    List<ImportTimesheetDetailDto> importDataWithSystemTemplate(InputStream inputStream,
                                                                List<ImportTimesheetDetailDto> importResults);

    ByteArrayOutputStream exportImportResultTimeSheet(MultipartFile file) throws IOException;

    List<TimeSheetDetailDto> readImportExportTimesheetDetailSystem(InputStream is) throws IOException;

    ByteArrayOutputStream exportImportResultTimeSheetDetailSystem(MultipartFile file) throws IOException;

    List<TimeSheetDetailDto> getListByApiTimeSheet(TimeSheetResponseDto result, String fromDate, String toDate, Boolean isOneTimeLock);

    List<TimeSheetDetailDto> convertTimeSheetDetailByApiTimeSheet(SearchTimeSheetApiDto dto);

    SearchTimeSheetDto getInitialFilter();

    Workbook exportExcelLATimekeepingData(SearchTimeSheetApiDto dto);
}
