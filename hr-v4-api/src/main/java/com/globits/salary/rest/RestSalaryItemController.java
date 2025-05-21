package com.globits.salary.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import com.globits.hr.dto.CandidateDto;
import com.globits.hr.utils.ImportExportExcelUtil;
import com.globits.timesheet.dto.LeaveRequestDto;
import jakarta.servlet.http.HttpServletResponse;
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
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.search.SearchSalaryItemDto;
import com.globits.salary.service.SalaryItemService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/salary-item")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestSalaryItemController {
    @Autowired
    private SalaryItemService salaryItemService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<SalaryItemDto> saveSalaryItem(@RequestBody SalaryItemDto dto) {
        Boolean checkCode = salaryItemService.isValidCode(dto);
        if (!checkCode) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

//        // cannot update Item that is system default
//        boolean isSystemDefault = salaryItemService.isSystemDefault(dto.getCode());
//        if (isSystemDefault) return new ResponseEntity<>(null, HttpStatus.NOT_MODIFIED);

        SalaryItemDto response = salaryItemService.saveSalaryItem(dto);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<SalaryItemDto> getSalaryItem(@PathVariable UUID id) {
        SalaryItemDto response = salaryItemService.getSalaryItem(id);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteOne(@PathVariable UUID id) {
        Boolean result = salaryItemService.deleteSalaryItem(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/paging-salary-item", method = RequestMethod.POST)
    public ResponseEntity<Page<SalaryItemDto>> searchByPage(@RequestBody SearchSalaryItemDto searchDto) {
        Page<SalaryItemDto> page = this.salaryItemService.searchByPage(searchDto);
        if (page == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/staff/{staffId}", method = RequestMethod.GET)
    public ResponseEntity<List<SalaryItemDto>> getByStaffId(@PathVariable UUID staffId) {
        List<SalaryItemDto> list = this.salaryItemService.getByStaffId(staffId);
        if (list == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = salaryItemService.deleteMultiple(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

//    @Autowired
//    private SalaryItemExpressionHandlerService salaryItemExpressionHandlerService;
//
//    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
//    @RequestMapping(value = "/calculate-expression", method = RequestMethod.POST)
//    public ResponseEntity<Object> searchByPage(@RequestBody ExpressionRequest dto) {
//
//
//        Page<SalaryItemDto> page = this.salaryItemService.searchByPage(searchDto);
//        return new ResponseEntity<>(page, HttpStatus.OK);
//    }


    @PostMapping("/export-excel-salary-item-template")
    public void exportExcelRecruitmentPlanTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_THANH_PHAN_LUONG.xlsx");

        try (InputStream inputStream = new ClassPathResource("Excel/MAU_THANH_PHAN_LUONG.xlsx").getInputStream()) {
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/import-excel-salary-item-template", method = RequestMethod.POST)
    public ResponseEntity<?> importSalaryItemFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<SalaryItemDto> list = ImportExportExcelUtil.readSalaryItemFromFile(bis);
            if (list == null || list.isEmpty()) {
                return new ResponseEntity<>("File import không có dữ liệu.", HttpStatus.BAD_REQUEST);
            }
            if (list.get(0) != null && list.get(0).getErrorMessage() != null) {
                return new ResponseEntity<>(list.get(0).getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
            Integer countSave = salaryItemService.saveListSalaryItem(list);
            return new ResponseEntity<>(countSave, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
