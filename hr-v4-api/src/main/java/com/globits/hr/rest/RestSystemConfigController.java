package com.globits.hr.rest;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.SystemConfigDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.SystemConfigService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/system-config")
public class RestSystemConfigController {
    @Autowired
    private SystemConfigService service;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/paging-system-config", method = RequestMethod.POST)
    public ResponseEntity<Page<SystemConfigDto>> pagingSystemConfig(@RequestBody SearchDto dto) {
        Page<SystemConfigDto> result = service.pagingSystemConfig(dto);
        return new ResponseEntity<Page<SystemConfigDto>>(result,
                (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/save-or-update", method = RequestMethod.POST)
    public ResponseEntity<SystemConfigDto> save(@RequestBody SystemConfigDto dto) {
        SystemConfigDto result = service.saveOrUpdate(dto);
        return new ResponseEntity<SystemConfigDto>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<SystemConfigDto> getById(@PathVariable("id") UUID id) {
        SystemConfigDto result = service.getById(id);
        return new ResponseEntity<SystemConfigDto>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteById(@PathVariable("id") UUID id) {
        Boolean result = service.deleteById(id);
        return new ResponseEntity<Boolean>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(path = "/delete-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> deleteMultiple(@RequestBody List<UUID> ids) {
        Integer response = service.deleteMultiple(ids);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/check-key-code", method = RequestMethod.POST)
    public ResponseEntity<Boolean> checkKeyCode(@RequestBody SystemConfigDto dto) {
        Boolean result = service.checkKeyCode(dto);
        return new ResponseEntity<Boolean>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/getByKey/{keyCode}", method = RequestMethod.GET)
    public ResponseEntity<SystemConfigDto> get(@PathVariable("keyCode") String keyCode) {
        SystemConfigDto result = service.getByKeyCode(keyCode);
        return new ResponseEntity<SystemConfigDto>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
