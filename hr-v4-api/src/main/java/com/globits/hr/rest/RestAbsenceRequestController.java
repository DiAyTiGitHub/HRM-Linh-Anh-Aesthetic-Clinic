package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.AbsenceRequestDto;
import com.globits.hr.dto.search.AbsenceRequestSearchDto;
import com.globits.hr.service.AbsenceRequestService;
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
@RequestMapping("/api/absence-request")
public class RestAbsenceRequestController {
    @Autowired
    private AbsenceRequestService service;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/search-by-page", method = RequestMethod.POST)
    public ResponseEntity<Page<AbsenceRequestDto>> pagingAbsenceRequest(@RequestBody AbsenceRequestSearchDto dto) {
        Page<AbsenceRequestDto> result = service.pagingAbsenceRequestDto(dto);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public Boolean deleteById(@PathVariable UUID id) {
        return service.deleteById(id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/get-by-id/{id}", method = RequestMethod.GET)
    public ResponseEntity<AbsenceRequestDto> getAbsenceRequestById(@PathVariable UUID id) {
        AbsenceRequestDto dto = service.getById(id);
        if (dto == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/save-or-update", method = RequestMethod.POST)
    public AbsenceRequestDto saveAbsenceRequest(@RequestBody AbsenceRequestDto dto) {
        return service.saveOrUpdate(dto);
    }
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update-requests-approval-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UUID>> updateRequestsApprovalStatus(@RequestBody AbsenceRequestSearchDto searchDto) {
        List<UUID> updatedRequestIds = service.updateRequestsApprovalStatus(searchDto);
        if (updatedRequestIds != null && updatedRequestIds.size() > 0)
            return new ResponseEntity<>(updatedRequestIds, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, value = "/delete-multiple", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> listId) {
        Boolean deleted = service.deleteMultiple(listId);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

}
