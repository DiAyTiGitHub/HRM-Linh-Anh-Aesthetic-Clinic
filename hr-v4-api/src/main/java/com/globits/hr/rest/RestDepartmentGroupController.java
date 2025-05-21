package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.DepartmentGroupDto;
import com.globits.hr.dto.DepartmentTypeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.DepartmentGroupService;
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
@RequestMapping("/api/department-group")
@CrossOrigin(value = "*")
public class RestDepartmentGroupController {
    @Autowired
    DepartmentGroupService departmentGroupService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/saveDepartmentGroup")
    public ResponseEntity<DepartmentGroupDto> saveDepartmentGroup(@RequestBody DepartmentGroupDto dto) {
        DepartmentGroupDto response = departmentGroupService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/pagingDepartmentGroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<DepartmentGroupDto>> pagingDepartmentGroup(@RequestBody SearchDto searchDto) {
        Page<DepartmentGroupDto> page = departmentGroupService.pageBySearch(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{DepartmentGroupId}")
    public ResponseEntity<Boolean> deleteDepartmentGroup(@PathVariable("DepartmentGroupId") UUID DepartmentGroupId) {
        Boolean res = departmentGroupService.deleteDepartmentGroup(DepartmentGroupId);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{DepartmentGroupId}", method = RequestMethod.GET)
    public ResponseEntity<DepartmentGroupDto> getById(@PathVariable("DepartmentGroupId") UUID DepartmentGroupId) {
        DepartmentGroupDto result = departmentGroupService.getById(DepartmentGroupId);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> DepartmentGroupIds) {
        Boolean deleted = departmentGroupService.deleteMultiple(DepartmentGroupIds);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }
}
