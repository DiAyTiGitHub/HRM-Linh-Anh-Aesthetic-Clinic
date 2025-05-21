package com.globits.hr.rest;

import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.PositionDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchPositionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffEducationHistoryDto;
import com.globits.hr.service.StaffEducationHistoryService;

@RestController
@RequestMapping("/api/education")
public class RestStaffEducationHistoryController {
    @Autowired
    private StaffEducationHistoryService educationHistoryService;

    @RequestMapping(value = "/{pageIndex}/{pageSize}", method = RequestMethod.GET)
    public Page<StaffEducationHistoryDto> getPages(@PathVariable int pageIndex, @PathVariable int pageSize) {
        return this.educationHistoryService.getPages(pageIndex, pageSize);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/paging", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<StaffEducationHistoryDto>> paging(@RequestBody SearchDto searchDto) {
        Page<StaffEducationHistoryDto> page = educationHistoryService.pagingEducationHistory(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/getall/{staffId}", method = RequestMethod.GET)
    public List<StaffEducationHistoryDto> getAll(@PathVariable("staffId") UUID id) {
        return this.educationHistoryService.getAll(id);
    }

    @RequestMapping(value = "/{educationId}", method = RequestMethod.GET)
    public StaffEducationHistoryDto getEducationById(@PathVariable("educationId") UUID id) {
        return this.educationHistoryService.getEducationById(id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public StaffEducationHistoryDto saveEducation(@RequestBody StaffEducationHistoryDto educationDto) {
        return this.educationHistoryService.saveEducation(educationDto, null);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public StaffEducationHistoryDto saveEducation(@PathVariable("id") UUID id, @RequestBody StaffEducationHistoryDto educationDto) {
        return this.educationHistoryService.saveEducation(educationDto, id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public StaffEducationHistoryDto removeEducation(@PathVariable UUID id) {
        return this.educationHistoryService.removeEducation(id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/deleteLists", method = RequestMethod.DELETE)
    public boolean removeLists(@RequestBody List<UUID> ids) {
        this.educationHistoryService.removeLists(ids);
        return false;
    }
}
