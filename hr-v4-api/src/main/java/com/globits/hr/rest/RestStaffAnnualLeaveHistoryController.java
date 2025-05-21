package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffAnnualLeaveHistoryDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchPersonCertificateDto;
import com.globits.hr.dto.search.SearchStaffAnnualLeaveHistoryDto;
import com.globits.hr.dto.staff.StaffInsuranceHistoryDto;
import com.globits.hr.service.StaffAnnualLeaveHistoryService;
import com.globits.hr.service.StaffInsuranceHistoryService;
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
@RequestMapping("/api/staff-annual-leave-history")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestStaffAnnualLeaveHistoryController {
    @Autowired
    private StaffAnnualLeaveHistoryService staffAnnualLeaveHistoryService;

    @GetMapping(value = "/initial-filter")
    public ResponseEntity<SearchStaffAnnualLeaveHistoryDto> getInitialFilter() {
        SearchStaffAnnualLeaveHistoryDto response = staffAnnualLeaveHistoryService.getInitialFilter();

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/save-or-update")
    public ResponseEntity<StaffAnnualLeaveHistoryDto> saveOrUpdate(@RequestBody StaffAnnualLeaveHistoryDto dto) {
        StaffAnnualLeaveHistoryDto response = staffAnnualLeaveHistoryService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/search-by-page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<StaffAnnualLeaveHistoryDto>> searchByPage(@RequestBody SearchStaffAnnualLeaveHistoryDto searchDto) {
        Page<StaffAnnualLeaveHistoryDto> page = staffAnnualLeaveHistoryService.searchByPage(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> deleteById(@PathVariable("id") UUID staffLeaveId) {
        Boolean res = staffAnnualLeaveHistoryService.deleteById(staffLeaveId);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<StaffAnnualLeaveHistoryDto> getById(@PathVariable("id") UUID id) {
        StaffAnnualLeaveHistoryDto result = staffAnnualLeaveHistoryService.getById(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = staffAnnualLeaveHistoryService.deleteMultiple(ids);

        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }
}
