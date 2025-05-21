package com.globits.hr.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.PositionTitleDto;
import com.globits.hr.dto.diagram.ResponseDiagram;
import com.globits.hr.dto.search.SearchHrDepartmentDto;
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
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.HRDepartmentService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/hRDepartment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestHRDepartmentController {
    @Autowired
    private HRDepartmentService hRDepartmentService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<HRDepartmentDto> saveOrUpdate(@RequestBody HRDepartmentDto dto) {
        // HRDepartment's code is duplicated
        Boolean isValidCode = hRDepartmentService.isValidCode(dto);
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<HRDepartmentDto>(dto, HttpStatus.CONFLICT);
        }

        HRDepartmentDto result = hRDepartmentService.saveOrUpdate(dto);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<HRDepartmentDto> getOne(@PathVariable UUID id) {
        HRDepartmentDto result = hRDepartmentService.getHRDepartment(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/diagram/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<ResponseDiagram>> getDiagram(@PathVariable UUID id) {
        List<ResponseDiagram> result = hRDepartmentService.getHRDepartmentDiagram(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteOne(@PathVariable UUID id) {
        Boolean result = hRDepartmentService.deleteHRDepartment(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = hRDepartmentService.deleteMultiple(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER})
    @RequestMapping(value = "/paging-tree-department", method = RequestMethod.POST)
    public ResponseEntity<Page<HRDepartmentDto>> pagingTreeDepartments(@RequestBody SearchHrDepartmentDto searchDto) {
        Page<HRDepartmentDto> page = this.hRDepartmentService.pagingTreeDepartments(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER})
    @RequestMapping(value = "/paging-department-hierarchy", method = RequestMethod.POST)
    public ResponseEntity<Page<HRDepartmentDto>> pagingDepartmentHierarchy(@RequestBody SearchHrDepartmentDto searchDto) {
        Page<HRDepartmentDto> page = this.hRDepartmentService.pagingDepartmentHierarchy(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER})
    @RequestMapping(value = "/check-valid-parent", method = RequestMethod.POST)
    public ResponseEntity<Boolean> checkValidParent(@RequestBody SearchDto searchDto) {
        Boolean page = hRDepartmentService.checkValidParent(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER})
    @RequestMapping(value = "/checkCode", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id,
                                             @RequestParam("code") String code) {
        Boolean result = hRDepartmentService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER})
    @RequestMapping(value = "/paging-department", method = RequestMethod.POST)
    public ResponseEntity<Page<HRDepartmentDto>> pagingDepartments(@RequestBody SearchHrDepartmentDto searchDto) {
        Page<HRDepartmentDto> page = this.hRDepartmentService.pagingDepartments(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER})
    @PostMapping("/export-excel-department-template")
    public void exportExcelHRDepartmentTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_PHONG_BAN.xlsx");

        try (InputStream inputStream = new ClassPathResource("Excel/MAU_PHONG_BAN.xlsx").getInputStream()) {
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
    @PostMapping("/export-excel-hr-department")
    public ResponseEntity<?> exportExcelDepartmentData(HttpServletResponse response, @RequestBody SearchHrDepartmentDto dto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            Workbook workbook = hRDepartmentService.exportExcelDepartment(dto);

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

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER})
    @RequestMapping(value = "/import-excel-department", method = RequestMethod.POST)
    public ResponseEntity<?> importHRDepartmentFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<HRDepartmentDto> list = ImportExportExcelUtil.readDepartmentFile(bis);
            if (list == null || list.isEmpty()) {
                return new ResponseEntity<>("File import không có dữ liệu.", HttpStatus.BAD_REQUEST);
            }
            if (list.get(0) != null && list.get(0).getErrorMessage() != null) {
                return new ResponseEntity<>(list.get(0).getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
            int result = this.hRDepartmentService.saveListDepartment(list);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/auto-gen-code/{configKey}")
    public ResponseEntity<?> autoGenCode(@PathVariable("configKey") String configKey) {
        return new ResponseEntity<>(hRDepartmentService.autoGenerateCode(configKey), HttpStatus.OK);
    }
}
