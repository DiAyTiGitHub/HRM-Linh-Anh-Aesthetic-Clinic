package com.globits.timesheet.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.ShiftWorkTimePeriodDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.repository.ShiftWorkTimePeriodRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.StaffService;
import com.globits.hr.service.UserExtService;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.hr.utils.ExportExcelUtil;
import com.globits.hr.utils.ImportExportExcelUtil;
import com.globits.timesheet.dto.*;
import com.globits.timesheet.dto.search.SearchTimeSheetDto;
import com.globits.timesheet.repository.TimeSheetRepository;
import com.globits.timesheet.service.TimeSheetService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/timesheet")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestTimeSheetController {
    @Autowired
    private UserExtService userExtService;
    @Autowired
    private TimeSheetService timeSheetService;
    @Autowired
    private StaffService staffService;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private TimeSheetRepository timeSheetRepository;

    @Autowired
    private ShiftWorkTimePeriodRepository shiftWorkTimePeriodRepository;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<TimeSheetDto> findTimeSheetById(@PathVariable("id") UUID id) {
        TimeSheetDto result = timeSheetService.findTimeSheetById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_ADMIN, HrConstants.ROLE_HR_MANAGEMENT})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteTimeSheet(@PathVariable("id") UUID id) {
        Boolean ret = timeSheetService.deleteTimeSheetById(id);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_ADMIN, HrConstants.ROLE_HR_MANAGEMENT})
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteTimeSheet(@RequestBody List<TimeSheetDto> list) {
        Boolean ret = timeSheetService.deleteTimeSheets(list);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN"})
    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    public ResponseEntity<Boolean> confirmTimeSheets(@RequestBody List<TimeSheetDto> listdto) {
        Boolean ret = timeSheetService.confirmTimeSheets(listdto);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @RequestMapping(value = "/search-by-page", method = RequestMethod.POST)
    public ResponseEntity<Page<TimeSheetDto>> searchByPage(@RequestBody SearchTimeSheetDto searchDto) {
        Page<TimeSheetDto> page = timeSheetService.searchByPage(searchDto);
        if (page == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<TimeSheetDto> save(@RequestBody TimeSheetDto dto) {
        TimeSheetDto result = timeSheetService.saveOrUpdate(null, dto);
        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<TimeSheetDto> save(@RequestBody TimeSheetDto dto, @PathVariable UUID id) {
        TimeSheetDto result = timeSheetService.saveOrUpdate(id, dto);
        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update-status/{id}/{workingStatusId}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateStatus(@PathVariable UUID id, @PathVariable UUID workingStatusId) {
        String result = timeSheetService.updateStatus(id, workingStatusId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/exportExcel", method = RequestMethod.POST)
    public void exportTimeSheetsToExcel(HttpServletResponse response,
                                        @RequestBody SearchTimeSheetDto searchTimeSheetDto) throws IOException {
        Page<TimeSheetDto> data = timeSheetService.searchByPage(searchTimeSheetDto);
        List<TimeSheetDto> dataList = data.getContent();
        ByteArrayResource excelFile;
        if (!dataList.isEmpty()) {
            excelFile = ExportExcelUtil.exportTimeSheetToExcelTable(dataList, true);
            InputStream ins = null;
            if (excelFile != null) {
                ins = new ByteArrayInputStream(excelFile.getByteArray());
            }
            if (ins != null) {
                org.apache.commons.io.IOUtils.copy(ins, response.getOutputStream());
            }
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.addHeader("Content-Disposition", "attachment; filename=TimeSheet.xlsx");
    }

    @RequestMapping(value = "/getListTimeSheetByProjectActivityId/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<TimeSheetDto>> getListTimeSheetByProjectActivityId(@PathVariable UUID id) {
        List<TimeSheetDto> list = timeSheetService.getListTimeSheetByProjectActivityId(id);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/time-keeping-service", method = RequestMethod.POST)
    public ResponseEntity<TimeSheetDto> timeKeepingService(@RequestBody TimekeepingItemDto dto) {
        TimeSheetDto result = timeSheetService.timekeepingService(dto, null);
        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/do-timekeeping", method = RequestMethod.POST)
    public ResponseEntity<TimekeepingItemDto> doTimekeeping(@RequestBody TimekeepingItemDto dto) {
        TimekeepingItemDto result = timeSheetService.doTimekeeping(dto, null);
        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // @RequestMapping(value = "/get-timesheet-by-time", method =
    // RequestMethod.POST)
    // public ResponseEntity<?> getTimeSheetByTime(@RequestBody TimekeepingDto dto)
    // {
    // StaffDto staffDto = userExtService.getCurrentStaff();
    // if (staffDto != null) {
    // dto.setStaffId(staffDto.getId());
    // }
    // List<TimeSheetDto> list = timeSheetService.getTimeSheetByTime(dto);
    // if (list != null && list.size() > 0) {
    // TimeSheetDto timeSheetDto = list.get(0);
    // return new ResponseEntity<>(timeSheetDto, HttpStatus.OK);
    // }
    // if (list != null && list.size() == 0) {
    // return new ResponseEntity<>(new TimeSheetDto(dto.getWorkingDate()),
    // HttpStatus.OK);
    // }
    // return new ResponseEntity<>(null, HttpStatus.OK);
    // }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/convert-timesheet-to-timesheetdetail", method = RequestMethod.POST)
    public ResponseEntity<Boolean> convertTimeSheetToTimeSheetDetail() {
        Boolean list = timeSheetService.convertTimeSheet();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/getListTimeSheetByStaffId/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<TimeSheetDto>> getListTimeSheetByStaffId(@PathVariable UUID id) {
        List<TimeSheetDto> list = timeSheetService.getListTimeSheetByStaffId(id);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-page-timesheet-by-staff-id", method = RequestMethod.POST)
    public ResponseEntity<Page<TimeSheetDto>> getlistTimeSheetBystaffId(@RequestBody SearchTimeSheetDto dto) {
        Page<TimeSheetDto> list = timeSheetService.getPageTimeSheetByStaffId(dto);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/check-time-keeping", method = RequestMethod.POST)
    public ResponseEntity<Page<StaffDto>> checkTimeKeeping(@RequestBody TimekeepingItemDto dto) {
        Page<StaffDto> list = timeSheetService.checkTimeKeeping(dto);
        return new ResponseEntity<>(list, HttpStatus.OK);

    }

    @RequestMapping(value = "/check-detail", method = RequestMethod.POST)
    public ResponseEntity<Page<StaffDto>> checkDetail(@RequestBody TimekeepingItemDto dto) {
        Page<StaffDto> list = timeSheetService.checkTimeSheetDetail(dto);
        return new ResponseEntity<>(list, HttpStatus.OK);

    }

    @RequestMapping(value = "/exportExcel-staff-not-timekeeping", method = RequestMethod.POST)
    public void exportStaffNotTimeKeepingToExcel(HttpServletResponse response,
                                                 @RequestBody TimekeepingItemDto timekeepingItemDto) throws IOException {
        Page<StaffDto> data = timeSheetService.checkTimeKeeping(timekeepingItemDto);
        List<StaffDto> dataList = data.getContent();
        ByteArrayResource excelFile;
        if (!dataList.isEmpty()) {
            excelFile = ExportExcelUtil.exportStaffToExcelTable(dataList, true);
            InputStream ins = null;
            if (excelFile != null) {
                ins = new ByteArrayInputStream(excelFile.getByteArray());
            }
            if (ins != null) {
                org.apache.commons.io.IOUtils.copy(ins, response.getOutputStream());
            }
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.addHeader("Content-Disposition", "attachment; filename=NhanVien.xlsx");
    }

    @RequestMapping(value = "/exportExcel-staff-not-timesheet", method = RequestMethod.POST)
    public void exportStaffNotTimeSheetToExcel(HttpServletResponse response,
                                               @RequestBody TimekeepingItemDto timekeepingItemDto)
            throws IOException {
        Page<StaffDto> data = timeSheetService.checkTimeSheetDetail(timekeepingItemDto);
        List<StaffDto> dataList = data.getContent();
        ByteArrayResource excelFile;
        if (!dataList.isEmpty()) {
            excelFile = ExportExcelUtil.exportStaffToExcelTable(dataList, true);
            InputStream ins = null;
            if (excelFile != null) {
                ins = new ByteArrayInputStream(excelFile.getByteArray());
            }
            if (ins != null) {
                org.apache.commons.io.IOUtils.copy(ins, response.getOutputStream());
            }
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.addHeader("Content-Disposition", "attachment; filename=NhanVien.xlsx");
    }

    @RequestMapping(value = "/time-keeping-service-plan", method = RequestMethod.POST)
    public ResponseEntity<TimeSheetDto> timeKeepingServicePlan(@RequestBody TimekeepingItemDto dto) {
        TimeSheetDto result = timeSheetService.timekeepingServicePlan(dto);
        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/export-time-sheet-shift-work-period", method = RequestMethod.POST)
    public void exportTimeSheetOfAllStaffToExcelTable(HttpServletResponse response, @RequestParam int year,
                                                      @RequestParam int month) throws IOException {
        List<UUID> idStaffList = timeSheetRepository.getAllStaffIdByWorkingDate(year, month);
        List<StaffDto> staffList = staffRepository.getAllByStaffId(idStaffList);
        List<ShiftWorkTimePeriodDto> shiftWorkTimePeriodList = shiftWorkTimePeriodRepository.getAllByStartTimeAsc();
        List<Date> dateList = DateTimeUtil.getDatesByYearMonth(year, month - 1);
        List<TimeSheetDto> timeSheetList = timeSheetRepository.getAllTimeSheetByWorkingDate(year, month);
        ByteArrayResource excelFile;
        if (!staffList.isEmpty() && !shiftWorkTimePeriodList.isEmpty() && !dateList.isEmpty()
                && !timeSheetList.isEmpty()) {
            excelFile = ExportExcelUtil.exportTimeSheetOfAllStaffToExcelTable(staffList, shiftWorkTimePeriodList,
                    dateList, timeSheetList, true);
            InputStream ins = null;
            if (excelFile != null) {
                ins = new ByteArrayInputStream(excelFile.getByteArray());
            }
            if (ins != null) {
                org.apache.commons.io.IOUtils.copy(ins, response.getOutputStream());
            }
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.addHeader("Content-Disposition", "attachment; filename=TimeSheetShiftWorkPeriod.xlsx");
    }

    // @RequestMapping(value = "/get-time-keeping-by-month", method =
    // RequestMethod.POST)
    // public ResponseEntity<?> getTimeKeepingByMonth(@RequestParam int year, int
    // month) {
    // StaffDto staff = userExtService.getCurrentStaff();
    // if(staff==null){
    // return new ResponseEntity<>("Can not find staff", HttpStatus.OK);
    // }
    // List<TimeSheetDto> timeSheetList =
    // timeSheetService.getTimeKeepingByMonth(month, year, staff.getId());
    // return new ResponseEntity<>(timeSheetList, HttpStatus.OK);

    // }

    @RequestMapping(value = "/get-time-keeping-by-month", method = RequestMethod.POST)
    public ResponseEntity<?> getTimeKeepingByMonth(@RequestBody SearchTimeSheetDto dto) {
        if (dto.getYearReport() == null || dto.getMonthReport() == null) return null;

        int year = dto.getYearReport();
        int month = dto.getMonthReport();

        UUID staffId = dto.getStaffId();
        StaffDto currentStaff = userExtService.getCurrentStaff();

        if (currentStaff == null) {
            return new ResponseEntity<>("Can not find staff", HttpStatus.OK);
        }
        if (staffId != null) {
            Staff staff = staffService.getEntityById(dto.getStaffId());
            currentStaff = new StaffDto(staff);
        } else {
            staffId = currentStaff.getId();
        }
        List<TimekeepingItemDto> timeSheetList = timeSheetService.getTimeKeepingByMonth(month, year, staffId);
        return new ResponseEntity<>(new TimekeepingDto(timeSheetList, currentStaff), HttpStatus.OK);
    }

    @ResponseBody
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/import-timekeeping", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importTimekeeping(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {

            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());

            List<TimekeepingItemDto> list = new ArrayList<>();

            ImportExportExcelUtil.importTimekeeping(bis, list);

            int size = list.size();
            for (int i = 0; i < size; i++) {
                TimekeepingItemDto itemDto = list.get(i);

                timeSheetService.doTimekeeping(itemDto, null);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/get-list-timekeeping-summary", method = RequestMethod.POST)
    public ResponseEntity<?> getListTimekeepingSummary(@RequestBody SearchTimeSheetDto searchDto) {
        List<TimekeepingSummaryDto> data = timeSheetService.getListTimekeepingSummary(searchDto);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/handle-upload-csv", method = RequestMethod.POST)
    public ResponseEntity<?> handleUploadCsv(@RequestParam("upload") MultipartFile file) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
            timeSheetService.handleImportExcel(bis);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/download-import-v1-template", method = RequestMethod.POST)
    public void downloadImportV1Template(HttpSession session, HttpServletResponse response) throws IOException {
        // Fallback to the resource file
        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("ImportTimesheetV1.xlsx");
        if (resourceStream == null) {
            throw new FileNotFoundException("Resource file 'ImportTimesheetV1.xlsx' not found.");
        }

        // Write the resource file directly to the response output stream
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.addHeader("Content-Disposition", "attachment; filename=ImportTimesheetV1.xlsx");

        org.apache.commons.io.IOUtils.copy(resourceStream, response.getOutputStream());
        response.getOutputStream().flush();
        resourceStream.close();
    }

    @RequestMapping(value = "/get-by-date", method = RequestMethod.POST)
    public ResponseEntity<TimeSheetDto> findTimeSheetById(@RequestBody TimeSheetDto dto) {
        TimeSheetDto result = timeSheetService.getByDate(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/check-time-sheet", method = RequestMethod.POST)
    public ResponseEntity<TimeSheetDto> checkTimeSheet(HttpServletRequest request, @RequestBody TimeSheetDto dto) {
        TimeSheetDto result = timeSheetService.checkTimeSheet(dto, request);
        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/get-ip")
    public ResponseEntity<String> getClientIp(HttpServletRequest request) {
        return new ResponseEntity<>(timeSheetService.getClientIp(request), HttpStatus.OK);
    }

    @RequestMapping(value = "/save-timekeeping", method = RequestMethod.POST)
    public ResponseEntity<String> saveTimekeeping(HttpServletRequest request, @RequestBody TimeSheetStaffDto dto) {
        String message = timeSheetService.saveTimekeeping(dto, request);

        if (message == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-current-timekeeping", method = RequestMethod.GET)
    public ResponseEntity<TimeSheetStaffDto> getCurrentTimekeepingData() {
        TimeSheetStaffDto response = timeSheetService.getCurrentTimekeepingData();

        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
