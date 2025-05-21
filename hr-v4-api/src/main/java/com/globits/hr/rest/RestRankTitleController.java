package com.globits.hr.rest;


import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.RankTitleDto;
import com.globits.hr.dto.search.SearchRankTitleDto;
import com.globits.hr.service.RankTitleService;
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
@RequestMapping("/api/rank-title")
public class RestRankTitleController {
    @Autowired
    private RankTitleService rankTitleService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/saveRankTitle")
    public ResponseEntity<RankTitleDto> saveRankTitle(@RequestBody RankTitleDto dto) {
        Boolean checkShortName = rankTitleService.checkShortName(dto);
        if (!checkShortName) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        RankTitleDto response = rankTitleService.saveRankTitle(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{rankTitleId}", method = RequestMethod.GET)
    public ResponseEntity<RankTitleDto> getById(@PathVariable("rankTitleId") UUID rankTitleId) {
        RankTitleDto response = rankTitleService.getById(rankTitleId);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{rankTitleId}")
    public ResponseEntity<RankTitleDto> deleteById(@PathVariable("rankTitleId") UUID rankTitleId) {
        Boolean response = rankTitleService.deleteRankTitle(rankTitleId);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> rankTitleIds) {
        Boolean deleted = rankTitleService.deleteMultipleRankTitles(rankTitleIds);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/pagingRankTitle", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<RankTitleDto>> pagingRankTitle(@RequestBody SearchRankTitleDto searchDto) {
        Page<RankTitleDto> page = rankTitleService.pagingRankTitle(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-excel-rank-title-template")
    public void exportExcelRankTitleTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_CAP_BAC.xlsx");

        try (InputStream inputStream = new ClassPathResource("Excel/MAU_CAP_BAC.xlsx").getInputStream()) {
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/import-excel-rank-title", method = RequestMethod.POST)
    public ResponseEntity<?> importRankTitleFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<RankTitleDto> list = ImportExportExcelUtil.readRankTitleFile(bis);
            if (list == null || list.isEmpty()) {
                return new ResponseEntity<>("File import không có dữ liệu.", HttpStatus.BAD_REQUEST);
            }
            if (list.get(0) != null && list.get(0).getErrorMessage() != null) {
                return new ResponseEntity<>(list.get(0).getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
            int countSaveRankTitle = rankTitleService.saveListRankTitle(list);
            return new ResponseEntity<>(countSaveRankTitle, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/auto-gen-code/{configValue}")
    public ResponseEntity<?> autoGenCode(@PathVariable("configKey") String configKey) {
        return new ResponseEntity<>(rankTitleService.autoGenerateCode(configKey), HttpStatus.OK);
    }
}
