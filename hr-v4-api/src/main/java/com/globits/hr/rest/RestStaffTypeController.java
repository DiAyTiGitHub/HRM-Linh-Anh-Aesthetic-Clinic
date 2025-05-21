package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.core.dto.SearchDto;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffTypeDto;
import com.globits.hr.service.StaffTypeService;
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
@RequestMapping("/api/staff-type")
public class RestStaffTypeController {

    @Autowired
    private StaffTypeService staffTypeService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/save-or-update")
    public ResponseEntity<StaffTypeDto> saveOrUpdate(@RequestBody StaffTypeDto dto) {
        Boolean checkCode = staffTypeService.checkCode(dto);
        if (!checkCode) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        StaffTypeDto response = staffTypeService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/search-by-page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<StaffTypeDto>> searchByPage(@RequestBody SearchDto searchDto) {
        Page<StaffTypeDto> page = staffTypeService.searchByPage(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/get-by-id/{id}", method = RequestMethod.GET)
    public ResponseEntity<StaffTypeDto> getById(@PathVariable("id") UUID id) {
        StaffTypeDto result = staffTypeService.getById(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/remove/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id") UUID id) {
        Boolean res = staffTypeService.remove(id);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/remove-multiple", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> removeMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = staffTypeService.removeMultiple(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

}
