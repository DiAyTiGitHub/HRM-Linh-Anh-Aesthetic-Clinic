package com.globits.hr.rest;

import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchHrDepartmentDto;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.dto.search.SearchStaffHierarchyDto;
import com.globits.hr.service.StaffHierarchyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff-hierarchy")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestStaffHierarchyController {
    @Autowired
    private StaffHierarchyService staffHierarchyService;

    //    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/paging-upper-level-staff", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<StaffDto>> pagingUpperLevelStaff(@RequestBody SearchStaffHierarchyDto searchDto) {
        Page<StaffDto> page = staffHierarchyService.pagingUpperLevelStaff(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    //    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/paging-lower-level-staff", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<StaffDto>> pagingLowerLevelStaff(@RequestBody SearchStaffDto searchDto) {
        Page<StaffDto> page = staffHierarchyService.pagingLowerLevelStaff(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(path = "/paging-has-permission-departments", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<HRDepartmentDto>> pagingHasPermissionDepartments(@RequestBody SearchHrDepartmentDto searchDto) {
        Page<HRDepartmentDto> page = staffHierarchyService.pagingHasPermissionDepartments(searchDto);

        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }
}
