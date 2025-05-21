package com.globits.timesheet.rest;

import com.globits.timesheet.dto.ShiftChangeRequestDto;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import com.globits.timesheet.dto.search.ShiftChangeRequestSearchDto;
import com.globits.timesheet.service.ShiftChangeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shift-change-request")
public class RestShiftChangeRequestController {

    @Autowired
    private ShiftChangeRequestService shiftChangeRequestService;

    @RequestMapping(method = RequestMethod.POST, path = "/save-or-update")
    public ResponseEntity<ShiftChangeRequestDto> saveOrUpdate(@RequestBody ShiftChangeRequestDto dto) {
        ShiftChangeRequestDto response = shiftChangeRequestService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/search-by-page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ShiftChangeRequestDto>> searchByPage(@RequestBody ShiftChangeRequestSearchDto dto) {
        Page<ShiftChangeRequestDto> page = shiftChangeRequestService.searchByPage(dto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping(value = "/initial-filter")
    public ResponseEntity<?> getInitialFilter() {
        ShiftChangeRequestSearchDto response = shiftChangeRequestService.getInitialFilter();

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-by-id/{id}", method = RequestMethod.GET)
    public ResponseEntity<ShiftChangeRequestDto> getById(@PathVariable("id") UUID id) {
        ShiftChangeRequestDto response = shiftChangeRequestService.getById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id") UUID id) {
        boolean isDeleted = shiftChangeRequestService.deleteById(id);
        return isDeleted
                ? ResponseEntity.ok(true)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }

    @RequestMapping(path = "/delete-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        boolean isDeletedMultiple = shiftChangeRequestService.deleteMultiple(ids);
        return isDeletedMultiple
                ? ResponseEntity.ok(true)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }

    @RequestMapping(value = "/update-requests-approval-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UUID>> updateRequestsApprovalStatus(@RequestBody ShiftChangeRequestSearchDto searchDto) {
        List<UUID> updatedRequestIds = shiftChangeRequestService.updateApprovalStatus(searchDto);
        if (updatedRequestIds != null && !updatedRequestIds.isEmpty())
            return new ResponseEntity<>(updatedRequestIds, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
}
