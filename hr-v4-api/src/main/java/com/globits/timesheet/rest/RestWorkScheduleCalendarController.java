package com.globits.timesheet.rest;

import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffWorkScheduleDto;
import com.globits.keycloak.auth.utils.Constants;
import com.globits.timesheet.dto.calendar.ScheduledStaffCalendarDto;
import com.globits.timesheet.dto.search.SearchWorkScheduleCalendarDto;
import com.globits.timesheet.service.WorkScheduleCalendarService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/work-schedule-calendar")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestWorkScheduleCalendarController {
    @Autowired
    private WorkScheduleCalendarService workScheduleCalendarService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/get-working-schedule-by-filter", method = RequestMethod.POST)
    public ResponseEntity<?> getWorkingScheduleByFilter(@RequestBody SearchWorkScheduleCalendarDto searchDto) {
        Page<ScheduledStaffCalendarDto> response = workScheduleCalendarService.getWorkingScheduleByFilter(searchDto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/work-schedule-calendar-of-staff", method = RequestMethod.POST)
    public ResponseEntity<?> getWorkCalendarOfStaff(@RequestBody SearchWorkScheduleCalendarDto searchDto) {
        ScheduledStaffCalendarDto response = workScheduleCalendarService.getWorkCalendarOfStaff(searchDto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/initial-timekeeping-filter")
    public ResponseEntity<?> getInitialTimekeepingReportFilter() {
        SearchWorkScheduleCalendarDto response = workScheduleCalendarService.getInitialTimekeepingReportFilter();

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-timekeeping-report-by-filter", method = RequestMethod.POST)
    public ResponseEntity<?> getTimekeepingReportByFilter(@RequestBody SearchWorkScheduleCalendarDto searchDto) {
    	Page<ScheduledStaffCalendarDto> response = workScheduleCalendarService.getTimekeepingReportByFitler(searchDto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-excel")
    public ResponseEntity<?> handleExcel(HttpServletResponse response, @RequestBody SearchWorkScheduleCalendarDto dto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            dto.setHasSocialIns(true);
            Workbook workbook = workScheduleCalendarService.handleExcel(dto);

            setupResponseHeaders(response, generateFileName("BangChamCong"), CONTENT_TYPE, FILE_EXTENSION);

            try (ServletOutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
        }
    }
    
    private void setupResponseHeaders(HttpServletResponse response, String fileName, String contentType, String extension) {
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + extension);
    }
    
    private String generateFileName(String baseName) {
        String dateTimeString = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        return baseName + "-" + dateTimeString;
    }



}
