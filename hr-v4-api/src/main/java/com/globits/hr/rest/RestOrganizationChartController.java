package com.globits.hr.rest;

import java.util.List;
import java.util.UUID;

import com.globits.hr.domain.OrgChartData;
import com.globits.hr.domain.OrganizationChart;
import com.globits.hr.dto.diagram.RequestGetChart;
import com.globits.hr.dto.diagram.ResponseSaveChart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.OrgChartDataDto;
import com.globits.hr.dto.OrganizationChartDto;
import com.globits.hr.dto.OrganizationChartRelationDto;
import com.globits.hr.dto.search.OrgChartSearchDto;
import com.globits.hr.service.OrganizationChartRelationService;
import com.globits.hr.service.OrganizationChartService;
import com.globits.security.dto.UserDto;


@RestController
@RequestMapping("/api/org-chart")
public class RestOrganizationChartController {

    @Autowired
    private OrganizationChartService orgChartService;
    @Autowired
    private OrganizationChartRelationService orgChartRelationService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/save")
    public ResponseEntity<ResponseSaveChart> saveOrUpdate(@RequestBody OrganizationChartDto dto) {
        ResponseSaveChart result = orgChartService.saveOrUpdate(dto);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<ResponseSaveChart>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/save-list")
    public ResponseEntity<OrgChartDataDto> saveListOrganizationChart(@RequestBody List<OrganizationChartDto> dtos) {
        OrgChartDataDto result = orgChartService.saveListOrganizationChart(dtos);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.DELETE, path = "/delete/{id}")
    public ResponseEntity<OrganizationChartDto> delete(@PathVariable("id") UUID id) {
        OrganizationChartDto result = orgChartService.deleteById(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<OrganizationChartDto>(result, HttpStatus.OK);
    }

    @PostMapping("/link-org-chart")
    public ResponseEntity<OrganizationChartRelationDto> linkOrgChart(@RequestBody OrganizationChartRelationDto dto) {
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        OrganizationChartRelationDto result = orgChartRelationService.saveOrUpdate(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/sync-position")
    public ResponseEntity<OrgChartDataDto> linkOrgChart() {
        OrgChartDataDto result = orgChartService.syncPosition();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/disconnect-org-chart/{id}")
    public ResponseEntity<OrganizationChartRelationDto> disconnectOrgChart(@PathVariable("id") UUID id) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        OrganizationChartRelationDto result = orgChartRelationService.deleteById(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/save-org-chart-data")
    public ResponseEntity<OrgChartDataDto> saveOrgChartData(@RequestBody OrgChartDataDto dto) {
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        OrgChartDataDto result = orgChartService.saveOrgChartData(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get-line-manager/{username}")
    public ResponseEntity<UserDto> getLineManager(@PathVariable("username") String username) {
        if (username == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        UserDto result = orgChartService.getLineManager(username);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get-org-chart-data/{id}")
    public ResponseEntity<OrgChartDataDto> saveOrgChartData(@PathVariable("id") UUID id) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        OrgChartDataDto result = orgChartService.getOrgChartData(id);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/position")
    public ResponseEntity<OrgChartDataDto> getOCbyStaff(@RequestBody RequestGetChart requestGetChart) {
        if (requestGetChart == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        OrgChartDataDto result = orgChartService.generateNodesFromNode(requestGetChart);
        return ResponseEntity.ok(result);
    }

    // API mới từ Department
    @PostMapping("/from-department")
    public ResponseEntity<OrgChartDataDto> getOrgChartFromDepartment(@RequestBody RequestGetChart request) {
        OrgChartDataDto result = orgChartService.generateNodesFromDepartment(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/refresh/{id}")
    public ResponseEntity<OrgChartDataDto> refreshOrgChartData(@PathVariable("id") UUID id) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        OrgChartDataDto result = orgChartService.refreshOrgChartData(id);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/paging-org-chart-data")
    public ResponseEntity<Page<OrgChartDataDto>> pagingOrgChartData(@RequestBody OrgChartSearchDto dto) {
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Page<OrgChartDataDto> result = orgChartService.pagingOrgChartData(dto);
        return ResponseEntity.ok(result);
    }


    @DeleteMapping(value = "/delete-org-chart-data/{id}")
    public ResponseEntity<Boolean> deleteOrgChartData(@PathVariable("id") UUID id) {
        Boolean result = orgChartService.deleteOrgChartData(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/delete-multiple-org-chart-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = orgChartService.deleteMultipleOrgChartData(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/save-relationship")
    public ResponseEntity<OrganizationChartRelationDto> savePositionRelationShip(@RequestBody OrganizationChartRelationDto dto) {
        OrganizationChartRelationDto result = orgChartRelationService.savePositionRelationShip(dto);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(result);
    }


    @PostMapping("/delete-relationship")
    public ResponseEntity<Boolean> deletePositionRelationShip(@RequestBody OrganizationChartRelationDto dto) {
        Boolean result = orgChartRelationService.deletePositionRelationShip(dto);
        if (result) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }
}
