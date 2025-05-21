package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.StaffSignatureDto;
import com.globits.hr.dto.search.SearchStaffSignatureDto;
import com.globits.hr.service.StaffSignatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/staff-signature")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestStaffSignatureController {

    @Autowired
    private StaffSignatureService staffSignatureService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/searchByPage")
    public Page<StaffSignatureDto> searchByPage(@RequestBody SearchStaffSignatureDto dto) {
        return staffSignatureService.searchByPage(dto);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @GetMapping(value = "/{id}")
    public ResponseEntity<StaffSignatureDto> getStaffSignature(@PathVariable String id) {
        StaffSignatureDto dto = staffSignatureService.getById(UUID.fromString(id));
        return dto != null ? new ResponseEntity<>(dto, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @GetMapping(value = "/generate-staff-signature")
    public String generateUniqueSignatureCode() {
        return staffSignatureService.generateUniqueSignatureCode();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id") UUID id) {
        Boolean response = staffSignatureService.deleteById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/save-or-update")
    public ResponseEntity<StaffSignatureDto> saveOrUpdate(@RequestBody StaffSignatureDto dto) {
        Boolean isValidCode = staffSignatureService.validateCode(dto);
        if (Boolean.FALSE.equals(isValidCode)) { // Tránh NullPointerException
            dto.setDescription("ERROR: Mã đã tồn tại"); // Cải thiện thông báo lỗi
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        }

        StaffSignatureDto response = staffSignatureService.saveOrUpdate(dto);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.badRequest().build();
    }
}
