package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffTrainingHistoryDto;
import com.globits.hr.service.StaffTrainingHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trainingHistory")
public class RestStaffTrainingHistoryController {
    @Autowired
    StaffTrainingHistoryService staffTrainingHistoryService;

    @RequestMapping(value = "/{pageIndex}/{pageSize}", method = RequestMethod.GET)
    public Page<StaffTrainingHistoryDto> getPages(@PathVariable int pageIndex, @PathVariable int pageSize) {
        return this.staffTrainingHistoryService.getPage(pageIndex, pageSize);
    }

    @RequestMapping(value = "/getAll/{staffId}", method = RequestMethod.GET)
    public List<StaffTrainingHistoryDto> getAll(@PathVariable("staffId") UUID id) {
        return this.staffTrainingHistoryService.getAll(id);
    }

    @RequestMapping(value = "/{trainingHistoryId}", method = RequestMethod.GET)
    public StaffTrainingHistoryDto getTrainingHistoryById(@PathVariable("trainingHistoryId") UUID id) {
        return this.staffTrainingHistoryService.getStaffTrainingHistoryById(id);
    }

    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public StaffTrainingHistoryDto saveTrainingHistory(@RequestBody StaffTrainingHistoryDto dto) {
        return this.staffTrainingHistoryService.saveStaffTrainingHistory(dto, null);
    }

    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public StaffTrainingHistoryDto saveTrainingHistory(@PathVariable("id") UUID id,
            @RequestBody StaffTrainingHistoryDto dto) {
        return this.staffTrainingHistoryService.saveStaffTrainingHistory(dto, id);
    }

    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public StaffTrainingHistoryDto removeStaffTrainingHistory(@PathVariable UUID id) {
        return this.staffTrainingHistoryService.removeStaffTrainingHistory(id);
    }

    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
    @RequestMapping(value = "/deleteLists", method = RequestMethod.DELETE)
    public boolean removeLists(@RequestBody List<UUID> ids) {
        this.staffTrainingHistoryService.removeLists(ids);
        return false;
    }
}
