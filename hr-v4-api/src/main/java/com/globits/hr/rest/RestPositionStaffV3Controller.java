package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.service.PositionStaffV3Service;
import com.globits.hrv3.dto.view.PositionStaffV3Dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v3/positionStaff")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestPositionStaffV3Controller {
    @Autowired
    private PositionStaffV3Service positionStaffV3Service;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @RequestMapping(value = "/getById/{id}", method = RequestMethod.GET)
    public PositionStaffV3Dto getById(@PathVariable("id") String id) {
        return positionStaffV3Service.getById(UUID.fromString(id));
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/getByStaff/{staffId}", method = RequestMethod.GET)
    public List<PositionStaffV3Dto> getListByStaffId(@PathVariable("staffId") String staffId) {
        return positionStaffV3Service.getListByStaffId(UUID.fromString(staffId));
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/savePositionStaff", method = RequestMethod.POST)
    public ResponseEntity<PositionStaffV3Dto> save(@RequestBody PositionStaffV3Dto dto) {
        PositionStaffV3Dto result = positionStaffV3Service.saveOrUpdate(dto, null);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/savePositionStaff/{id}", method = RequestMethod.PUT)
    public ResponseEntity<PositionStaffV3Dto> update(@RequestBody PositionStaffV3Dto dto, @PathVariable UUID id) {
        PositionStaffV3Dto result = positionStaffV3Service.saveOrUpdate(dto, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Boolean removeTitle(@PathVariable("id") String id) {
        return positionStaffV3Service.remove(UUID.fromString(id));
    }
}
