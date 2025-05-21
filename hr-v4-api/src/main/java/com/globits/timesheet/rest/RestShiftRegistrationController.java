package com.globits.timesheet.rest;

import com.globits.hr.dto.StaffWorkScheduleDto;
import com.globits.timesheet.dto.search.SearchShiftRegistrationDto;
import com.globits.timesheet.dto.ShiftRegistrationDto;
import com.globits.timesheet.service.ShiftRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/shift-registration")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestShiftRegistrationController {

    @Autowired
    private ShiftRegistrationService shiftRegistrationService;

    @PostMapping("/save-or-update")
    public ResponseEntity<ShiftRegistrationDto> saveShiftRegistration(@RequestBody ShiftRegistrationDto shiftRegistrationDto) {
       
        ShiftRegistrationDto result = shiftRegistrationService.saveOrUpdate(shiftRegistrationDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/mark-delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Integer> markDelete(@PathVariable UUID id) {
        int result = shiftRegistrationService.markDelete(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteShiftRegistration(@PathVariable UUID id) {
        Boolean result = shiftRegistrationService.deleteById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/search-by-page", method = RequestMethod.POST)
    public ResponseEntity<Page<ShiftRegistrationDto>> pagingShiftRegistrations(@RequestBody SearchShiftRegistrationDto searchDto) {
        Page<ShiftRegistrationDto> result = shiftRegistrationService.pagingShiftRegistrations(searchDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<ShiftRegistrationDto> getShiftRegistrationById(@PathVariable UUID id) {
        ShiftRegistrationDto result = shiftRegistrationService.getShiftRegistration(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/update-approval-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UUID>> updateApprovalStatus(@RequestBody SearchShiftRegistrationDto searchDto) {
        List<UUID> updatedPlanIds = shiftRegistrationService.updateApprovalStatus(searchDto);
        if (updatedPlanIds != null && updatedPlanIds.size() > 0)
            return new ResponseEntity<>(updatedPlanIds, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/create-staff-work-schedule")
    public ResponseEntity<StaffWorkScheduleDto> createStaffWorkSchedule(@RequestBody ShiftRegistrationDto shiftRegistrationDto) {
        StaffWorkScheduleDto response = shiftRegistrationService.createStaffWorkSchedule(shiftRegistrationDto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/create-staff-work-schedules")
    public ResponseEntity<String> createStaffWorkSchedules(@RequestBody List<ShiftRegistrationDto> listShiftRegistrationDto) {
        String response = shiftRegistrationService.createStaffWorkSchedules(listShiftRegistrationDto);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
