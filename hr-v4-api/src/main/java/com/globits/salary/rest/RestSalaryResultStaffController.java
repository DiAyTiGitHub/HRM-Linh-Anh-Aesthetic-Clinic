package com.globits.salary.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.salary.domain.SalaryResultStaff;
import com.globits.salary.dto.RequestDownloadSlip;
import com.globits.salary.dto.SalaryResultDto;
import com.globits.salary.dto.SalaryResultStaffDto;
import com.globits.salary.dto.SalaryResultStaffPaySlipDto;
import com.globits.salary.dto.excel.ImportSalaryResultStaffDto;
import com.globits.salary.dto.search.SalaryCalculatePayslipDto;
import com.globits.salary.dto.search.SearchSalaryResultDto;
import com.globits.salary.dto.search.SearchSalaryResultStaffDto;
import com.globits.salary.dto.search.SearchStaffAdvancePaymentDto;
import com.globits.salary.service.SalaryPayslipService;
import com.globits.salary.service.SalaryResultService;
import com.globits.salary.service.SalaryResultStaffItemService;
import com.globits.salary.service.SalaryResultStaffService;
import com.globits.salary.utils.ExportExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/salary-result-staff")
public class RestSalaryResultStaffController {
    @Autowired
    private SalaryResultStaffService salaryResultStaffService;

    @Autowired
    private SalaryPayslipService salaryPayslipService;

    @Autowired
    private SalaryResultService salaryResultService;

    @Autowired
    private SalaryResultStaffItemService salaryResultStaffItemService;


    // used for update salary payslip
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/save-salary-staff-payslip")
    public ResponseEntity<SalaryResultStaffPaySlipDto> saveOrUpdate(@RequestBody SalaryResultStaffPaySlipDto dto) {
        SalaryResultStaffPaySlipDto response = salaryPayslipService.saveOrUpdate(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // used for save row data in salary result board
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/save-salary-result-staff")
    public ResponseEntity<SalaryResultStaffDto> saveResultStaff(@RequestBody SalaryResultStaffDto dto) {
        SalaryResultStaffDto response = salaryResultStaffService.saveResultStaff(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/paging-salary-payslip", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> pagingSalaryPayslip(@RequestBody SearchSalaryResultStaffDto searchDto) {
        Page<SalaryResultStaffPaySlipDto> page = salaryPayslipService.pagingSalaryPayslip(searchDto);
        if (page == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping(value = "/initial-filter")
    public ResponseEntity<?> getInitialFilter() {
        SearchSalaryResultStaffDto response = salaryPayslipService.getInitialFilter();

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/paging-salary-result-staff", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> pagingSalaryResultStaff(@RequestBody SearchSalaryResultStaffDto searchDto) {
        Page<SalaryResultStaffDto> page = salaryResultStaffService.pagingSalaryResultStaff(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }
    
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/get-total-salary-result-staff", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getTotalSalaryResultStaff(@RequestBody SearchSalaryResultStaffDto searchDto) {
        SalaryResultStaffDto result = salaryResultStaffService.getTotalSalaryResultStaff(searchDto);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.ROLE_USER})
    @RequestMapping(value = "/get-by-id/{id}", method = RequestMethod.GET)
    public ResponseEntity<SalaryResultStaffPaySlipDto> getById(@PathVariable("id") UUID id) {
        SalaryResultStaffPaySlipDto result = salaryPayslipService.getById(id);
        if (result == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PostMapping("/download-payslip")
    public ResponseEntity<byte[]> getPayslipPdf(@RequestBody RequestDownloadSlip requestDownloadSlip) {
        byte[] pdfBytes = salaryResultStaffService.generatePayslipPdf(requestDownloadSlip.getSalaryResultStaffId(), requestDownloadSlip.getStaffSignatureId());

        if (pdfBytes == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"payslip_" + requestDownloadSlip.getSalaryResultStaffId() + ".pdf\"");
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/export-pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestBody SearchSalaryResultStaffDto dto) {
        byte[] pdfBytes = salaryResultStaffService.exportPdf(dto);

        if (pdfBytes == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"salaryresult.pdf\"");
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/remove-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> removeMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = salaryResultStaffService.removeMultiple(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/delete-salary-result-staff/{resultStaffId}")
    public ResponseEntity<Boolean> deleteSalaryResultStaff(@PathVariable("resultStaffId") UUID resultStaffId) {
        Boolean res = salaryResultStaffService.deleteSalaryResultStaff(resultStaffId);
        if (res == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update-approval-status", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateApprovalStatus(@RequestBody SearchSalaryResultStaffDto dto) {
        try {
            Boolean isUpdated = salaryResultStaffService.updateApprovalStatus(dto);
            if (isUpdated != null && isUpdated.equals(true)) {
                return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update-paid-status", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updatePaidStatus(@RequestBody SearchSalaryResultStaffDto dto) {
        try {
            Boolean isUpdated = salaryResultStaffService.updatePaidStatus(dto);
            if (isUpdated != null && isUpdated.equals(true)) {
                return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/recalculate-payslip-row", method = RequestMethod.POST)
    public ResponseEntity<SalaryResultStaffDto> reCalculateRowByChangingCellValue(@RequestBody SalaryCalculatePayslipDto dto) {
        SalaryResultStaffDto response = salaryPayslipService.reCalculateRowByChangingCellValue(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/view-salary-staff", method = RequestMethod.POST)
    public ResponseEntity<SalaryResultDto> viewSalaryResult(@RequestBody SalaryResultDto dto) {
        SalaryResultDto response = salaryResultStaffService.viewSalaryResult(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "/download-salary-result-staff-item-import-template", method = RequestMethod.POST)
    public void downloadSalaryResultStaffItemImportTemplate(HttpSession session, HttpServletResponse response) throws IOException {
        // Fallback to the resource file
        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("ImportSalaryResultStaffItems.xlsx");
        if (resourceStream == null) {
            throw new FileNotFoundException("Resource file 'ImportSalaryResultStaffItems.xlsx' not found.");
        }

        // Write the resource file directly to the response output stream
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.addHeader("Content-Disposition", "attachment; filename=ImportTimesheetV1.xlsx");

        org.apache.commons.io.IOUtils.copy(resourceStream, response.getOutputStream());
        response.getOutputStream().flush();
        resourceStream.close();
    }

//    @RequestMapping(value = "/upload-salary-result-staff-item-template", method = RequestMethod.POST)
//    public void uploadSalaryResultStaffItemTemplate(HttpSession session, HttpServletResponse response) throws IOException {
//        // Fallback to the resource file
//        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("ImportSalaryResultStaffItems.xlsx");
//        if (resourceStream == null) {
//            throw new FileNotFoundException("Resource file 'ImportSalaryResultStaffItems.xlsx' not found.");
//        }
//
//        // Write the resource file directly to the response output stream
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.addHeader("Content-Disposition", "attachment; filename=ImportTimesheetV1.xlsx");
//
//        org.apache.commons.io.IOUtils.copy(resourceStream, response.getOutputStream());
//        response.getOutputStream().flush();
//        resourceStream.close();
//    }

    @ResponseBody
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/import-salary-result-staff-item-template",
            method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importSalaryResultStaffItemTemplate(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<ImportSalaryResultStaffDto> list = new ArrayList<>();

            salaryResultStaffService.importSalaryResultStaffItemTemplate(bis, list);

            salaryResultStaffItemService.importItemValueAndRecalculateStaffPayslip(list);


//            int size = list.size();
//            for (int i = 0; i < size; i++) {
//            	ImportSalaryResultStaffDto itemDto = list.get(i);
//                // xu ly List<ImportSalaryResultStaffDto>
//            	//salaryResultStaffService.handleListImportSalaryResultStaff(list);
//            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/calculate-salary-staffs", method = RequestMethod.POST)
    public ResponseEntity<SalaryResultDto> calculateSalaryStaffs(@RequestBody SearchSalaryResultDto dto) {
        SalaryResultDto response = salaryResultStaffService.calculateSalaryStaffs(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/export-calculate-salary-by-staffs", method = RequestMethod.POST)
    public ResponseEntity<?> exportCalculateSalaryStaffsToExcel(HttpServletResponse response, @RequestBody SearchSalaryResultStaffDto dto) throws IOException {
        SalaryResultDto data = salaryResultService.searchSalaryResultBoard(dto);

        if (data != null) {
            ByteArrayResource excelFile = ExportExcelUtil.exportExcelSalaryResult(data);

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.addHeader("Content-Disposition", "attachment; filename=Mau_Bang_Luong.xlsx");
            org.apache.commons.io.IOUtils.copy(excelFile.getInputStream(), response.getOutputStream());

            return ResponseEntity.ok(data); // Trả về entity nếu thành công
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Tính toán mới phiếu lương cho nhân viên, nếu phiếu lương đã tồn tại tại thì tính toán lại
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/calculate-salary-staff")
    public ResponseEntity<SalaryResultStaffDto> calculateSalaryStaff(@RequestBody SalaryResultStaffDto dto) {
        SalaryResultStaffDto response = salaryResultStaffService.calculateSalaryStaff(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        response = salaryResultStaffService.handleSetFullSalaryTemplate(response);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Tính toán lại phiếu lương cho nhân viên
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/update-salary-staff")
    public ResponseEntity<SalaryResultStaffDto> updateSalaryStaff(
            @RequestBody SalaryResultStaffDto dto) {
        SalaryResultStaffDto response = salaryResultStaffService.recalculateSalaryStaff(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        response = salaryResultStaffService.handleSetFullSalaryTemplate(response);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
