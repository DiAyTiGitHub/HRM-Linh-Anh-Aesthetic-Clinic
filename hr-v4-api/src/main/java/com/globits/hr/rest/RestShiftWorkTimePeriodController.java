package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.ShiftWorkTimePeriodDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.ShiftWorkTimePeriodService;
import com.globits.timesheet.dto.search.SearchShiftWorkTimePeriodDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/timePeriod")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestShiftWorkTimePeriodController {
    @Autowired
    private ShiftWorkTimePeriodService timePeriodService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PostMapping("/getPage")
    public ResponseEntity<Page<ShiftWorkTimePeriodDto>> getPageTimePeriod(@RequestBody SearchShiftWorkTimePeriodDto dto) {
        Page<ShiftWorkTimePeriodDto> page = timePeriodService.searchByPage(dto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PostMapping()
    public ResponseEntity<ShiftWorkTimePeriodDto> create(@RequestBody ShiftWorkTimePeriodDto dto) {
        ShiftWorkTimePeriodDto result = timePeriodService.saveOne(dto, null);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PutMapping("/{id}")
    public ResponseEntity<ShiftWorkTimePeriodDto> update(@RequestBody ShiftWorkTimePeriodDto dto, @PathVariable UUID id) {
        ShiftWorkTimePeriodDto result = timePeriodService.saveOne(dto, id);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable String id) {
        timePeriodService.remove(UUID.fromString(id));
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @GetMapping("/{id}")
    public ResponseEntity<ShiftWorkTimePeriodDto> getItemById(@PathVariable String id) {
        ShiftWorkTimePeriodDto result = timePeriodService.getById(UUID.fromString(id));
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @RequestMapping(value = "/getAllReligions", method = RequestMethod.GET)
    public ResponseEntity<List<ShiftWorkTimePeriodDto>> getAllShiftWorkTimePeriod() {
        List<ShiftWorkTimePeriodDto> timePeriods = timePeriodService.getAll();
        return new ResponseEntity<>(timePeriods, HttpStatus.OK);
    }
    @RequestMapping(value = "/get-time-period-by-shift-work-id/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<ShiftWorkTimePeriodDto>> getShiftWorkTimePeriodByShiftWorkId(@PathVariable("id") UUID id) {
        List<ShiftWorkTimePeriodDto> timePeriods = timePeriodService.getShiftWorkTimePeriodByShiftWorkId(id);
        return new ResponseEntity<>(timePeriods, HttpStatus.OK);
    }
}
