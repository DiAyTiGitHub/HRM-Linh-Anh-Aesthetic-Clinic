package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.AllowanceDto;
import com.globits.hr.dto.SearchAllowanceDto;
import com.globits.hr.dto.StaffAllowanceDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.AllowanceService;
import com.globits.hr.service.StaffAllowanceService;

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
@RequestMapping("/api/staff-allowance")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestStaffAllowanceController {
    @Autowired
    StaffAllowanceService staffAllowanceService;
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/search-by-page")
    public Page<StaffAllowanceDto> searchByPage(@RequestBody SearchDto dto) {
        return staffAllowanceService.searchByPage(dto);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/save-or-update")
    public ResponseEntity<StaffAllowanceDto> saveOrUpdate(@RequestBody StaffAllowanceDto dto) {
    	StaffAllowanceDto response = staffAllowanceService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @GetMapping(value = "/{id}")
    public StaffAllowanceDto getAllowanceStaffAllowance(@PathVariable String id) {
        return staffAllowanceService.getStaffAllowanceById(UUID.fromString(id));
    }
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable String id) {
    	staffAllowanceService.deleteStaffAllowance(UUID.fromString(id));
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @GetMapping(value = "/staff-allowance-of-staff/{staffId}")
    public ResponseEntity<List<StaffAllowanceDto>> getListStaffAllowance(@PathVariable String staffId) {
    	List<StaffAllowanceDto> result = staffAllowanceService.getStaffAllowanceByStaffId(UUID.fromString(staffId));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    
}
