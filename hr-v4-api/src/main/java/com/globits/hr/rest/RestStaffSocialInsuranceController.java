package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchStaffSocialInsuranceDto;
import com.globits.hr.dto.staff.StaffSocialInsuranceDto;
import com.globits.hr.service.StaffSocialInsuranceService;
import com.globits.salary.dto.search.SearchSalaryResultStaffDto;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/staff-social-insurance")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestStaffSocialInsuranceController {
    @Autowired
    private StaffSocialInsuranceService staffSocialInsuranceService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<StaffSocialInsuranceDto> saveStaffSocialInsurance(@RequestBody StaffSocialInsuranceDto dto) {

        StaffSocialInsuranceDto response = staffSocialInsuranceService.saveStaffSocialInsurance(dto);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<StaffSocialInsuranceDto> getStaffSocialInsurance(@PathVariable UUID id) {
        StaffSocialInsuranceDto result = staffSocialInsuranceService.getStaffSocialInsurance(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update-paid-status", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateStaffSocialInsurancePaidStatus(@RequestBody SearchStaffSocialInsuranceDto dto) {
        try {
            Boolean isUpdated = staffSocialInsuranceService.updateStaffSocialInsurancePaidStatus(dto);
            if (isUpdated != null && isUpdated.equals(true)) {
                return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteStaffSocialInsurance(@PathVariable UUID id) {
        Boolean result = staffSocialInsuranceService.deleteStaffSocialInsurance(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export")
    public ResponseEntity<?> exportStaffSocialInsurance(HttpServletResponse response, @RequestBody SearchStaffSocialInsuranceDto dto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            Workbook workbook = staffSocialInsuranceService.exportStaffSocialInsurance(dto);

            setupResponseHeaders(response, generateFileName("DuLieuBHXH"), CONTENT_TYPE, FILE_EXTENSION);
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

    @GetMapping(value = "/initial-filter")
    public ResponseEntity<?> getInitialFilter() {
        SearchStaffSocialInsuranceDto response = staffSocialInsuranceService.getInitialFilter();

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/paging-staff-social-insurance", method = RequestMethod.POST)
    public ResponseEntity<Page<StaffSocialInsuranceDto>> searchByPage(@RequestBody SearchStaffSocialInsuranceDto searchDto) {
        Page<StaffSocialInsuranceDto> page = staffSocialInsuranceService.searchByPage(searchDto);

        if (page == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    //    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = staffSocialInsuranceService.deleteMultiple(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value = "/generate-insurance-tickets", method = RequestMethod.POST)
    public ResponseEntity<List<UUID>> generateSocialInsuranceTicketsForStaffsBySalaryPeriod(@RequestBody SearchStaffSocialInsuranceDto dto) {
        List<UUID> response = staffSocialInsuranceService.generateSocialInsuranceTicketsForStaffsBySalaryPeriod(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/generate-single-ticket", method = RequestMethod.POST)
    public ResponseEntity<StaffSocialInsuranceDto> generateSingleSocialInsuranceTicket(@RequestBody SearchStaffSocialInsuranceDto dto) {
        StaffSocialInsuranceDto response = staffSocialInsuranceService.generateSingleSocialInsuranceTicket(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
