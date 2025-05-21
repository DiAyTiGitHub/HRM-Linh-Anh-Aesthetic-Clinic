package com.globits.timesheet.rest;

import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchLeaveTypeDto;
import com.globits.timesheet.dto.LeaveTypeDto;
import com.globits.timesheet.service.LeaveTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leave-type")
public class RestLeaveTypeController {

    @Autowired
    private LeaveTypeService leaveTypeService;

    @RequestMapping(method = RequestMethod.POST, path = "/save-or-update")
    public ResponseEntity<LeaveTypeDto> saveOrUpdate(@RequestBody LeaveTypeDto dto) {
        boolean isValidCode = leaveTypeService.isValidCode(dto);
        if (!isValidCode) {
            return new ResponseEntity<>(dto, HttpStatus.CONFLICT);
        }
        LeaveTypeDto response = leaveTypeService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/find-by-code/{code}", method = RequestMethod.GET)
    public ResponseEntity<LeaveTypeDto> findOneByCode(@PathVariable("code") String code) {
        LeaveTypeDto response = leaveTypeService.findOneByCode(code);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<LeaveTypeDto>> searchByPage(@RequestBody SearchLeaveTypeDto dto) {
        Page<LeaveTypeDto> page = leaveTypeService.searchByPage(dto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<LeaveTypeDto> getById(@PathVariable("id") UUID id) {
        LeaveTypeDto response = leaveTypeService.getById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-list", method = RequestMethod.GET)
    public ResponseEntity<List<LeaveTypeDto>> getListLeaveType() {
        List<LeaveTypeDto> response = leaveTypeService.getListLeaveTypeDto();
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id") UUID id) {
        Boolean response = leaveTypeService.deleteById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/deleteMultiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> deleteMultiple(@RequestBody List<UUID> ids) {
        Integer response = leaveTypeService.deleteMultiple(ids);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
