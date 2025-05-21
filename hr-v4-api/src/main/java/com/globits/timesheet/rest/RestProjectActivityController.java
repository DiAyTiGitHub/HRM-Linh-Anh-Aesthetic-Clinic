package com.globits.timesheet.rest;

import java.util.List;
import java.util.UUID;


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
import com.globits.hr.dto.search.ProjectActivitySearchDto;
import com.globits.timesheet.dto.ProjectActivityDto;
import com.globits.timesheet.service.ProjectActivityService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/project-activity")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestProjectActivityController {
    @Autowired
    ProjectActivityService projectActivityService;

    // Save
    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ProjectActivityDto> save(@RequestBody ProjectActivityDto dto) {
        ProjectActivityDto result = projectActivityService.saveOrUpdate(null, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getCreate(@RequestParam String projectId, String parentId) {
        String result = projectActivityService.getCreate(projectId, parentId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Update
    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ProjectActivityDto> update(@RequestBody ProjectActivityDto dto, @PathVariable UUID id) {
        ProjectActivityDto result = projectActivityService.saveOrUpdate(id, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Find by id
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<ProjectActivityDto> getProjectActivity(@PathVariable UUID id) {
        ProjectActivityDto result = projectActivityService.getProjectActivity(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Delete
    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> delete(@PathVariable UUID id) {
        Boolean result = projectActivityService.voidProjectActivity(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Get page
    @RequestMapping(value = "/search-by-page", method = RequestMethod.POST)
    public ResponseEntity<Page<ProjectActivityDto>> searchByPage(@RequestBody ProjectActivitySearchDto searchDto) {
        Page<ProjectActivityDto> page = projectActivityService.searchByPage(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/search-to-list", method = RequestMethod.POST)
    public ResponseEntity<List<ProjectActivityDto>> searchToList(@RequestBody ProjectActivitySearchDto searchDto) {
        searchDto.setDisablePaging(true);
        Page<ProjectActivityDto> page = projectActivityService.searchByPage(searchDto);
        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }

    // Check code
    @RequestMapping(value = "/check-code", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
            @RequestParam("code") String code) {
        Boolean result = projectActivityService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    // Check name
    @RequestMapping(value = "/check-name", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkName(@RequestParam(value = "id", required = false) UUID id,
            @RequestParam("name") String name) {
        Boolean result = projectActivityService.checkName(id, name);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    // Get list by id
    @RequestMapping(value = "/getListByProjectId", method = RequestMethod.POST)
    public ResponseEntity<List<ProjectActivityDto>> getListByProjectId(@RequestBody ProjectActivitySearchDto dto) {
        List<ProjectActivityDto> list = projectActivityService.getList(dto);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // Export
    @Secured({ HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN })
    @RequestMapping(value = "/export-project/{id}", method = RequestMethod.GET)
    public ResponseEntity<Boolean> export(@PathVariable UUID id, HttpServletResponse response) {
        Boolean result = projectActivityService.exportProject(id, response);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
