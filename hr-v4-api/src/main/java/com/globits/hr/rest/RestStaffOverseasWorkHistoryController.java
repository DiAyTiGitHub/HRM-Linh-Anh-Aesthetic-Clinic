package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffOverseasWorkHistoryDto;
import com.globits.hr.service.StaffOverseasWorkHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/overseasWorkHistory")
public class RestStaffOverseasWorkHistoryController {
    @Autowired
    private StaffOverseasWorkHistoryService staffOverseasWorkHistoryService;

    @RequestMapping(value = "/{pageIndex}/{pageSize}", method = RequestMethod.GET)
    public Page<StaffOverseasWorkHistoryDto> getPages(@PathVariable int pageIndex, @PathVariable int pageSize) {
        return this.staffOverseasWorkHistoryService.getPage(pageIndex, pageSize);
    }

    @RequestMapping(value = "/getAll/{staffId}", method = RequestMethod.GET)
    public List<StaffOverseasWorkHistoryDto> getAll(@PathVariable("staffId") UUID id) {
        return this.staffOverseasWorkHistoryService.getAll(id);
    }

    @RequestMapping(value = "/{overseasWorkHistoryId}", method = RequestMethod.GET)
    public StaffOverseasWorkHistoryDto getEducationById(@PathVariable("overseasWorkHistoryId") UUID id) {
        return this.staffOverseasWorkHistoryService.getStaffOverseasWorkHistoryById(id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public StaffOverseasWorkHistoryDto saveStaffOverseasWorkHistory(@RequestBody StaffOverseasWorkHistoryDto dto) {
        return this.staffOverseasWorkHistoryService.saveStaffOverseasWorkHistory(dto, null);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public StaffOverseasWorkHistoryDto saveEducation(@PathVariable("id") UUID id, @RequestBody StaffOverseasWorkHistoryDto dto) {
        return this.staffOverseasWorkHistoryService.saveStaffOverseasWorkHistory(dto, id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public StaffOverseasWorkHistoryDto removeStaffOverseasWorkHistory(@PathVariable UUID id) {
        return this.staffOverseasWorkHistoryService.removeStaffOverseasWorkHistory(id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/deleteLists", method = RequestMethod.DELETE)
    public boolean removeLists(@RequestBody List<UUID> ids) {
        this.staffOverseasWorkHistoryService.removeLists(ids);
        return false;
    }
}
