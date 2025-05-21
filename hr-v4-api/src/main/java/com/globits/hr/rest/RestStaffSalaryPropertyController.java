package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffSalaryPropertyDto;
import com.globits.hr.service.StaffSalaryPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/staffsalaryproperty")
public class RestStaffSalaryPropertyController {
    @Autowired
    private StaffSalaryPropertyService staffSalaryPropertyService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{pageIndex}/{pageSize}", method = RequestMethod.GET)
    public ResponseEntity<Page<StaffSalaryPropertyDto>> getPage(@PathVariable int pageIndex,
                                                                @PathVariable int pageSize) {
        Page<StaffSalaryPropertyDto> results = staffSalaryPropertyService.getPage(pageSize, pageIndex);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<StaffSalaryPropertyDto> saveStaffSalaryProperty(@RequestBody StaffSalaryPropertyDto dto) {
        StaffSalaryPropertyDto result = staffSalaryPropertyService.saveStaffSalaryProperty(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<StaffSalaryPropertyDto> getStaffSalaryProperty(@PathVariable UUID id) {
        StaffSalaryPropertyDto result = staffSalaryPropertyService.getStaffSalaryProperty(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Integer> saveStaffSalaryProperty(@RequestBody List<StaffSalaryPropertyDto> dtos) {
        int result = staffSalaryPropertyService.deleteStaffSalaryPropertyList(dtos);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> saveSalaryItem(@PathVariable UUID id) {
        Boolean result = staffSalaryPropertyService.deleteStaffSalaryProperty(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
