package com.globits.hr.rest;

import com.globits.hr.dto.StaffSalaryHistoryDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.StaffSalaryHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/staff-salary-history")
public class RestStaffSalaryHistoryController {

    @Autowired
    private StaffSalaryHistoryService staffSalaryHistoryService;

    @RequestMapping(method = RequestMethod.POST, path = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StaffSalaryHistoryDto> saveOrUpdate(@RequestBody StaffSalaryHistoryDto dto) {
        StaffSalaryHistoryDto response = staffSalaryHistoryService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<StaffSalaryHistoryDto>> searchByPage(@RequestBody SearchDto dto) {
        Page<StaffSalaryHistoryDto> page = staffSalaryHistoryService.paging(dto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(path = "/all-staff-salary-history-by-staff/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<StaffSalaryHistoryDto>> getAllByStaff(@PathVariable("id") UUID id) {
        List<StaffSalaryHistoryDto> response = staffSalaryHistoryService.getAllByStaff(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<StaffSalaryHistoryDto> getById(@PathVariable("id") UUID id) {
        StaffSalaryHistoryDto response = staffSalaryHistoryService.getById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
