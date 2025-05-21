package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.core.domain.Organization;
import com.globits.core.dto.OrganizationDto;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.OrganizationBranchDto;
import com.globits.hr.dto.OrganizationExtDto;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.service.OrganizationBranchService;
import com.globits.hr.service.OrganizationExtService;
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
@RequestMapping("/api/organization-ext")
public class RestOrganizationExtController {
    @Autowired
    private OrganizationExtService organizationExtService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/saveOrganization")
    public ResponseEntity<OrganizationExtDto> saveOrganization(@RequestBody OrganizationExtDto dto) {
        OrganizationExtDto response = organizationExtService.saveOrganization(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/pagingOrganization", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<OrganizationDto>> pagingOrganization(@RequestBody SearchStaffDto searchDto) {
        Page<OrganizationDto> page = organizationExtService.pagingOrganization(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{orgId}")
    public ResponseEntity<Boolean> deleteOrganization(@PathVariable("orgId") UUID orgId) {
        Boolean res = organizationExtService.deleteOrganization(orgId);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{orgId}", method = RequestMethod.GET)
    public ResponseEntity<OrganizationExtDto> getById(@PathVariable("orgId") UUID orgId) {
        OrganizationExtDto result = organizationExtService.getById(orgId);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultipleOrganization(@RequestBody List<UUID> orgIds) {
        Boolean deleted = organizationExtService.deleteMultipleOrganization(orgIds);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, path = "/currentOrg")
    public ResponseEntity<OrganizationExtDto> getCurrentOrganizationOfCurrentUser() {
        OrganizationExtDto response = organizationExtService.getCurrentOrganizationOfCurrentUser();
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
