package com.globits.hr.rest;


import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.OrganizationBranchDto;
import com.globits.hr.dto.search.SearchOrganizationBranchDto;
import com.globits.hr.service.OrganizationBranchService;
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
@RequestMapping("/api/organization-branch")
public class RestOrganizationBranchController {
    @Autowired
    private OrganizationBranchService organizationBranchService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/saveOrganizationBranch")
    public ResponseEntity<OrganizationBranchDto> saveOrganizationBranch(@RequestBody OrganizationBranchDto dto) {
        OrganizationBranchDto response = organizationBranchService.saveOrganizationBranch(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/pagingOrganizationBranch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<OrganizationBranchDto>> pagingOrganizationBranch(@RequestBody SearchOrganizationBranchDto searchDto) {
        Page<OrganizationBranchDto> page = organizationBranchService.pagingOrganization(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{organizationBranchId}")
    public ResponseEntity<Boolean> deleteOrganizationBranch(@PathVariable("organizationBranchId") UUID organizationBranchId) {
        Boolean res = organizationBranchService.deleteOrganizationBranch(organizationBranchId);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{organizationBranchId}", method = RequestMethod.GET)
    public ResponseEntity<OrganizationBranchDto> getById(@PathVariable("organizationBranchId") UUID organizationBranchId) {
        OrganizationBranchDto result = organizationBranchService.getById(organizationBranchId);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> organizationBranchIds) {
        Boolean deleted = organizationBranchService.deleteMultipleOrganizationBranches(organizationBranchIds);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

}
