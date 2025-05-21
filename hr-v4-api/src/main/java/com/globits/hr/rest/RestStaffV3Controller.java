package com.globits.hr.rest;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import com.globits.hr.service.StaffServiceV3;
import com.globits.hrv3.dto.view.GeneralInformationDto;
import com.globits.hrv3.dto.view.ProfileInformationDto;

@RestController
@RequestMapping("/api/v3/staff")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestStaffV3Controller {
    @Autowired
    private StaffServiceV3 staffServiceV3;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @RequestMapping(value = "/generalinformation/{id}", method = RequestMethod.GET)
    public GeneralInformationDto getGeneralInformation(@PathVariable("id") UUID id) {
        return staffServiceV3.getGeneralInformation(id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/generalinformation", method = RequestMethod.POST)
    public ResponseEntity<GeneralInformationDto> save(@RequestBody GeneralInformationDto dto) {
        GeneralInformationDto result = staffServiceV3.saveOrUpdateGeneralInformation(null, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/generalinformation/{id}", method = RequestMethod.PUT)
    public ResponseEntity<GeneralInformationDto> update(@RequestBody GeneralInformationDto dto, @PathVariable UUID id) {
        GeneralInformationDto result = staffServiceV3.saveOrUpdateGeneralInformation(id, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @RequestMapping(value = "/profileInformation/{id}", method = RequestMethod.GET)
    public ProfileInformationDto getProfileInformation(@PathVariable("id") UUID id) {
        return staffServiceV3.getProfileInformation(id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/profileInformation", method = RequestMethod.POST)
    public ResponseEntity<ProfileInformationDto> saveProfileInformation(@RequestBody ProfileInformationDto dto) {
        ProfileInformationDto result = staffServiceV3.saveOrUpdateProfileInformation(null, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/profileInformation/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ProfileInformationDto> updateProfileInformation(@RequestBody ProfileInformationDto dto, @PathVariable UUID id) {
        ProfileInformationDto result = staffServiceV3.saveOrUpdateProfileInformation(id, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
