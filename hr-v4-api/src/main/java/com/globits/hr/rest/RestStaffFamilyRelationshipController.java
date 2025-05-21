package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffFamilyRelationshipDto;
import com.globits.hr.service.StaffFamilyRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/relationship")
public class RestStaffFamilyRelationshipController {

    @Autowired
    private StaffFamilyRelationshipService familyRelationshipService;

    @RequestMapping(value = "/{pageIndex}/{pageSize}", method = RequestMethod.GET)
    public Page<StaffFamilyRelationshipDto> getPages(@PathVariable int pageIndex, @PathVariable int pageSize) {
        return this.familyRelationshipService.getPages(pageIndex, pageSize);
    }

    @RequestMapping(value = "/getall/{staffId}", method = RequestMethod.GET)
    public List<StaffFamilyRelationshipDto> getAll(@PathVariable("staffId") UUID id) {
        return this.familyRelationshipService.getAll(id);
    }

    @RequestMapping(value = "/{familyId}", method = RequestMethod.GET)
    public StaffFamilyRelationshipDto getFamilyById(@PathVariable("familyId") UUID id) {
        return this.familyRelationshipService.getFamilyById(id);

    }

    //create
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public StaffFamilyRelationshipDto saveFamily(@RequestBody StaffFamilyRelationshipDto familyDto) {
        return this.familyRelationshipService.saveFamily(familyDto, null);
    }

    //update
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public StaffFamilyRelationshipDto saveFamily(@PathVariable("id") UUID id, @RequestBody StaffFamilyRelationshipDto familyDto) {
        return this.familyRelationshipService.saveFamily(familyDto, id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public StaffFamilyRelationshipDto removeFamily(@PathVariable UUID id) {
        return this.familyRelationshipService.removeFamily(id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/deleteLists", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean removeLists(@RequestBody List<UUID> ids) {
        return this.familyRelationshipService.removeLists(ids);
    }
}
