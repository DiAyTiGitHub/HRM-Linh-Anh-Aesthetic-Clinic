package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffRewardHistoryDto;
import com.globits.hr.service.StaffRewardHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/rewardHistory")
public class RestStaffRewardHistoryController {
    @Autowired
    private StaffRewardHistoryService staffRewardHistoryService;

    @RequestMapping(value = "/{pageIndex}/{pageSize}", method = RequestMethod.GET)
    public Page<StaffRewardHistoryDto> getPages(@PathVariable int pageIndex, @PathVariable int pageSize) {
        return this.staffRewardHistoryService.getPage(pageIndex, pageSize);
    }

    @RequestMapping(value = "/getAll/{staffId}", method = RequestMethod.GET)
    public List<StaffRewardHistoryDto> getAll(@PathVariable("staffId") UUID id) {
        return this.staffRewardHistoryService.getAll(id);
    }

    @RequestMapping(value = "/{rewardHistoryId}", method = RequestMethod.GET)
    public StaffRewardHistoryDto getEducationById(@PathVariable("rewardHistoryId") UUID id) {
        return this.staffRewardHistoryService.getStaffRewardHistoryById(id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public StaffRewardHistoryDto saveStaffRewardHistory(@RequestBody StaffRewardHistoryDto dto) {
        return this.staffRewardHistoryService.saveStaffRewardHistory(dto, null);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public StaffRewardHistoryDto saveRewardHistory(@PathVariable("id") UUID id,
                                                   @RequestBody StaffRewardHistoryDto dto) {
        return this.staffRewardHistoryService.saveStaffRewardHistory(dto, id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public StaffRewardHistoryDto removeStaffOverseasWorkHistory(@PathVariable UUID id) {
        return this.staffRewardHistoryService.removeStaffRewardHistory(id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/deleteLists", method = RequestMethod.POST)
    public boolean removeLists(@RequestBody List<UUID> ids) {
        this.staffRewardHistoryService.removeLists(ids);
        return false;
    }
}
