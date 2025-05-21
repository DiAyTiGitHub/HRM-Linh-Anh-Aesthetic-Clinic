package com.globits.hr.rest;

import com.globits.hr.dto.RecruitmentPlanDto;
import com.globits.hr.dto.RecruitmentRoundDto;
import com.globits.hr.dto.search.SearchRecruitmentDto;
import com.globits.hr.service.RecruitmentRoundService;
import com.globits.hr.utils.ImportExportExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/recruitment-round")
public class RecruitmentRoundController {

    @Autowired
    private RecruitmentRoundService recruitmentRoundService;

    @PostMapping("/paging")
    public Page<RecruitmentRoundDto> paging(@RequestBody SearchRecruitmentDto searchDto) {
        return recruitmentRoundService.pagingRecruitmentRound(searchDto);
    }

    @PostMapping("/export-excel-recruitment-round-template")
    public void exportExcelRecruitmentRoundTemplate(HttpServletResponse response) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_VONG_TUYEN_DUNG.xlsx");

        try (InputStream inputStream = new ClassPathResource("Excel/MAU_VONG_TUYEN_DUNG.xlsx").getInputStream()) {
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/import-excel-recruitment-round-template", method = RequestMethod.POST)
    public ResponseEntity<?> importRecruitmentRoundFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<RecruitmentRoundDto> list = ImportExportExcelUtil.readRecruitmentRoundFromFile(bis);
            if (list == null || list.isEmpty()) {
                return new ResponseEntity<>("File import không có dữ liệu.", HttpStatus.BAD_REQUEST);
            }
            if (list.get(0) != null && list.get(0).getErrorMessage() != null) {
                return new ResponseEntity<>(list.get(0).getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
            Integer countSave = recruitmentRoundService.saveListRecruitmentRound(list);
            return new ResponseEntity<>(countSave, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
