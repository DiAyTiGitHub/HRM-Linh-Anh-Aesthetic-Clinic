package com.globits.timesheet.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.HrAdministrativeUnitDto;
import com.globits.hr.dto.RecruitmentPlanDto;
import com.globits.hr.dto.search.SearchAdministrativeUnitDto;
import com.globits.hr.utils.ExportExcelUtil;
import com.globits.hr.utils.ImportExportExcelUtil;
import com.globits.timesheet.dto.LeaveRequestDto;
import com.globits.timesheet.dto.search.ExistLeaveRequestInPeriodDto;
import com.globits.timesheet.dto.search.LeaveRequestSearchDto;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import com.globits.timesheet.service.LeaveRequestService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leave-request")
public class RestLeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @RequestMapping(method = RequestMethod.POST, path = "/save-or-update")
    public ResponseEntity<LeaveRequestDto> saveOrUpdate(@RequestBody LeaveRequestDto dto) {
        LeaveRequestDto response = leaveRequestService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(path = "/search-by-page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<LeaveRequestDto>> searchByPage(@RequestBody LeaveRequestSearchDto dto) {
        Page<LeaveRequestDto> page = leaveRequestService.searchByPage(dto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping(value = "/initial-filter")
    public ResponseEntity<?> getInitialFilter() {
        LeaveRequestSearchDto response = leaveRequestService.getInitialFilter();

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-by-id/{id}", method = RequestMethod.GET)
    public ResponseEntity<LeaveRequestDto> getById(@PathVariable("id") UUID id) {
        LeaveRequestDto response = leaveRequestService.getById(id);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Boolean> remove(@PathVariable("id") UUID id) {
        boolean isDeleted = leaveRequestService.deleteById(id);
        return isDeleted
                ? ResponseEntity.ok(true)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }

    @RequestMapping(path = "/delete-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        boolean isDeletedMultiple = leaveRequestService.deleteMultiple(ids);
        return isDeletedMultiple
                ? ResponseEntity.ok(true)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }

    @RequestMapping(value = "/update-requests-approval-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UUID>> updateRequestsApprovalStatus(@RequestBody LeaveRequestSearchDto searchDto) {
        List<UUID> updatedRequestIds = leaveRequestService.updateRequestsApprovalStatus(searchDto);
        if (updatedRequestIds != null && updatedRequestIds.size() > 0)
            return new ResponseEntity<>(updatedRequestIds, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/is-exist-leave-request-in-period")
    ApiResponse<List<UUID>> isExistLeaveRequestInPeriod(@RequestBody ExistLeaveRequestInPeriodDto request) {
        return leaveRequestService.isExistLeaveRequestInPeriod(request.getStaffIds(), request.getFromDate(), request.getToDate());
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/unpaid-leave/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> generateUnpaidLeaveDocx(HttpServletResponse response, @PathVariable("id") UUID id) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document;charset=UTF-8";
        final String FILE_EXTENSION = ".docx";

        try {
            response.setContentType(CONTENT_TYPE);
            response.setHeader("Content-Disposition", "attachment; filename=" + "DON_XIN_NGHI_PHEP_KHONG_LUONG" + FILE_EXTENSION);
            // Ghi file DOCX vào response
            XWPFDocument document = leaveRequestService.generateUnpaidLeaveDocx(id);
            if (document == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy yêu cầu nghỉ phép.");
            }
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                document.write(outputStream);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi khi tạo file DOCX.");
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/paid-leave/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> generatePaidLeaveXlsx(HttpServletResponse response, @PathVariable("id") UUID id) {
        ByteArrayResource excelFile;
        LeaveRequestDto data = leaveRequestService.getById(id);
        if (data != null) {
            try {
                // Cấu hình response header
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.addHeader("Content-Disposition", "attachment; filename=DON_XIN_NGHI_PHEP_CO_LUONG.xlsx");

                InputStream inputStream = new ClassPathResource("Excel/DON_XIN_NGHI_PHEP_CO_LUONG.xlsx").getInputStream();
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

                excelFile = ExportExcelUtil.generatePaidLeaveXlsx(data, workbook);
                if (excelFile == null) {
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }

                InputStream ins = null;
                if (excelFile != null) {
                    ins = new ByteArrayInputStream(excelFile.getByteArray());
                }
                if (ins != null) {
                    org.apache.commons.io.IOUtils.copy(ins, response.getOutputStream());
                }
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
            }
        } else {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/export-excel-leave-request-template")
    public void exportExcelRecruitmentPlanTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_DANH_SACH_NGHI_PHEP.xlsx");

        try (InputStream inputStream = new ClassPathResource("Excel/MAU_DANH_SACH_NGHI_PHEP.xlsx").getInputStream()) {
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/import-excel-leave-request-template", method = RequestMethod.POST)
    public ResponseEntity<?> importLeaveRequestFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<LeaveRequestDto> list = ImportExportExcelUtil.readLeaveRequestFromFile(bis);
            if (list == null || list.isEmpty()) {
                return new ResponseEntity<>("File import không có dữ liệu.", HttpStatus.BAD_REQUEST);
            }
            if (list.get(0) != null && list.get(0).getErrorMessage() != null) {
                return new ResponseEntity<>(list.get(0).getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
            Integer countSave = leaveRequestService.saveListLeaveRequest(list);
            return new ResponseEntity<>(countSave, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
