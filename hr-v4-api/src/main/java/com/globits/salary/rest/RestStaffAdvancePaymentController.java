package com.globits.salary.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.salary.dto.StaffAdvancePaymentDto;
import com.globits.salary.dto.search.SearchStaffAdvancePaymentDto;
import com.globits.salary.service.StaffAdvancePaymentService;
import com.globits.timesheet.dto.search.SearchWorkScheduleCalendarDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/staff-advance-payment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestStaffAdvancePaymentController {
    @Autowired
    private StaffAdvancePaymentService staffAdvancePaymentService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<StaffAdvancePaymentDto> saveStaffAdvancePayment(@RequestBody StaffAdvancePaymentDto dto) {

        StaffAdvancePaymentDto response = staffAdvancePaymentService.saveStaffAdvancePayment(dto);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<StaffAdvancePaymentDto> getStaffAdvancePayment(@PathVariable UUID id) {
        StaffAdvancePaymentDto result = staffAdvancePaymentService.getStaffAdvancePayment(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/initial-filter")
    public ResponseEntity<?> getInitialFilter() {
        SearchStaffAdvancePaymentDto response = staffAdvancePaymentService.getInitialFilter();

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update-approval-status", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateStaffAdvancePaymentApprovalStatus(@RequestBody SearchStaffAdvancePaymentDto dto) {
        try {
            Boolean isUpdated = staffAdvancePaymentService.updateStaffAdvancePaymentApprovalStatus(dto);
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
    public ResponseEntity<Boolean> deleteStaffAdvancePayment(@PathVariable UUID id) {
        Boolean result = staffAdvancePaymentService.deleteStaffAdvancePayment(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/paging-staff-advance-payment", method = RequestMethod.POST)
    public ResponseEntity<Page<StaffAdvancePaymentDto>> searchByPage(@RequestBody SearchStaffAdvancePaymentDto searchDto) {
        Page<StaffAdvancePaymentDto> page = staffAdvancePaymentService.searchByPage(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = staffAdvancePaymentService.deleteMultiple(ids);
        System.out.println(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

}
