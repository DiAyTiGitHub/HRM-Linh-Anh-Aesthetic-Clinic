package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffCivilServantGradeDto;
import com.globits.hr.service.StaffCivilServantGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/staffcivilservantgrade")
public class RestStaffCivilServantGradeController {
    @Autowired
    private StaffCivilServantGradeService staffCivilServantGradeService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public StaffCivilServantGradeDto saveStaffCivilServantGrade(
            @RequestBody StaffCivilServantGradeDto staffCivilServantGradeDto) {
        return staffCivilServantGradeService
                .saveStaffCivilServantGrade(staffCivilServantGradeDto);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StaffCivilServantGradeDto> getDocument(@PathVariable("id") UUID id) {
        StaffCivilServantGradeDto dto = staffCivilServantGradeService.getStaffCivilServantGrade(id);
        if (dto == null) {
            return new ResponseEntity<>(new StaffCivilServantGradeDto(),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{staffCivilServantGradeId}", method = RequestMethod.DELETE)
    public Boolean removeStaffCivilServantGrade(
            @PathVariable("staffCivilServantGradeId") String staffCivilServantGradeId) {
        return staffCivilServantGradeService.removeStaffCivilServantGrade(UUID.fromString(staffCivilServantGradeId));
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteStaffCivilServantGrades(@RequestBody StaffCivilServantGradeDto[] dtos) {
        Boolean deleted = staffCivilServantGradeService.deleteMultiple(dtos);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }
}
