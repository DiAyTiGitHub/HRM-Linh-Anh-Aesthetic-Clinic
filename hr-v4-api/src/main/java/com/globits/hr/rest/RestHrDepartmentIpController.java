package com.globits.hr.rest;


import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.HrDepartmentIpDto;
import com.globits.hr.dto.search.SearchHrDepartmentDto;
import com.globits.hr.service.HrDepartmentIpService;
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
@RequestMapping("/api/hr-department-ip")
public class RestHrDepartmentIpController {
    @Autowired
    private HrDepartmentIpService hrDepartmentIpService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/saveDepartmentIp")
    public ResponseEntity<HrDepartmentIpDto> saveDepartmentIp(@RequestBody HrDepartmentIpDto dto) {
        HrDepartmentIpDto response = hrDepartmentIpService.saveHrDepartmentIp(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{hrDepartmentIpId}", method = RequestMethod.GET)
    public  ResponseEntity<HrDepartmentIpDto> getById(@PathVariable("hrDepartmentIpId") UUID hrDepartmentIpId){
        HrDepartmentIpDto  result = hrDepartmentIpService.getById(hrDepartmentIpId);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{hrDepartmentIpId}")
    public   ResponseEntity<Boolean> deleteHrDepartmentIp(@PathVariable("hrDepartmentIpId") UUID hrDepartmentIpId){
        Boolean res = hrDepartmentIpService.deleteHrDepartmentIp(hrDepartmentIpId);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<Boolean> deleteMultipleHrDepartmentIp(@RequestBody List<UUID> hrDepartmentIpIds){
        Boolean deleted = hrDepartmentIpService.deleteMultipleHrDepartmentIps(hrDepartmentIpIds);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/pagingHrDepartmentIp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<HrDepartmentIpDto>> pagingHrDepartmentIp(@RequestBody SearchHrDepartmentDto searchDto){
        Page<HrDepartmentIpDto> page = hrDepartmentIpService.pagingHrDepartmentIp(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }
}
