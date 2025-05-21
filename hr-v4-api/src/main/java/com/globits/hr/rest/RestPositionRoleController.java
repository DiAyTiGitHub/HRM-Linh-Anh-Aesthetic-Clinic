package com.globits.hr.rest;


import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.PositionRoleDto;
import com.globits.hr.dto.RankTitleDto;
import com.globits.hr.dto.search.SearchPositionRole;
import com.globits.hr.dto.search.SearchRankTitleDto;
import com.globits.hr.service.PositionRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/position-role")
public class RestPositionRoleController {
    @Autowired
    private PositionRoleService positionRoleService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/savePositionRole")
    public ResponseEntity<PositionRoleDto> savePositionRole(@RequestBody PositionRoleDto dto) {
        PositionRoleDto response = positionRoleService.savePositionRole(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{positionRoleId}", method = RequestMethod.GET)
    public ResponseEntity<PositionRoleDto> getById(@PathVariable("positionRoleId") UUID positionRoleId) {
        PositionRoleDto response = positionRoleService.getById(positionRoleId);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{positionRoleId}")
    public ResponseEntity<PositionRoleDto> deletePositionRole(@PathVariable("positionRoleId") UUID positionRoleId) {
        Boolean response = positionRoleService.deletePositionRole(positionRoleId);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> positionRoleIds) {
        Boolean deleted = positionRoleService.deleteMultiplePositionRoles(positionRoleIds);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/pagingPositionRole", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<PositionRoleDto>> pagingPositionRole(@RequestBody SearchPositionRole searchDto) {
        Page<PositionRoleDto> page = positionRoleService.pagingPositionRoles(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }
}
