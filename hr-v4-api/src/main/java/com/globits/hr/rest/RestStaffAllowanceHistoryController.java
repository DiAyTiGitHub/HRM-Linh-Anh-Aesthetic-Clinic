package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffAllowanceHistoryDto;
import com.globits.hr.service.StaffAllowanceHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/allowanceHistory")
public class RestStaffAllowanceHistoryController {
    @Autowired
    StaffAllowanceHistoryService staffAllowanceHistoryService;
    @RequestMapping(value = "/{pageIndex}/{pageSize}", method = RequestMethod.GET)
    public Page<StaffAllowanceHistoryDto> getPages(@PathVariable int pageIndex, @PathVariable int pageSize) {
        return this.staffAllowanceHistoryService.getPage(pageIndex, pageSize);
    }

    @RequestMapping(value = "/getAll/{staffId}", method = RequestMethod.GET)
    public List<StaffAllowanceHistoryDto> getAll(@PathVariable("staffId") UUID id) {
        return this.staffAllowanceHistoryService.getAll(id);
    }

    @RequestMapping(value = "/{overseasWorkHistoryId}", method = RequestMethod.GET)
    public StaffAllowanceHistoryDto getEducationById(@PathVariable("overseasWorkHistoryId") UUID id) {
        return this.staffAllowanceHistoryService.getStaffAllowanceHistoryById(id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public StaffAllowanceHistoryDto saveAllowanceHistory(@RequestBody StaffAllowanceHistoryDto dto) {
        return this.staffAllowanceHistoryService.saveStaffAllowanceHistory(dto, null);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public StaffAllowanceHistoryDto saveAllowanceHistory(@PathVariable("id") UUID id, @RequestBody StaffAllowanceHistoryDto dto) {
        return this.staffAllowanceHistoryService.saveStaffAllowanceHistory(dto, id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public StaffAllowanceHistoryDto removeStaffAllowanceHistory(@PathVariable UUID id) {
        return this.staffAllowanceHistoryService.removeStaffAllowanceHistory(id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/deleteLists", method = RequestMethod.DELETE)
    public boolean removeLists(@RequestBody List<UUID> ids) {
        this.staffAllowanceHistoryService.removeLists(ids);
        return false;
    }
}
