package com.globits.timesheet.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.AbsenceRequestDto;
import com.globits.hr.dto.search.AbsenceRequestSearchDto;
import com.globits.hr.service.AbsenceRequestService;
import com.globits.timesheet.dto.OvertimeRequestDto;
import com.globits.timesheet.dto.search.SearchOvertimeRequestDto;
import com.globits.timesheet.service.OvertimeRequestService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
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
@RequestMapping("/api/overtime-request")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestOvertimeRequestController {

    @Autowired
    private OvertimeRequestService service;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/search-by-page", method = RequestMethod.POST)
    public ResponseEntity<Page<OvertimeRequestDto>> pagingOvertimeRequest(@RequestBody SearchOvertimeRequestDto dto) {
        Page<OvertimeRequestDto> result = service.pagingOvertimeRequest(dto);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/initial-filter")
    public ResponseEntity<?> getInitialFilter() {
        SearchOvertimeRequestDto response = service.getInitialFilter();

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public Boolean deleteById(@PathVariable UUID id) {
        return service.deleteById(id);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/get-by-id/{id}", method = RequestMethod.GET)
    public ResponseEntity<OvertimeRequestDto> getAbsenceRequestById(@PathVariable UUID id) {
        OvertimeRequestDto result = service.getById(id);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/save-or-update", method = RequestMethod.POST)
    public OvertimeRequestDto saveOvertimeRequest(@RequestBody OvertimeRequestDto dto) {
        return service.saveOrUpdate(dto);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update-requests-approval-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> updateRequestsApprovalStatus(@RequestBody List<OvertimeRequestDto> dtos) {
        Integer updatedRequests = service.updateRequestsApprovalStatus(dtos);
        if (updatedRequests != null)
            return new ResponseEntity<>(updatedRequests, HttpStatus.OK);
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, value = "/delete-multiple", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> listId) {
        Boolean deleted = service.deleteMultiple(listId);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }


    // Tải mẫu nhập YC làm thêm giờ
    @RequestMapping(value = "/download-OT-import-template", method = RequestMethod.GET)
    public void downloadOTImportTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_YEU_CAU_XAC_NHAN_LAM_THEM_GIO.xlsx");

        try (InputStream inputStream = new ClassPathResource("Excel/MAU_YEU_CAU_XAC_NHAN_LAM_THEM_GIO.xlsx").getInputStream()) {
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/import-excel-overtime-request", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<?> importExcelOvertimeRequest(@RequestParam("upload") MultipartFile file, HttpServletResponse response) {
        try {
            // Gọi service xử lý và nhận file output
            ByteArrayOutputStream excelOutput = service.importExcelOvertimeRequest(file);

            // Thiết lập response trả về file Excel
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=Ket_qua_nhap_YC_xac_nhan_gio_lam_them.xlsx");

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
