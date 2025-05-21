package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffWorkScheduleDto;
import com.globits.hr.dto.search.AbsenceRequestSearchDto;
import com.globits.hr.service.ConfirmStaffWorkScheduleService;
import com.globits.timesheet.dto.OvertimeRequestDto;
import com.globits.timesheet.dto.search.SearchOvertimeRequestDto;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import com.globits.timesheet.service.OvertimeRequestService;
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
@RequestMapping("/api/confirm-staff-work-schedule")
public class RestConfirmStaffWorkScheduleController {

    @Autowired
    private ConfirmStaffWorkScheduleService service;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/search-by-page", method = RequestMethod.POST)
    public ResponseEntity<Page<StaffWorkScheduleDto>> pagingConfirmStaffWorkSchedule(@RequestBody SearchOvertimeRequestDto dto) {
        Page<StaffWorkScheduleDto> result = service.pagingConfirmStaffWorkSchedule(dto);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/initial-filter")
    public ResponseEntity<?> getInitialFilter() {
        SearchOvertimeRequestDto response = service.getInitialFilter();

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
//
//    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
//    @RequestMapping(value = "/get-by-id/{id}", method = RequestMethod.GET)
//    public ResponseEntity<OvertimeRequestDto> getAbsenceRequestById(@PathVariable UUID id) {
//    	OvertimeRequestDto result = service.getById(id);
//    	return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
//    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update-requests-approval-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UUID>> updateRequestsApprovalStatus(@RequestBody AbsenceRequestSearchDto searchDto) {
        List<UUID> updatedRequestIds = service.updateApprovalStatus(searchDto);
        if (updatedRequestIds != null && updatedRequestIds.size() > 0)
            return new ResponseEntity<>(updatedRequestIds, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


}
