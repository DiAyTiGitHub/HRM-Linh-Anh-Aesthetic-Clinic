package com.globits.hr.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.search.PositionTitleSearchDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.utils.ImportExportExcelUtil;
import com.globits.salary.dto.SalaryItemDto;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.DepartmentsTreeDto;
import com.globits.hr.dto.PositionTitleDto;
import com.globits.hr.service.PositionTitleService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/positionTitle")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestPositionTitleController {
    @Autowired
    private PositionTitleService titleService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PositionTitleDto> getPositionTitleById(@PathVariable("id") UUID id) {
        PositionTitleDto dto = titleService.getTitle(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{titleId}", method = RequestMethod.DELETE)
    public Boolean removeTitle(@PathVariable("titleId") String titleId) {
        return titleService.removeTitle(UUID.fromString(titleId));
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteTitles(@RequestBody PositionTitleDto[] list) {
        Boolean deleted = titleService.deleteMultiple(list);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/searchByPage", method = RequestMethod.POST)
    public ResponseEntity<Page<PositionTitleDto>> searchByPage(@RequestBody PositionTitleSearchDto searchDto) {
        Page<PositionTitleDto> page = titleService.searchByPage(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/parent/searchByPage", method = RequestMethod.POST)
    public ResponseEntity<Page<PositionTitleDto>> searchByPageParent(@RequestBody PositionTitleSearchDto searchDto) {
        searchDto.setIsGroup(true);
        Page<PositionTitleDto> page = titleService.searchByPage(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("code") String code) {
        Boolean result = titleService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/getByRoot/{pageIndex}/{pageSize}", method = RequestMethod.GET)
    public ResponseEntity<Page<DepartmentsTreeDto>> getByRoot(@PathVariable("pageIndex") int pageIndex,
                                                              @PathVariable("pageSize") int pageSize) {
        Page<DepartmentsTreeDto> page = this.titleService.getByRoot(pageIndex, pageSize);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<PositionTitleDto> save(@RequestBody PositionTitleDto dto) {

        // salaryitem's code is duplicated
        Boolean isValidCode = titleService.isValidCode(dto);
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<PositionTitleDto>(dto, HttpStatus.CONFLICT);
        }

//        // cannot update Item that is system default
//        boolean isSystemDefault = salaryItemService.isSystemDefault(dto.getCode());
//        if (isSystemDefault) return new ResponseEntity<>(null, HttpStatus.NOT_MODIFIED);

        PositionTitleDto response = titleService.saveOrUpdate(dto);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<PositionTitleDto> update(@RequestBody PositionTitleDto dto, @PathVariable UUID id) {
        Boolean isValidCode = titleService.isValidCode(dto);
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<>(dto, HttpStatus.CONFLICT);
        }
        PositionTitleDto result = titleService.saveOrUpdate(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Tải mẫu nhập chức danh
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-excel-position-title-template")
    public void exportExcelPositionTitleTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_CHUC_DANH.xlsx");

        try (InputStream inputStream = new ClassPathResource("Excel/MAU_CHUC_DANH.xlsx").getInputStream()) {
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

    // Export chức danh
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-excel-position-title")
    public ResponseEntity<?> exportExcelPositionTitleData(HttpServletResponse response, @RequestBody PositionTitleSearchDto searchDto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            Workbook workbook = titleService.exportExcelPositionTitleData(searchDto);

            setupResponseHeaders(response, generateFileName("DuLieuPhongBan"), CONTENT_TYPE, FILE_EXTENSION);
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
        }
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/import-excel-position-title", method = RequestMethod.POST)
    public ResponseEntity<?> importPositionTitleFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<PositionTitleDto> list = ImportExportExcelUtil.readPositionTitleFile(bis);
            if (list == null || list.isEmpty()) {
                return new ResponseEntity<>("File import không có dữ liệu.", HttpStatus.BAD_REQUEST);
            }
            if (list.get(0) != null && list.get(0).getErrorMessage() != null) {
                return new ResponseEntity<>(list.get(0).getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
            int countSavePositionTitle = titleService.saveListPositionTitle(list, false);
            return new ResponseEntity<>(countSavePositionTitle, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-excel-group-position-title-template")
    public void exportExcelGroupPositionTitleTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_NHOM_NGACH.xlsx");

        try (InputStream inputStream = new ClassPathResource("Excel/MAU_NHOM_NGACH.xlsx").getInputStream()) {
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/import-excel-group-position-title", method = RequestMethod.POST)
    public ResponseEntity<?> importGroupPositionTitleFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<PositionTitleDto> list = ImportExportExcelUtil.readGroupPositionTitleFile(bis);
            int countSaveGroupPositionTitle = titleService.saveListPositionTitle(list, true);
            return new ResponseEntity<>(countSaveGroupPositionTitle, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/auto-gen-code/{configKey}")
    public ResponseEntity<?> autoGenCode(@PathVariable("configKey") String configKey) {
        return new ResponseEntity<>(titleService.autoGenerateCode(configKey), HttpStatus.OK);
    }
}

