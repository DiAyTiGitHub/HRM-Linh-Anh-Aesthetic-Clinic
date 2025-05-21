package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.service.CalculateStaffWorkTimeService;
import com.globits.hr.service.CalculateStaffWorkTimeServiceV2;
import com.globits.hr.service.StaffWorkScheduleService;
import com.globits.hr.utils.ExportExcelUtil;
import com.globits.hr.utils.ImportExportExcelUtil;
import com.globits.salary.dto.search.SearchStaffAdvancePaymentDto;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import com.globits.timesheet.dto.search.SearchWorkScheduleCalendarDto;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.BadRequestException;

@RestController
@RequestMapping("/api/staff-work-schedule")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestStaffWorkScheduleController {
    @Autowired
    private StaffWorkScheduleService staffWorkScheduleService;

    @Autowired
    private CalculateStaffWorkTimeService calculateStaffWorkTimeService;

    @Autowired
    private CalculateStaffWorkTimeServiceV2 calculateStaffWorkTimeServiceV2;


    @PostMapping("/re-statistic-schedules")
    public ResponseEntity<List<UUID>> reStatisticSchedulesInRangeTime(@RequestBody SearchStaffWorkScheduleDto dto) {
        List<UUID> response = calculateStaffWorkTimeServiceV2.reStatisticSchedulesInRangeTime(dto);

        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/search-by-page")
    public ResponseEntity<Page<StaffWorkScheduleDto>> searchByPage(@RequestBody SearchStaffWorkScheduleDto dto) {
        Page<StaffWorkScheduleDto> pageShiftWork = staffWorkScheduleService.searchByPage(dto);
        return new ResponseEntity<>(pageShiftWork, HttpStatus.OK);
    }

    @GetMapping(value = "/initial-filter")
    public ResponseEntity<?> getInitialFilter() {
        SearchStaffWorkScheduleDto response = staffWorkScheduleService.getInitialFilter();

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Danh sách Lịch làm việc có thể được xác nhận OT
    @PostMapping("/paging-work-schedule-result")
    public ResponseEntity<Page<StaffWorkScheduleDto>> pagingOverTimeSchedules(@RequestBody SearchStaffWorkScheduleDto dto) {
        Page<StaffWorkScheduleDto> workScheduleResults = staffWorkScheduleService.pagingOverTimeSchedules(dto);
        if (workScheduleResults == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(workScheduleResults, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/assign-shift-for-multiple-staffs")
    public ResponseEntity<?> assignShiftForMultipleStaffs(@RequestBody StaffWorkScheduleListDto dto) {
        // 
        try {
            StaffWorkScheduleListDto result = staffWorkScheduleService.assignShiftForMultipleStaffs(dto);
            return ResponseEntity.ok(result);
        } catch (RuntimeException ex) {
            // Các lỗi throw RuntimeException như new RuntimeException("...")
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            // Lỗi hệ thống không xác định
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Đã xảy ra lỗi không xác định."));
        }
    }


    // Tự động lấy thông tin phân ca làm việc theo người dùng hiện tại
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.GET, value = "/initial-shift-assignment-form")
    public ResponseEntity<StaffWorkScheduleListDto> getInitialShiftAssignmentForm() {
        StaffWorkScheduleListDto response = staffWorkScheduleService.getInitialShiftAssignmentForm();

        if (response == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/save-one", method = RequestMethod.POST)
    public ResponseEntity<StaffWorkScheduleDto> saveOrUpdate(@RequestBody StaffWorkScheduleDto dto) {
        StaffWorkScheduleDto result = staffWorkScheduleService.saveOrUpdate(dto);
        if(result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/save-multiple", method = RequestMethod.POST)
    public ResponseEntity<Integer> saveMultiple(@RequestBody StaffWorkScheduleDto dto) {
        int result = staffWorkScheduleService.saveMultiple(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER})
    @RequestMapping(value = "/import-excel-staff-work-schedule", method = RequestMethod.POST)
    public ResponseEntity<?> importStaffWorkScheduleFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            long startTime = System.currentTimeMillis();

            // Đọc dữ liệu từ file Excel
            List<StaffWorkScheduleDto> list = ImportExportExcelUtil.readAllStaffWorkScheduleFile(bis);
            if (list.size() == 1 && list.get(0).getErrorMessage() != null && !list.get(0).getErrorMessage().isEmpty()) {
                return new ResponseEntity<>(list.get(0).getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
            if (list.isEmpty()) {
                return new ResponseEntity<>("Không có dữ liệu trong tệp được tải lên", HttpStatus.BAD_REQUEST);
            }
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Thời gian đọc file và đẩy vào list (ms): " + duration);

            startTime = System.currentTimeMillis();

            Integer result = staffWorkScheduleService.saveListImportExcel(list);
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            System.out.println("Thời gian ghi file và đẩy vào db (ms): " + duration);

            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-excel-template")
    public void exportStaffWorkScheduleFromInputStream(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=StaffWorkScheduleTemplate.xlsx");

        try (InputStream inputStream = new ClassPathResource("StaffWorkScheduleTemplate.xlsx").getInputStream()) {
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<StaffWorkScheduleDto> getById(@PathVariable("id") UUID id) {
        StaffWorkScheduleDto result = staffWorkScheduleService.getById(id);
        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/recalculate/{id}", method = RequestMethod.GET)
    public ResponseEntity<StaffWorkScheduleDto> recalculateStaffWorkTime(@PathVariable("id") UUID id) {
        // StaffWorkScheduleDto result = calculateStaffWorkTimeService.calculateStaffWorkTime(id);
        StaffWorkScheduleDto result = calculateStaffWorkTimeServiceV2.calculateStaffWorkTimeAndSave(id);
        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteStaffWorkSchedule(@PathVariable UUID id) {
        Boolean result = staffWorkScheduleService.deleteStaffWorkSchedule(id);
        if (result) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = staffWorkScheduleService.deleteMultiple(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value = "/export-actual-time-sheet", method = RequestMethod.POST)
    public void exportActualTimesheet(HttpServletResponse response, @RequestBody SearchStaffWorkScheduleDto dto) throws
            IOException {

        ByteArrayResource excelFile = this.staffWorkScheduleService.exportActualTimesheet(dto);

        if (excelFile == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Hoặc SC_NOT_FOUND (404)
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Không có dữ liệu để xuất excel.\"}");
            return;
        }

        // Cấu hình response header
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.addHeader("Content-Disposition", "attachment; filename=BANG_CHAM_CONG_THUC_TE.xlsx");

        try (InputStream ins = new ByteArrayInputStream(excelFile.getByteArray())) {
            org.apache.commons.io.IOUtils.copy(ins, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Đã xảy ra lỗi khi xuất file excel.\"}");
        } finally {
            response.flushBuffer();
        }

    }


    @RequestMapping(value = "/export-tax-time-sheet", method = RequestMethod.POST)
    public void exportTaxTimesheet(HttpServletResponse response, @RequestBody SearchStaffDto searchDto) throws
            IOException {
//        searchDto.setIsExportExcel(true);
//        Page<StaffDto> page = this.staffService.searchByPage(searchDto);
//        List<StaffDto> datas = page.getContent();
//
//        ByteArrayResource excelFile;
//        if (!datas.isEmpty()) {
//
//            // Cấu hình response header
//            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//            response.addHeader("Content-Disposition", "attachment; filename=BAO_CAO_TINH_HINH_SU_DUNG_LAO_DONG.xlsx");
//
//            InputStream inputStream = new ClassPathResource("Excel/BAO_CAO_TINH_HINH_SU_DUNG_LAO_DONG.xlsx").getInputStream();
//            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
//
//            excelFile = ExportExcelUtil.handleExcelReportOnLaborUseSituation(datas, workbook);
//            InputStream ins = null;
//            if (excelFile != null) {
//                ins = new ByteArrayInputStream(excelFile.getByteArray());
//            }
//            if (ins != null) {
//                org.apache.commons.io.IOUtils.copy(ins, response.getOutputStream());
//            }
//        }
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update-schedule-ot-hours", method = RequestMethod.POST)
    public ResponseEntity<StaffWorkScheduleDto> updateScheduleOTHours(@RequestBody StaffWorkScheduleDto dto) {
        StaffWorkScheduleDto response = staffWorkScheduleService.updateScheduleOTHours(dto);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/save-schedule-statistic", method = RequestMethod.POST)
    public ResponseEntity<StaffWorkScheduleDto> saveScheduleStatistic(@RequestBody StaffWorkScheduleDto dto) {
        StaffWorkScheduleDto response = calculateStaffWorkTimeServiceV2.saveScheduleStatistic(dto);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "/get-schedules-in-day-of-staff", method = RequestMethod.POST)
    public ResponseEntity<?> getSchedulesInDayOfStaff(@RequestBody SearchStaffWorkScheduleDto searchDto) {
        List<StaffWorkScheduleDto> response = staffWorkScheduleService.getSchedulesInDayOfStaff(searchDto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/lock-schedules", method = RequestMethod.POST)
    public ResponseEntity<?> lockSchedules(@RequestBody List<UUID> ids) {
        List<UUID> response = staffWorkScheduleService.lockSchedules(ids);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/approve-leave-request/{leaveRequestId}/{status}")
    public ApiResponse<List<StaffWorkScheduleDto>> generateAndMarkSchedulesFromApprovedRequest(@PathVariable UUID leaveRequestId, @PathVariable Integer status) {
        return staffWorkScheduleService.generateAndMarkSchedulesFromApprovedRequest(leaveRequestId, status);
    }

    @RequestMapping(value = "/summary-of-staff-work-schedule", method = RequestMethod.POST)
    public TotalStaffWorkScheduleDto getStaffWorkScheduleSummary(@RequestBody SearchStaffWorkScheduleDto searchDto) {
        return staffWorkScheduleService.getStaffWorkScheduleSummary(searchDto);
    }
    
    @PostMapping("/search-staff-work-schedules-for-crm")
    public ResponseEntity<List<StaffWorkScheduleDto>> searchStaffWorkSchedulesForCRM(@RequestBody SearchStaffWorkScheduleDto dto) {
    	List<StaffWorkScheduleDto> pageShiftWork = staffWorkScheduleService.searchStaffWorkSchedulesForCRM(dto);
        return new ResponseEntity<>(pageShiftWork, HttpStatus.OK);
    }

}
