package com.globits.hr.rest;


import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.RecruitmentPlanDto;
import com.globits.hr.dto.RecruitmentRequestDto;
import com.globits.hr.dto.ShiftWorkDto;
import com.globits.hr.dto.search.SearchRecruitmentDto;
import com.globits.hr.service.RecruitmentPlanService;
import com.globits.hr.utils.ImportExportExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recruitment-plan")
public class RestRecruitmentPlanController {
    @Autowired
    private RecruitmentPlanService recruitmentPlanService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/save-or-update")
    public ResponseEntity<RecruitmentPlanDto> saveRecruitmentPlan(@RequestBody RecruitmentPlanDto dto) {
        // RecruitmentPlan's code is duplicated
        Boolean isValidCode = recruitmentPlanService.isValidCode(dto);
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<RecruitmentPlanDto>(dto, HttpStatus.CONFLICT);
        }

        RecruitmentPlanDto response = recruitmentPlanService.saveRecruitmentPlan(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/search-by-page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<RecruitmentPlanDto>> pagingRecruitmentPlan(@RequestBody SearchRecruitmentDto searchDto) {
        Page<RecruitmentPlanDto> page = recruitmentPlanService.pagingRecruitmentPlan(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/remove/{id}")
    public ResponseEntity<Boolean> deleteRecruitmentPlan(@PathVariable("id") UUID id) {
        Boolean res = recruitmentPlanService.deleteRecruitmentPlan(id);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/get-by-id/{id}", method = RequestMethod.GET)
    public ResponseEntity<RecruitmentPlanDto> getById(@PathVariable("id") UUID id) {
        RecruitmentPlanDto result = recruitmentPlanService.getById(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/remove-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = recruitmentPlanService.deleteMultipleRecruitmentPlan(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update-plan-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UUID>> updatePlansStatus(@RequestBody SearchRecruitmentDto searchDto) {
        List<UUID> updatedPlanIds = recruitmentPlanService.updatePlansStatus(searchDto);
        if (updatedPlanIds != null && updatedPlanIds.size() > 0)
            return new ResponseEntity<>(updatedPlanIds, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/export-excel-recruitment-plan-template")
    public void exportExcelRecruitmentPlanTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=KE_HOACH_TUYEN_DUNG.xlsx");

        try (InputStream inputStream = new ClassPathResource("Excel/KE_HOACH_TUYEN_DUNG.xlsx").getInputStream()) {
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/import-excel-recruitment-plan-template", method = RequestMethod.POST)
    public ResponseEntity<?> importRecruitmentPlanFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<RecruitmentPlanDto> list = ImportExportExcelUtil.readRecruitmentPlanFromFile(bis);
            if (list == null || list.isEmpty()) {
                return new ResponseEntity<>("File import không có dữ liệu.", HttpStatus.BAD_REQUEST);
            }
            if (list.get(0) != null && list.get(0).getErrorMessage() != null) {
                return new ResponseEntity<>(list.get(0).getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
            Integer countSave = recruitmentPlanService.saveListRecruitmentPlan(list);
            return new ResponseEntity<>(countSave, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/auto-gen-code/{configKey}")
    public ResponseEntity<?> autoGenCode(@PathVariable("configKey") String configKey) {
        return new ResponseEntity<>(recruitmentPlanService.autoGenerateCode(configKey), HttpStatus.OK);
    }
}
