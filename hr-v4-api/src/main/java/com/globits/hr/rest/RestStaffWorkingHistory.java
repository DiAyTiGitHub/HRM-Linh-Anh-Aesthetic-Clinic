package com.globits.hr.rest;

import com.globits.hr.dto.StaffWorkingHistoryDto;
import com.globits.hr.dto.search.SearchStaffWorkingHistoryDto;
import com.globits.hr.service.StaffWorkingHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/staff-working-history")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestStaffWorkingHistory {
    @Autowired
    StaffWorkingHistoryService service;

    @PostMapping(value = "/paging")
    public ResponseEntity<Page<StaffWorkingHistoryDto>> pagingStaffWorkingHistory(@RequestBody SearchStaffWorkingHistoryDto dto) {
        Page<StaffWorkingHistoryDto> result = service.getPage(dto);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping(value = "/{id}")
    public Boolean deleteById(@PathVariable UUID id) {
        return service.deleteStaffWorkingHistory(id);
    }

    @GetMapping(value = "/recent/{staffId}")
    public ResponseEntity<StaffWorkingHistoryDto> getRecentStaffWorkingHistory(@PathVariable UUID staffId) {
        StaffWorkingHistoryDto result = service.getRecentStaffWorkingHistory(staffId);

        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<StaffWorkingHistoryDto> getStaffWorkingHistoryById(@PathVariable UUID id) {
        StaffWorkingHistoryDto staffWorkingHistoryDto = service.getStaffWorkingHistory(id);
        if (staffWorkingHistoryDto == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(staffWorkingHistoryDto, HttpStatus.OK);
    }


    @PostMapping(value = "/save")
    public ResponseEntity<StaffWorkingHistoryDto> saveStaffWorkingHistory(@RequestBody StaffWorkingHistoryDto dto) {
        StaffWorkingHistoryDto staffWorkingHistoryDto = service.saveStaffWorkingHistory(dto);
        if (staffWorkingHistoryDto == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(staffWorkingHistoryDto, HttpStatus.OK);
    }


}
