package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.*;
import com.globits.hr.dto.function.ImportStaffDto;
import com.globits.hr.dto.importExcel.HrOrganizationImportResult;
import com.globits.hr.dto.importExcel.StaffLAImport;
import com.globits.hr.dto.importExcel.StaffLAImportResult;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.service.HrOrganizationService;
import com.globits.hr.utils.ExcelUtils;
import com.globits.hr.utils.ExportExcelUtil;
import com.globits.hr.utils.ImportExportExcelUtil;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/hr-organization")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestHrOrganizationController {
    private static final Logger logger = LoggerFactory.getLogger(HrOrganizationDto.class);


    @Autowired
    private HrOrganizationService service;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/import-excel-organization", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importOrganizationFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            HrOrganizationImportResult importResults = service.readDataFromExcel(bis);

            if (importResults.getSuccessImportRows() != null && !importResults.getSuccessImportRows().isEmpty()) {
                importResults = service.saveHrOrganizationImportFromExcel(importResults);
            } else {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>("successful", HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ResponseEntity<HrOrganizationDto> saveOrUpdate(@RequestBody HrOrganizationDto dto) {
        Boolean isValidCode = service.isValidCode(dto);
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<>(dto, HttpStatus.CONFLICT);
        }
        HrOrganizationDto result = service.saveOrUpdate(dto);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<HrOrganizationDto> getOne(@PathVariable UUID id) {
        HrOrganizationDto result = service.getHROrganization(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteOne(@PathVariable UUID id) {
        Boolean result = service.deleteHrOrganization(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/delete-multiple", method = RequestMethod.POST)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        Boolean result = service.deleteMultipleHrOrganizations(ids);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/search-by-page", method = RequestMethod.POST)
    public ResponseEntity<Page<HrOrganizationDto>> searchByPage(@RequestBody SearchDto searchDto) {
        Page<HrOrganizationDto> result = service.searchByPage(searchDto);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/check-code", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("code") String code) {
        Boolean result = service.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/paging-organizations", method = RequestMethod.POST)
    public ResponseEntity<Page<HrOrganizationDto>> pagingDepartments(@RequestBody SearchDto searchDto) {
        Page<HrOrganizationDto> page = service.pagingHrOrganizations(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @PostMapping("/export-excel-organization-template")
    public void exportExcelOrganizationTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_DON_VI.xlsx");

        try (InputStream inputStream = new ClassPathResource("Excel/MAU_DON_VI.xlsx").getInputStream()) {
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        }
    }

    private void setupResponseHeaders(HttpServletResponse response, String fileName, String contentType, String extension) {
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + extension);
    }

    private String generateFileName(String baseName) {
        String dateTimeString = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        return baseName + "-" + dateTimeString;
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-excel-organization")
    public ResponseEntity<?> exportExcelOrgData(HttpServletResponse response, @RequestBody SearchDto dto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            Workbook workbook = service.exportExcelHrOrganization(dto);
            setupResponseHeaders(response, generateFileName("DuLieuDonVi"), CONTENT_TYPE, FILE_EXTENSION);
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
        }
    }

//    @RequestMapping(value = "/import-excel-organization", method = RequestMethod.POST)
//    public ResponseEntity<?> importOrganizationFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
//        try {
//            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
//            List<HrOrganizationDto> list = ImportExportExcelUtil.readHrOrganizationFile(bis);
//            int countSaveOrganization = 0;
//            for (HrOrganizationDto dto : list) {
//                HrOrganizationDto ds = service.saveOrUpdate(dto);
//                if (ds != null) countSaveOrganization++;
//            }
//            return new ResponseEntity<>(countSaveOrganization, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }

    @GetMapping(value = "/auto-gen-code/{configKey}")
    public ResponseEntity<?> autoGenCode(@PathVariable("configKey") String configKey) {
        return new ResponseEntity<>(service.autoGenerateCode(configKey), HttpStatus.OK);
    }
}