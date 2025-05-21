package com.globits.hr.rest;


import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.RecruitmentRequestDto;
import com.globits.hr.dto.importExcel.RecruitmentRequestReportDto;
import com.globits.hr.dto.search.RecruitmentRequestSummarySearch;
import com.globits.hr.dto.search.SearchRecruitmentDto;
import com.globits.hr.projection.RecruitmentRequestSummary;
import com.globits.hr.service.RecruitmentRequestService;
import com.globits.hr.utils.ImportExportExcelUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recruitment-request")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestRecruitmentRequestController {
    @Autowired
    private RecruitmentRequestService recruitmentRequestService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/save-or-update")
    public ApiResponse<RecruitmentRequestDto> saveRecruitmentRequest(@RequestBody RecruitmentRequestDto dto) {
        // RecruitmentRequest's code is duplicated
        Boolean isValidCode = recruitmentRequestService.isValidCode(dto);
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ApiResponse<RecruitmentRequestDto>(HttpStatus.CONFLICT.value(), "Mã trùng", null);
        }
        ApiResponse<RecruitmentRequestDto> response = recruitmentRequestService.saveRecruitmentRequest(dto);
        return response;
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/search-by-page", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<RecruitmentRequestDto>> pagingRecruitmentRequest(@RequestBody SearchRecruitmentDto searchDto) {
        Page<RecruitmentRequestDto> page = recruitmentRequestService.pagingRecruitmentRequest(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/remove/{id}")
    public ResponseEntity<Boolean> deleteRecruitmentRequest(@PathVariable("id") UUID id) {
        Boolean res = recruitmentRequestService.deleteRecruitmentRequest(id);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/get-by-id/{id}", method = RequestMethod.GET)
    public ResponseEntity<RecruitmentRequestDto> getById(@PathVariable("id") UUID id) {
        RecruitmentRequestDto result = recruitmentRequestService.getById(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/remove-multiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = recruitmentRequestService.deleteMultipleRecruitmentRequest(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update-request-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<Boolean> updateRequestsStatus(@RequestBody SearchRecruitmentDto searchDto) {
        ApiResponse<Boolean> response = recruitmentRequestService.updateRequestsStatus(searchDto);
        return response;
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/person-in-charge", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<List<RecruitmentRequestDto>> personInCharge(@RequestBody SearchRecruitmentDto searchDto) {
        ApiResponse<List<RecruitmentRequestDto>> response = recruitmentRequestService.personInCharge(searchDto);
        return response;
    }

    @GetMapping("/approve-recruitment-request/{recruitmentRequestId}/{approved}")
    public ApiResponse<Boolean> approveRecruitmentRequest(@PathVariable UUID recruitmentRequestId, @PathVariable Boolean approved) {
        return recruitmentRequestService.approveRecruitmentRequest(recruitmentRequestId, approved);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/export/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> exportDocx(HttpServletResponse response, @PathVariable("id") UUID id) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document;charset=UTF-8";
        final String FILE_EXTENSION = ".docx";

        try {
            response.setContentType(CONTENT_TYPE);
            response.setHeader("Content-Disposition", "attachment; filename=" + "PHIEU_DE_XUAT_TUYEN_DUNG" + FILE_EXTENSION);
            // Ghi file DOCX vào response
            XWPFDocument document = recruitmentRequestService.generateDocx(id);
            if (document == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy yêu cầu tuyển dụng.");
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
    @PostMapping("/export-excel")
    public ResponseEntity<?> exportExcelDepartmentData(HttpServletResponse response, @RequestBody SearchRecruitmentDto dto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            Workbook workbook = recruitmentRequestService.exportExcel(dto);

            setupResponseHeaders(response, generateFileName("DuLieuRecruitment"), CONTENT_TYPE, FILE_EXTENSION);
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
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
    @RequestMapping(value = "/paging-recruitment-request-report", method = RequestMethod.POST)
    public ResponseEntity<Page<RecruitmentRequestReportDto>> pagingRecruitmentRequestReport(@RequestBody SearchRecruitmentDto searchDto) {
        Page<RecruitmentRequestReportDto> response = recruitmentRequestService.pagingRecruitmentRequestReport(searchDto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/export-recruitment-request-report", method = RequestMethod.POST)
    public ResponseEntity<?> exportRecruitmentRequestReport(HttpServletResponse response, @RequestBody SearchRecruitmentDto dto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            Workbook workbook = recruitmentRequestService.exportRecruitmentRequestReport(dto);
            setupResponseHeaders(response, generateFileName("BAO_CAO_THEO_YC_TUYEN_DUNG"), CONTENT_TYPE, FILE_EXTENSION);
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
        }
    }

    @PostMapping("/export-excel-recruitment-request-template")
    public void exportExcelCandidateTemplate(HttpServletResponse response) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_YEU_CAU_TUYEN_DUNG.xlsx");

        try (InputStream inputStream = new ClassPathResource("Excel/MAU_YEU_CAU_TUYEN_DUNG_IMPORT.xlsx").getInputStream()) {
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/import-excel-recruitment-request", method = RequestMethod.POST)
    public ResponseEntity<?> importCandidateFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<RecruitmentRequestDto> list = ImportExportExcelUtil.readRecruitmentRequestFromFile(bis);
            if (list == null || list.isEmpty()) {
                return new ResponseEntity<>("File import không có dữ liệu.", HttpStatus.BAD_REQUEST);
            }
            if (list.get(0) != null && list.get(0).getErrorMessage() != null) {
                return new ResponseEntity<>(list.get(0).getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
            Integer countSave = recruitmentRequestService.saveListRecruitmentRequest(list);
            return new ResponseEntity<>(countSave, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/check-number-is-within-headcount/{departmentId}/{positionTileId}/{announcementQuantity}")
    public ApiResponse<Boolean> checkNumberIsWithinHeadcount(@PathVariable UUID departmentId,@PathVariable UUID positionTileId,@PathVariable Integer announcementQuantity) {
        return recruitmentRequestService.checkNumberIsWithinHeadcount(departmentId, positionTileId, announcementQuantity);
    }

    @GetMapping(value = "/auto-gen-code/{configKey}")
    public ResponseEntity<?> autoGenCode(@PathVariable("configKey") String configKey) {
        return new ResponseEntity<>(recruitmentRequestService.autoGenerateCode(configKey), HttpStatus.OK);
    }

    @PostMapping("/change-status/{status}")
    private ApiResponse<Boolean> changeStatus(@RequestBody List<UUID> ids,@PathVariable("status") HrConstants.RecruitmentRequestStatus status) {
        return recruitmentRequestService.changeStatus(ids, status);

    }

    @PostMapping("/recruitment-request-summary")
    public ApiResponse<Page<RecruitmentRequestSummary>> getRecruitmentRequestSummaries(@RequestBody RecruitmentRequestSummarySearch dto) {
        return new ApiResponse<Page<RecruitmentRequestSummary>>(HttpStatus.OK.value(), "Oke", recruitmentRequestService.getRecruitmentRequestSummaries(dto));
    }
}
