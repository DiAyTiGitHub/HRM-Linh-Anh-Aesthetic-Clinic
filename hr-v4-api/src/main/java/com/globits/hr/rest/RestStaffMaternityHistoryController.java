package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffMaternityHistoryDto;
import com.globits.hr.dto.StaffSignatureDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchStaffSignatureDto;
import com.globits.hr.service.StaffMaternityHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/maternityHistory")
public class RestStaffMaternityHistoryController {
    @Autowired
    private StaffMaternityHistoryService staffMaternityHistoryService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/searchByPage")
    public Page<StaffMaternityHistoryDto> searchByPage(@RequestBody SearchDto dto) {
        return staffMaternityHistoryService.searchByPage(dto);
    }

    @RequestMapping(value = "/getAll/{staffId}", method = RequestMethod.GET)
    public List<StaffMaternityHistoryDto> getAll(@PathVariable("staffId") UUID id) {
        return this.staffMaternityHistoryService.getAll(id);
    }

    @RequestMapping(value = "/{maternityHistoryId}", method = RequestMethod.GET)
    public StaffMaternityHistoryDto getMaternityHistoryById(@PathVariable("maternityHistoryId") UUID id) {
        return this.staffMaternityHistoryService.getStaffMaternityHistoryById(id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public StaffMaternityHistoryDto saveStaffMaternityHistory(@RequestBody StaffMaternityHistoryDto dto) {
        return this.staffMaternityHistoryService.saveStaffMaternityHistory(dto);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public StaffMaternityHistoryDto removeStaffMaternityHistory(@PathVariable UUID id) {
        return this.staffMaternityHistoryService.removeStaffMaternityHistory(id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/deleteLists", method = RequestMethod.DELETE)
    public boolean removeLists(@RequestBody List<UUID> ids) {
        this.staffMaternityHistoryService.removeLists(ids);
        return false;
    }
}
