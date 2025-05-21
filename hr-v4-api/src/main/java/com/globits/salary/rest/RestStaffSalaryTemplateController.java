package com.globits.salary.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.search.SearchStaffSalaryTemplateDto;
import com.globits.salary.dto.SalaryTemplateDto;
import com.globits.salary.dto.StaffSalaryTemplateDto;
import com.globits.salary.dto.search.CalculateSalaryRequest;
import com.globits.salary.service.StaffSalaryTemplateService;
import com.globits.salary.utils.ExportExcelUtil;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/staff-salary-template")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestStaffSalaryTemplateController {

    @Autowired
    StaffSalaryTemplateService staffSalaryTemplateService;

    @RequestMapping(value = "/save-or-update", method = RequestMethod.POST)
    public ResponseEntity<StaffSalaryTemplateDto> saveStaffSalaryTemplateDto(@RequestBody StaffSalaryTemplateDto dto) {
        StaffSalaryTemplateDto result = staffSalaryTemplateService.saveOrUpdate(dto);

        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/save-list")
    public ResponseEntity<?> saveListStaffSalaryTemplate(@RequestBody StaffSalaryTemplateDto dto) {
        Integer result = staffSalaryTemplateService.saveListStaffSalaryTemplate(dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/salary-template-of-staff")
    public ResponseEntity<?> getSalaryTemplatesOfStaff(@RequestBody CalculateSalaryRequest dto) {
        Page<SalaryTemplateDto> result = staffSalaryTemplateService.getSalaryTemplatesOfStaff(dto);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<StaffSalaryTemplateDto> getStaffSalaryTemplateDto(@PathVariable UUID id) {
        StaffSalaryTemplateDto result = staffSalaryTemplateService.getById(id);

        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/find-staff-template-id-by-staff-id-and-template-id", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> findStaffTemplateIdByStaffIdAndTemplateId(@RequestBody SearchStaffSalaryTemplateDto dto) {
        UUID response = staffSalaryTemplateService.findStaffTemplateIdByStaffIdAndTemplateId(dto);

        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteStaffSalaryTemplate(@PathVariable UUID id) {
        Boolean result = staffSalaryTemplateService.deleteStaffSalaryTemplate(id);

        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = staffSalaryTemplateService.deleteMultiple(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/search-by-page", method = RequestMethod.POST)
    public ResponseEntity<Page<StaffSalaryTemplateDto>> searchByPage(@RequestBody SearchStaffSalaryTemplateDto dto) {
        Page<StaffSalaryTemplateDto> result = staffSalaryTemplateService.searchByPage(dto);

        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }


    @RequestMapping(value = "/export-import-template", method = RequestMethod.POST)
    public void exportImportTemplate(HttpServletResponse response, @RequestBody SearchStaffSalaryTemplateDto dto) throws IOException {
        if (dto == null || dto.getSalaryTemplateId() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try (InputStream inputStream = new ClassPathResource("Empty.xlsx").getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

            SalaryTemplateDto salaryTemplateDto = staffSalaryTemplateService.getSalaryTemplate(dto);

            ByteArrayResource excelFile = ExportExcelUtil.wirteSalaryTemplateItem(salaryTemplateDto, workbook);

            if (excelFile != null) {
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=StaffSalaryTemplate.xlsx");
                org.apache.commons.io.IOUtils.copy(excelFile.getInputStream(), response.getOutputStream());
                response.flushBuffer();
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/import-list-staff-salary-template", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<?> exportImportResultStaffSalaryTemplate(@RequestParam("uploadfile") MultipartFile file, HttpServletResponse response) {
        try {
            // Gọi service xử lý và nhận file output
            ByteArrayOutputStream excelOutput = staffSalaryTemplateService.exportImportResultStaffSalaryTemplate(file);

            // Thiết lập response trả về file Excel
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=result_import_staff_salary_template.xlsx");

            // Ghi vào response
            ServletOutputStream out = response.getOutputStream();
            excelOutput.writeTo(out);
            out.flush();
            out.close();

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Lỗi xử lý file: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
