package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.core.dto.EthnicsDto;
import com.globits.core.dto.ReligionDto;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.HrEthinicsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/hrEthnics")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestHrEthnicsController {
    @Autowired
    private HrEthinicsService hrEthinicsService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PostMapping("/searchByPage")
    public ResponseEntity<Page<EthnicsDto>> searchByPage(@RequestBody SearchDto dto) {
        Page<EthnicsDto> page = hrEthinicsService.searchByPage(dto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PostMapping()
    public ResponseEntity<EthnicsDto> create(@RequestBody EthnicsDto dto) {
        EthnicsDto result = hrEthinicsService.saveOne(dto, null);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PutMapping("/{id}")
    public ResponseEntity<EthnicsDto> update(@RequestBody EthnicsDto dto, @PathVariable UUID id) {
        EthnicsDto result = hrEthinicsService.saveOne(dto, id);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable String id) {
        hrEthinicsService.remove(UUID.fromString(id));
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("code") String code) {
        Boolean result = hrEthinicsService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @GetMapping("/{id}")
    public ResponseEntity<EthnicsDto> getItemById(@PathVariable UUID id) {
        EthnicsDto result = hrEthinicsService.getItemById(id);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @GetMapping("/get-by-code/{code}")
    public EthnicsDto findByCode(@PathVariable String code) {
        return hrEthinicsService.findByCode(code);
    }


}
