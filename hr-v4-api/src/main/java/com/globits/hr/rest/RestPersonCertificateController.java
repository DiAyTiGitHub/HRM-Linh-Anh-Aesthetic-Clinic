package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.PersonCertificateDto;
import com.globits.hr.dto.StaffLeaveDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchPersonCertificateDto;
import com.globits.hr.service.PersonCertificateService;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
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
@RequestMapping("/api/person-certificate")
public class RestPersonCertificateController {
    @Autowired
    private PersonCertificateService personCertificateService;

    @RequestMapping(method = RequestMethod.POST, path = "/save")
    public ResponseEntity<PersonCertificateDto> saveOrUpdate(@RequestBody PersonCertificateDto dto) {
        PersonCertificateDto response = personCertificateService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/search-by-page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<PersonCertificateDto>> searchByPage(@RequestBody SearchPersonCertificateDto searchDto) {
        Page<PersonCertificateDto> page = personCertificateService.searchByPage(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping(value = "/initial-filter")
    public ResponseEntity<SearchPersonCertificateDto> getInitialFilter() {
        SearchPersonCertificateDto response = personCertificateService.getInitialFilter();

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/get-all-by-person/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<PersonCertificateDto>> getAllPersonCertificateByPerson(@PathVariable("id") UUID id) {
        List<PersonCertificateDto> personCertificateDtoList = personCertificateService.getPersonCertificateAllByPersonId(id);
        if (personCertificateDtoList == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(personCertificateDtoList, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<PersonCertificateDto> getById(@PathVariable("id") UUID id) {
        PersonCertificateDto response = personCertificateService.getById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id") UUID id) {
        Boolean response = personCertificateService.deleteById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/deleteMultiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> deleteMultiple(@RequestBody List<UUID> ids) {
        Integer response = personCertificateService.deleteMultiple(ids);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
