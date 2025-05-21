package com.globits.timesheet.rest;

import java.util.UUID;

import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchProjectDto;
import com.globits.hr.service.UserExtService;
import com.globits.timesheet.dto.ProjectDto;
import com.globits.timesheet.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.search.SearchDto;

@RestController
@RequestMapping("/api/project")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestProjectController {
    @Autowired
    ProjectService projectService;

    @Autowired
    private UserExtService userExtService;

    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ProjectDto> save(@RequestBody ProjectDto dto) {
        ProjectDto result = projectService.saveOrUpdate(null, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER })
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ProjectDto> save(@RequestBody ProjectDto dto, @PathVariable UUID id) {
        ProjectDto result = projectService.saveOrUpdate(id, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<ProjectDto> getProject(@PathVariable UUID id) {
        ProjectDto result = projectService.getProject(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> delete(@PathVariable UUID id) {
        Boolean result = projectService.delete(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/search-by-page", method = RequestMethod.POST)
    public ResponseEntity<?> searchByPage(@RequestBody SearchProjectDto searchDto) {
        StaffDto currentStaff = userExtService.getCurrentStaff();
        if (currentStaff == null) {
            return new ResponseEntity<>("Can not find staff", HttpStatus.BAD_REQUEST);
        }
        if (searchDto.getStaffId() == null) {
            searchDto.setStaffId(currentStaff.getId());
        }
        Page<ProjectDto> page = projectService.searchByPage(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/check-code", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
            @RequestParam("code") String code) {
        Boolean result = projectService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    // Check name
    @RequestMapping(value = "/check-name", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkName(@RequestParam(value = "id", required = false) UUID id,
            @RequestParam("name") String name) {
        Boolean result = projectService.checkName(id, name);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

}
