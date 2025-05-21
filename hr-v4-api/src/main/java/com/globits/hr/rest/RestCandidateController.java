package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Candidate;
import com.globits.hr.dto.*;
import com.globits.hr.dto.ExistingCandidatesDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.importExcel.CandidateImport;
import com.globits.hr.dto.search.ExistingCandidatesSearchDto;
import com.globits.hr.dto.search.SearchCandidateDto;
import com.globits.hr.dto.search.SearchRecruitmentDto;
import com.globits.hr.service.CandidateService;
import com.globits.hr.service.SendMailCandidateService;
import com.globits.hr.utils.ImportExportExcelUtil;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/candidate")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestCandidateController {
    @Autowired
    private CandidateService candidateService;

    @Autowired
    private SendMailCandidateService sendMailCandidateService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/saveCandidate")
    public ResponseEntity<CandidateDto> saveCandidate(@RequestBody CandidateDto dto) {
        // candidate's code is duplicated
        Boolean isValidCode = candidateService.isValidCode(dto);
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<CandidateDto>(dto, HttpStatus.CONFLICT);
        }
        CandidateDto response = candidateService.saveCandidate(dto);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<CandidateDto> deleteCandidate(@PathVariable("id") UUID id) {
        CandidateDto res = candidateService.deleteCandidate(id);
        if (res == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<CandidateDto> getById(@PathVariable("id") UUID id) {
        CandidateDto result = candidateService.getById(id);
        if (result == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/ge-candidates-by-recruitment-plan/{recruitmentPlanId}", method = RequestMethod.GET)
    public ResponseEntity<List<CandidateDto>> getCandidatesByRecruitmentPlanId(@PathVariable("recruitmentPlanId") UUID recruitmentPlanId) {
        List<CandidateDto> result = candidateService.getCandidatesByRecruitmentPlanId(recruitmentPlanId);
        if (result == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/export/{candidateId}", method = RequestMethod.GET)
    public ResponseEntity<?> exportDocx(HttpServletResponse response, @PathVariable("candidateId") UUID candidateId) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document;charset=UTF-8";
        final String FILE_EXTENSION = ".docx";

        try {
            // Lấy dữ liệu hợp đồng lao động từ Service
            CandidateDto candidateDto = candidateService.getById(candidateId);
            if (candidateDto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thông tin ứng viên cho hợp đồng này.");
            }

            // Lấy thông tin nhân viên từ hợp đồng
            String employeeName = candidateDto.getDisplayName();
            XWPFDocument document = candidateService.generateDocx(candidateDto);

            // Tạo tên file: "Hợp đồng lao động - Tên nhân viên.docx"
            String fileName = generateFileName("HopDongLaoDong", employeeName);

            // Thiết lập header response để tải file DOCX
            setupResponseHeaders(response, fileName, CONTENT_TYPE, FILE_EXTENSION);

            // Ghi file DOCX vào response
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                document.write(outputStream);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi khi tạo file DOCX.");
        }
    }

    private void setupResponseHeaders(HttpServletResponse response, String fileName, String contentType, String extension) {
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + extension);
    }

    private String generateFileName(String baseName, String employeeName) {
        if (employeeName == null) {
            return baseName;
        }
        return baseName + " - " + employeeName.replaceAll("\\s+", "_");
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = candidateService.deleteMultiple(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    // danh sách ứng viên
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/pagingCandidates", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CandidateDto>> pagingCandidates(@RequestBody SearchCandidateDto searchDto) {
        Page<CandidateDto> page = candidateService.pagingCandidates(searchDto);
        if (page == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    // danh sách ứng viên duoc tham gia buoi phong van/kiem tra
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/paging-exam-candidates", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CandidateDto>> pagingExamCandidates(@RequestBody SearchCandidateDto searchDto) {
        Page<CandidateDto> page = candidateService.pagingExamCandidates(searchDto);
        if (page == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    // danh sách ứng viên da PASS bai kiem tra/phong van
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/paging-passed-candidates", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CandidateDto>> pagingPassedCandidates(@RequestBody SearchCandidateDto searchDto) {
        Page<CandidateDto> page = candidateService.pagingPassedCandidates(searchDto);
        if (page == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    // danh sách ứng viên Chờ nhận việc
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/paging-waiting-job-candidates", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CandidateDto>> pagingWaitingJobCandidates(@RequestBody SearchCandidateDto searchDto) {
        Page<CandidateDto> page = candidateService.pagingWaitingJobCandidates(searchDto);
        if (page == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    // Danh sách ứng viên Không đến nhận việc
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/paging-not-come-candidates", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CandidateDto>> pagingNotComeCandidates(@RequestBody SearchCandidateDto searchDto) {
        Page<CandidateDto> page = candidateService.pagingNotComeCandidates(searchDto);
        if (page == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    // danh sách ứng viên ĐÃ nhận việc
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/paging-onboarded-candidates", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<CandidateDto>> pagingOnboardedCandidates(@RequestBody SearchCandidateDto searchDto) {
        Page<CandidateDto> page = candidateService.pagingOnboardedCandidates(searchDto);
        if (page == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    // trang thai ho so cua ung vien
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/updateApprovalStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateApprovalStatus(@RequestBody SearchCandidateDto dto) {
        try {
            Boolean isUpdated = candidateService.updateApprovalStatus(dto);
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
    @RequestMapping(value = "/update-status", method = RequestMethod.POST)
    public ResponseEntity<Boolean> updateStatus(@RequestBody SearchCandidateDto dto) {
        try {
            Boolean isUpdated = candidateService.updateStatus(dto);
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

    // trang thai bai kiem tra/phong van cua ung vien
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update-exam-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateExamStatus(@RequestBody SearchCandidateDto dto) {
        try {
            Boolean isUpdated = candidateService.updateExamStatus(dto);
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

    // Cập nhật trạng thái tiếp nhận ứng viên
    // (trạng thái tiếp nhận ứng viên có giá trị sau khi ứng viên đã thi PASS bài
    // phỏng vấn/thi tuyển của đợt tuyển dụng)
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/update-reception-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateReceptionStatus(@RequestBody SearchCandidateDto dto) {
        try {
            Boolean isUpdated = candidateService.updateReceptionStatus(dto);
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

    // chuyen ung vien sang Chờ nhận việc
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/convert-to-waiting-job", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> convertToWaitingJob(@RequestBody SearchCandidateDto dto) {
        try {
            Boolean isUpdated = candidateService.convertToWaitingJob(dto);
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

    // Chuyển ứng viên sang Không tới nhận việc
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/convert-to-not-come", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> convertToNotCome(@RequestBody SearchCandidateDto dto) {
        try {
            Boolean isUpdated = candidateService.convertToNotCome(dto);
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

    // Chuyển ứng viên sang Đã nhận việc
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/convert-to-received-job", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<StaffDto> convertToReceivedJob(@RequestBody List<CandidateDto> dto) {
        return candidateService.convertToReceivedJob(dto);
    }

    @RequestMapping(path = "/existing-candidates", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExistingCandidatesDto> existingCandidates(@RequestBody ExistingCandidatesSearchDto searchDto) {
        ExistingCandidatesDto item = candidateService.existingCandidates(searchDto);
        if (item == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @RequestMapping(path = "/exist-candidate-profile-of-staff/{staffId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CandidateDto>> getExistCandidateProfileOfStaff(@PathVariable("staffId") UUID staffId) {
        List<CandidateDto> item = candidateService.getExistCandidateProfileOfStaff(staffId);
        if (item == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }


    @PostMapping("/export-excel-candidate-template")
    public void exportExcelCandidateTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_HO_SO_UNG_VIEN.xlsx");

        try (InputStream inputStream = new ClassPathResource("Excel/MAU_HO_SO_UNG_VIEN.xlsx").getInputStream()) {
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/import-excel-candidate", method = RequestMethod.POST)
    public ResponseEntity<?> importCandidateFromInputStream(HttpServletResponse response, @RequestParam("uploadfile") MultipartFile uploadfile) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            CandidateImport list = ImportExportExcelUtil.readCandidateFromFile(bis);
            if (list.getCandidates() == null || list.getCandidates().isEmpty() && (list.getErrors() != null && list.getErrors().isEmpty())) {
                return new ResponseEntity<>("File import không có dữ liệu.", HttpStatus.BAD_REQUEST);
            }
            if (list.getErrors() == null || list.getErrors().isEmpty()) {
                candidateService.saveListCandidate(list.getCandidates());
            }
            try {
                Workbook workbook = candidateService.exportExcelCandidateReports(list);

                setupResponseHeaders(response, "BaoCaoImportUngVien", CONTENT_TYPE, FILE_EXTENSION);

                try (ServletOutputStream outputStream = response.getOutputStream()) {
                    workbook.write(outputStream);
                }
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/send-mail")
    public ApiResponse<Boolean> sendMail(@RequestBody SendMailCandidateDto dto) throws TemplateException, MessagingException, IOException {
        return sendMailCandidateService.sendMail(dto);
    }

    @PostMapping(value = "/send-mail-edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Boolean> sendMailEdit(
            @RequestPart("dto") SendMailCandidateDto dto,
            @RequestParam MultiValueMap<String, MultipartFile> fileMap
    ) throws TemplateException, MessagingException, IOException {
        List<MultipartFile> files = fileMap.get("attachedFileTemplate");
        for (int i = 0; i < dto.getCandidate().size(); i++) {
            CandidateDto candidate = dto.getCandidate().get(i);
            String key = String.valueOf(i);
            List<MultipartFile> filesForCandidate = fileMap.get(key);
            if (!CollectionUtils.isEmpty(files)) {
                if (filesForCandidate != null) {
                    filesForCandidate.addAll(files);
                    candidate.setFiles(filesForCandidate);
                } else {
                    filesForCandidate = files;
                }
            }
            candidate.setFiles(filesForCandidate);
        }
        return sendMailCandidateService.sendMailEdit(dto);
    }


    @RequestMapping(path = "/export-excel-recruitment-reports", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exportExcelRecruitmentReports(HttpServletResponse response, @RequestBody SearchCandidateDto dto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            Workbook workbook = candidateService.exportExcelRecruitmentReports(dto);

            setupResponseHeaders(response, "BAO_CAO_TUYEN_DUNG", CONTENT_TYPE, FILE_EXTENSION);

            try (ServletOutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
        }
    }

    @PostMapping(value = "/approve-cv")
    public ApiResponse<Boolean> approveCv(@RequestBody SearchCandidateDto dto) {
        ApiResponse<Boolean> response = candidateService.approveCv(dto);
        return response;
    }

    @GetMapping(value = "/auto-gen-code/{configKey}")
    public ResponseEntity<?> autoGenCode(@PathVariable("configKey") String configKey) {
        return new ResponseEntity<>(candidateService.autoGenerateCode(configKey), HttpStatus.OK);
    }

    @PostMapping("/get-preview-mail")
    public ApiResponse<String> getPreview(@RequestBody SendMailCandidateDto dto) throws TemplateException, MessagingException, IOException {
        return sendMailCandidateService.getMailPreview(dto);
    }

    @PutMapping("/resign-multiple")
    public ResponseEntity<Boolean> resignMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = candidateService.resignMultiple(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }
}
