package com.globits.salary.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.globits.core.Constants;
import com.globits.core.dto.SearchDto;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.PersonBankAccountDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.StaffFamilyRelationshipDto;
import com.globits.hr.dto.function.ImportStaffDto;
import com.globits.hr.dto.importExcel.StaffLAImport;
import com.globits.hr.dto.importExcel.StaffLAImportResult;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.dto.SalaryResultDto;
import com.globits.salary.dto.SalaryResultStaffDto;
import com.globits.salary.dto.SalaryTemplateItemDto;
import com.globits.salary.dto.excel.ImportSalaryResultStaffDto;
import com.globits.salary.dto.search.SearchSalaryResultDto;
import com.globits.salary.dto.search.SearchSalaryResultStaffDto;
import com.globits.salary.repository.SalaryResultRepository;
import com.globits.salary.service.*;

import com.globits.salary.utils.ExportExcelUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/salary-result")
public class RestSalaryResultController {
    private static final Logger logger = LoggerFactory.getLogger(StaffDto.class);

    @Autowired
    private SalaryResultService salaryResultService;

    @Autowired
    private SalaryResultStaffService salaryResultStaffService;

    @Autowired
    private SalaryPayslipService salaryPayslipService;

    @Autowired
    private CommissionPayrollService commissionPayrollService;


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/save-salary-result")
    public ResponseEntity<SalaryResultDto> saveOrUpdate(@RequestBody SalaryResultDto dto) {
        // SalaryResult code is duplicated
        Boolean isValidCode = salaryResultService.isValidCode(dto);
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<SalaryResultDto>(dto, HttpStatus.CONFLICT);
        }

        SalaryResultDto response = salaryResultService.saveOrUpdate(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/save-board-config-of-salary-result")
    public ResponseEntity<SalaryResultDto> saveBoardConfigOfSalaryResult(@RequestBody SalaryResultDto dto) {
        SalaryResultDto response = salaryResultService.saveBoardConfigOfSalaryResultV2(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/paging-salary-result", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<SalaryResultDto>> searchByPage(@RequestBody SearchSalaryResultDto searchDto) {
        Page<SalaryResultDto> page = salaryResultService.searchByPage(searchDto);
        if (page == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/get-by-id/{id}", method = RequestMethod.GET)
    public ResponseEntity<SalaryResultDto> getById(@PathVariable("id") UUID id) {
        SalaryResultDto result = salaryResultService.getById(id);
        if (result == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/get-basic-info/{id}", method = RequestMethod.GET)
    public ResponseEntity<SalaryResultDto> getBasicInfoById(@PathVariable("id") UUID id) {
        SalaryResultDto result = salaryResultService.getBasicInfoById(id);
        if (result == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/get-config-salary-result/{id}", method = RequestMethod.GET)
    public ResponseEntity<SalaryResultDto> getConfigSalaryResult(@PathVariable("id") UUID id) {
        SalaryResultDto result = salaryResultService.getConfigSalaryResult(id);
        if (result == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/get-salary-result-board/{id}", method = RequestMethod.GET)
    public ResponseEntity<SalaryResultDto> getSalaryResultBoard(@PathVariable("id") UUID id) {
        SalaryResultDto result = salaryResultService.getSalaryResultBoard(id);
        if (result == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/get-list-template-item-by-salary-result/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<SalaryTemplateItemDto>> getListTemplateItem(@PathVariable("id") UUID salaryResultId) {
        List<SalaryTemplateItemDto> result = salaryResultService.getListTemplateItem(salaryResultId);
        if (result == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/handle-excel/{id}")
    public ResponseEntity<?> handleExcel(HttpServletResponse response, @PathVariable("id") UUID id) throws IOException {
        SalaryResultDto result = salaryResultService.getSalaryResultBoard(id);

        if (result != null) {
            ByteArrayResource excelFile = ExportExcelUtil.exportExcelSalaryResult(result);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.addHeader("Content-Disposition", "attachment; filename=Bang_luong.xlsx");
            org.apache.commons.io.IOUtils.copy(excelFile.getInputStream(), response.getOutputStream());

            return ResponseEntity.ok(result);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
    @DeleteMapping(value = "/remove/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id") UUID id) {
        Boolean res = salaryResultService.remove(id);
        if (res == null || res == false)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/remove-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> removeMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = salaryResultService.removeMultiple(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    // Tạo bảng lương và tính lương cho nhiều nhân viên đang áp dụng mẫu
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/create-salary-board")
    public ResponseEntity<SalaryResultDto> createSalaryBoardByPeriodAndTemplate(@RequestBody SalaryResultDto dto) {
        long startCreateSalaryBoardTime = System.nanoTime();

        // SalaryResult is valid or not
        Boolean isValidCode = salaryResultService.isValidToCreateSalaryBoard(dto);
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<SalaryResultDto>(dto, HttpStatus.CONFLICT);
        }

        SalaryResultDto response = salaryResultService.createSalaryBoardByPeriodAndTemplate(dto);

        long endCreateSalaryBoardTime = System.nanoTime();
        long elapsedTimeMs = (endCreateSalaryBoardTime - startCreateSalaryBoardTime) / 1_000_000;

        logger.info("TẠO BẢNG LƯƠNG {} - Xử lý mất {} ms ", dto.getName(), elapsedTimeMs);


        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // TÍNH TOÁN LẠI BẢNG LƯƠNG
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/recalculate-salary-board/{id}")
    public ResponseEntity<SalaryResultDto> recalculateSalaryBoard(@PathVariable("id") UUID id) {
        long startCreateSalaryBoardTime = System.nanoTime();

        SalaryResultDto response = salaryResultService.recalculateSalaryBoard(id);

        long endCreateSalaryBoardTime = System.nanoTime();
        long elapsedTimeMs = (endCreateSalaryBoardTime - startCreateSalaryBoardTime) / 1_000_000;

        logger.info("TÍNH TOÁN LẠI BẢNG LƯƠNG - Xử lý mất {} ms ", elapsedTimeMs);


        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/exportExcelSalaryResult/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> exportExcelSalaryResult(HttpServletResponse response, @PathVariable("id") UUID id) throws IOException {
        SalaryResultDto result = salaryResultService.getSalaryResultBoard(id);

        if (result != null) {
            ByteArrayResource excelFile = ExportExcelUtil.exportExcelSalaryResult(result);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.addHeader("Content-Disposition", "attachment; filename=Mau_Bang_Luong.xlsx");
            org.apache.commons.io.IOUtils.copy(excelFile.getInputStream(), response.getOutputStream());

            return ResponseEntity.ok(result);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    // Tải mẫu nhập giá trị lương
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-file-import-salary-value-by-filter")
    public ResponseEntity<?> exportFileImportSalaryValueByFilter(HttpServletResponse response, @RequestBody SearchSalaryResultStaffDto dto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            Workbook workbook = salaryResultService.exportFileImportSalaryValueByFilter(dto);
            setupResponseHeaders(response, generateFileName("MauNhapDuLieuGiaTriLuong"), CONTENT_TYPE, FILE_EXTENSION);
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
        }
    }

    // Nhập mẫu giá trị lương
    @RequestMapping(value = "/import-file-salary-value-by-filter", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importFileSalaryValueByFilter(
            @RequestParam("uploadfile") MultipartFile uploadfile,
            @RequestParam("data") String dataJson // DTO ở dạng JSON string
    ) {
        try {
            // Convert JSON string về DTO
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            SearchSalaryResultStaffDto searchDto = mapper.readValue(dataJson, SearchSalaryResultStaffDto.class);


            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<ImportSalaryResultStaffDto> list = new ArrayList<>();

            salaryResultStaffService.importSalaryResultStaffItemTemplate(bis, list);

            salaryPayslipService.importItemValueForPayslips(list, searchDto);

            return new ResponseEntity<>("successful", HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping(value = "/not-approved-yet", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SalaryResultDto>> updateStatusNotApprovedYet(@RequestBody List<UUID> ids) {
        List<SalaryResultDto> result = salaryResultService.updateStatus(ids, HrConstants.SalaryResulStaffApprovalStatus.NOT_APPROVED_YET.getValue());

        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping(value = "/approved", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SalaryResultDto>> updateStatusApproved(@RequestBody List<UUID> ids) {
        List<SalaryResultDto> result = salaryResultService.updateStatus(ids, HrConstants.SalaryResulStaffApprovalStatus.APPROVED.getValue());

        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping(value = "/not-approved", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SalaryResultDto>> updateStatusNotApproved(@RequestBody List<UUID> ids) {
        List<SalaryResultDto> result = salaryResultService.updateStatus(ids, HrConstants.SalaryResulStaffApprovalStatus.NOT_APPROVED.getValue());

        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping(value = "/locked", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SalaryResultDto>> updateStatusLocked(@RequestBody List<UUID> ids) {
        List<SalaryResultDto> result = salaryResultService.updateStatus(ids, HrConstants.SalaryResulStaffApprovalStatus.LOCKED.getValue());

        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/search-salary-result-board/{id}", method = RequestMethod.POST)
    public ResponseEntity<SalaryResultDto> searchSalaryResultBoard(@RequestBody SearchSalaryResultStaffDto dto) {
        SalaryResultDto result = salaryResultService.searchSalaryResultBoard(dto);

        if (result == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    // Số lượng phiếu lương có thể thuộc bảng lương nhưng chưa được tổng hợp
    @RequestMapping(value = "/has-any-orphaned-payslips/{id}", method = RequestMethod.GET)
    public ResponseEntity<Integer> hasAnyOrphanedPayslips(@PathVariable("id") UUID id) {
        try {
            Integer hasOrphaned = salaryPayslipService.hasAnyOrphanedPayslips(id);

            if (hasOrphaned != null) {
                return new ResponseEntity<>(hasOrphaned, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(0, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new ResponseEntity<>(0, HttpStatus.BAD_REQUEST);
        }
    }


    // Lấy danh sách phiếu lương có thể tổng hợp vào bảng lương
    @RequestMapping(value = "/orphaned-payslips/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<SalaryResultStaffDto>> getAllOrphanedPayslips(@PathVariable("id") UUID id) {
        List<SalaryResultStaffDto> result = salaryPayslipService.getAllOrphanedPayslips(id);

        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Tổng hợp các phiếu lương được chọn vào bảng lương
    @RequestMapping(value = "/merge-orphaned-payslips", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> mergeOrphanedPayslips(@RequestBody SearchSalaryResultStaffDto dto) {
        Boolean deleted = salaryPayslipService.mergeOrphanedPayslips(dto);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    // Khóa bảng lương
    @RequestMapping(value = "/lock-payroll/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> lockPayroll(@PathVariable("id") UUID id) {
        Boolean response = salaryResultService.lockPayroll(id);
        if (response) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    // Hủy khóa bảng lương
    @RequestMapping(value = "/unlock-payroll/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> unlockPayroll(@PathVariable("id") UUID id) {
        Boolean response = salaryResultService.unlockPayroll(id);
        if (response) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }




    // Xuất excel  bảng lương hoa hồng
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-excel-commission-payroll")
    public ResponseEntity<?> exportExcelCommissionPayroll(HttpServletResponse response, @RequestBody SearchSalaryResultStaffDto dto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            Workbook workbook = commissionPayrollService.exportExcelCommissionPayroll(dto);
            setupResponseHeaders(response, generateFileName("BangLuongHoaHong"), CONTENT_TYPE, FILE_EXTENSION);
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
        }
    }


}
