package com.globits.hr.rest;


import com.globits.core.Constants;
import com.globits.core.dto.SearchDto;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.DisciplineReasonDto;
import com.globits.hr.service.DisciplineReasonService;
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
@RequestMapping("/api/discipline-reason")
public class RestDisciplineReasonController {
    @Autowired
    private DisciplineReasonService disciplineReasonService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/save-or-update")
    public ResponseEntity<DisciplineReasonDto> saveDisciplineReason(@RequestBody DisciplineReasonDto dto) {
        Boolean checkCode = disciplineReasonService.checkCode(dto);
        if (!checkCode) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }   DisciplineReasonDto response = disciplineReasonService.saveDisciplineReason(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/search-by-page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<DisciplineReasonDto>> pagingDisciplineReasons(@RequestBody SearchDto searchDto) {
        Page<DisciplineReasonDto> page = disciplineReasonService.pagingDisciplineReasons(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/remove/{disciplineReasonId}")
    public ResponseEntity<Boolean> deleteDisciplineReason(@PathVariable("disciplineReasonId") UUID disciplineReasonId) {
        Boolean res = disciplineReasonService.deleteDisciplineReason(disciplineReasonId);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/get-by-id/{disciplineReasonId}", method = RequestMethod.GET)
    public ResponseEntity<DisciplineReasonDto> getById(@PathVariable("disciplineReasonId") UUID disciplineReasonId) {
        DisciplineReasonDto result = disciplineReasonService.getById(disciplineReasonId);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/remove-multiple", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> disciplineReasonIds) {
        Boolean deleted = disciplineReasonService.deleteMultipleDisciplineReasons(disciplineReasonIds);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

}
