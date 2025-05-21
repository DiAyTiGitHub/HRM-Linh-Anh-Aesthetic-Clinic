package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.DepartmentTypeDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.DepartmentTypeService;
import com.globits.hr.utils.ImportExportExcelUtil;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
@RequestMapping("/api/department-type")
@CrossOrigin(value = "*")
public class RestDepartmentTypeController {
    @Autowired
    DepartmentTypeService departmentTypeService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/saveDepartmentType")
    public ResponseEntity<DepartmentTypeDto> saveDepartmentType(@RequestBody DepartmentTypeDto dto) {
        Boolean isValidCode = departmentTypeService.isValidCode(dto);
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<DepartmentTypeDto>(dto, HttpStatus.CONFLICT);
        }
        DepartmentTypeDto response = departmentTypeService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/pagingDepartmentType", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<DepartmentTypeDto>> pagingDepartmentType(@RequestBody SearchDto searchDto) {
        Page<DepartmentTypeDto> page = departmentTypeService.pageBySearch(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{DepartmentTypeId}")
    public ResponseEntity<Boolean> deleteDepartmentType(@PathVariable("DepartmentTypeId") UUID departmentTypeId) {
        Boolean res = departmentTypeService.deleteDepartmentType(departmentTypeId);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{DepartmentTypeId}", method = RequestMethod.GET)
    public ResponseEntity<DepartmentTypeDto> getById(@PathVariable("DepartmentTypeId") UUID departmentTypeId) {
        DepartmentTypeDto result = departmentTypeService.getById(departmentTypeId);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> departmentTypeIds) {
        Boolean deleted = departmentTypeService.deleteMultiple(departmentTypeIds);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER})
    @PostMapping("/export-excel-department-type-template")
    public void exportExcelDepartmentTypeTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_LOAI_PHONG_BAN.xlsx");
        ClassPathResource resource = new ClassPathResource("Excel/MAU_LOAI_PHONG_BAN.xlsx");
        if (resource.exists()) {
            try (InputStream inputStream = resource.getInputStream()) {
                IOUtils.copy(inputStream, response.getOutputStream());
                response.flushBuffer();
            }
        } else {
            try (Workbook workbook = new XSSFWorkbook()) {
                workbook.createSheet("Loại phòng ban");
                workbook.write(response.getOutputStream());
                response.flushBuffer();
            }
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER})
    @RequestMapping(value = "/import-excel-department-type", method = RequestMethod.POST)
    public ResponseEntity<?> importDepartmentTypeFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<DepartmentTypeDto> list = ImportExportExcelUtil.readDepartmentTypeFile(bis);
            int result = departmentTypeService.saveListDepartmentType(list);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/auto-gen-code/{configKey}")
    public ResponseEntity<?> autoGenCode(@PathVariable("configKey") String configKey) {
        return new ResponseEntity<>(departmentTypeService.autoGenerateCode(configKey), HttpStatus.OK);
    }
}
