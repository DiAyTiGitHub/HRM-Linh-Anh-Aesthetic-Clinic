package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.RecruitmentExamTypeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.RecruitmentExamTypeService;
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
@RequestMapping("/api/recruitment-exam-type")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestRecruitmentExamTypeController {
    @Autowired
    private RecruitmentExamTypeService recruitmentExamTypeService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PostMapping("/search-by-page")
    public ResponseEntity<Page<RecruitmentExamTypeDto>> searchByPage(@RequestBody SearchDto dto) {
        Page<RecruitmentExamTypeDto> page = recruitmentExamTypeService.searchByPage(dto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PostMapping()
    public ResponseEntity<RecruitmentExamTypeDto> create(@RequestBody RecruitmentExamTypeDto dto) {
        Boolean isValidCode = recruitmentExamTypeService.checkCode(null, dto.getCode());
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<RecruitmentExamTypeDto>(dto, HttpStatus.CONFLICT);
        }
        RecruitmentExamTypeDto result = recruitmentExamTypeService.saveOne(dto, null);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PutMapping("/{id}")
    public ResponseEntity<RecruitmentExamTypeDto> update(@RequestBody RecruitmentExamTypeDto dto, @PathVariable UUID id) {
        UUID currentId = dto.getId() != null ? dto.getId() : id;
        Boolean isValidCode = recruitmentExamTypeService.checkCode(currentId, dto.getCode());
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<RecruitmentExamTypeDto>(dto, HttpStatus.CONFLICT);
        }
        RecruitmentExamTypeDto result = recruitmentExamTypeService.saveOne(dto, id);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @DeleteMapping("/{recruitmentExamTypeId}")
    public ResponseEntity<Boolean> remove(@PathVariable UUID recruitmentExamTypeId) {
        Boolean res = recruitmentExamTypeService.deleteRecruitmentExamType(recruitmentExamTypeId);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @GetMapping("/check-code")
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("code") String code) {
        Boolean result = recruitmentExamTypeService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT",
            "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @GetMapping("/{id}")
    public ResponseEntity<RecruitmentExamTypeDto> getItemById(@PathVariable UUID id) {
        RecruitmentExamTypeDto result = recruitmentExamTypeService.getItemById(id);
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, value = "/delete-multiple", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> recruitmentExamTypeIds) {
        Boolean deleted = recruitmentExamTypeService.deleteMultiple(recruitmentExamTypeIds);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }
}
