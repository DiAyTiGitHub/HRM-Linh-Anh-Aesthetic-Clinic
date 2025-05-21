package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.StaffLabourAgreementDto;
import com.globits.hr.dto.search.SearchStaffLabourAgreementDto;
import com.globits.hr.service.StaffLabourAgreementService;
import com.globits.hr.utils.ExportExcelUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/staff-labour-agreement")
public class RestStaffLabourAgreementController {
    private static final Logger logger = LoggerFactory.getLogger(RestStaffLabourAgreementController.class);

    @Autowired
    private StaffLabourAgreementService staffLabourAgreementService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/save-labour-agreement")
    public ResponseEntity<StaffLabourAgreementDto> saveStaffLabourAgreement(@RequestBody StaffLabourAgreementDto dto) {
        StaffLabourAgreementDto response = staffLabourAgreementService.saveOrUpdate(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{agreementId}", method = RequestMethod.GET)
    public ResponseEntity<StaffLabourAgreementDto> getById(@PathVariable("agreementId") UUID rankTitleId) {
        StaffLabourAgreementDto response = staffLabourAgreementService.getById(rankTitleId);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/export/{agreementId}", method = RequestMethod.GET)
    public ResponseEntity<?> exportDocx(HttpServletResponse response, @PathVariable("agreementId") UUID agreementId) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document;charset=UTF-8";
        final String FILE_EXTENSION = ".docx";

        try {
            // Lấy dữ liệu hợp đồng lao động từ Service
            StaffLabourAgreementDto agreementDto = staffLabourAgreementService.getById(agreementId);
            if (agreementDto == null || agreementDto.getStaff() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thông tin nhân viên cho hợp đồng này.");
            }

            // Lấy thông tin nhân viên từ hợp đồng

            String staffName = agreementDto.getStaff().getDisplayName();
            String staffCode = agreementDto.getStaff().getStaffCode();
            String contractType = null;
            if (agreementDto.getContractType() != null) {
                contractType = agreementDto.getContractType().getName();
            }

            XWPFDocument document = staffLabourAgreementService.generateDocx(agreementDto);


            // Tạo tên file: "Hợp đồng lao động - Tên nhân viên.docx"
            String fileName = generateFileNameToUpperCase(contractType) + "_" + generateFileNameToUpperCase(staffName) + "_" + generateFileNameToUpperCase(staffCode);

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
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition"); // Cho phép frontend đọc header này
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{agreementId}")
    public ResponseEntity<StaffLabourAgreementDto> deleteById(@PathVariable("agreementId") UUID rankTitleId) {
        StaffLabourAgreementDto response = staffLabourAgreementService.deleteById(rankTitleId);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> agreementIds) {
        Boolean deleted = staffLabourAgreementService.deleteMultiple(agreementIds);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/pagingLabourAgreement", method = RequestMethod.POST)
    public ResponseEntity<Page<StaffLabourAgreementDto>> pagingLabourAgreement(@RequestBody SearchStaffLabourAgreementDto searchDto) {
        Page<StaffLabourAgreementDto> page = staffLabourAgreementService.pagingLabourAgreement(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-has-insurance-staff")
    public ResponseEntity<?> handleExcel(HttpServletResponse response, @RequestBody SearchStaffLabourAgreementDto dto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            dto.setHasSocialIns(true);
            Workbook workbook = staffLabourAgreementService.handleExcel(dto);

            setupResponseHeaders(response, generateFileName("TongHopBaoHiemNhanVien"), CONTENT_TYPE, FILE_EXTENSION);

            try (ServletOutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
        }
    }

//    private void setupResponseHeaders(HttpServletResponse response, String fileName, String contentType, String extension) {
//        response.setContentType(contentType);
//        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + extension);
//    }

    private String generateFileName(String baseName) {
        String dateTimeString = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        return baseName + "-" + dateTimeString;
    }

    // Dòng tổng tiền trong bảng danh sách đóng bảo hiểm
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, value = "/get-total-has-social-ins")
    public ResponseEntity<StaffLabourAgreementDto> getTotalHasSocialIns(@RequestBody SearchStaffLabourAgreementDto searchDto) {
        StaffLabourAgreementDto sumIns = staffLabourAgreementService.getTotalHasSocialIns(searchDto);
        return ResponseEntity.ok(sumIns);
    }


    @GetMapping("/export-hic-info-to-word/{staffId}")
    public void exportHICInfoToWord(HttpServletResponse response, @PathVariable UUID staffId) {
        try {
            staffLabourAgreementService.exportHICInfoToWord(response, staffId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping(value = "/export-excel-staff-social-insurance-by-type")
    public ResponseEntity<?> exportExcelStaffSIByType(@RequestBody SearchStaffLabourAgreementDto dto) {
        try {
            List<StaffLabourAgreementDto> dataList = staffLabourAgreementService.getAllStaffLabourAgreementWithSearch(dto);

            if (dataList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            InputStream inputStream = new ClassPathResource("Excel/XuatDanhsachBHXH.xlsx").getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            ByteArrayResource excelFile = ExportExcelUtil.handleExcelStaffLabourAgreements(dataList, dto, workbook);

            if (excelFile == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=XuatDanhsachBHXH.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelFile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/check-overdue-contract", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> checkOverdueContract(@RequestBody SearchStaffLabourAgreementDto searchDto) {
        Boolean result = staffLabourAgreementService.checkOverdueContract(searchDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @GetMapping(path = "/get-last-labour-agreement/{staffId}")
    public ApiResponse<StaffLabourAgreementDto> getLastLabourAgreement(@PathVariable UUID staffId) {
        return staffLabourAgreementService.getLastLabourAgreement(staffId);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/import-excel-staff-labour-agreement", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importStaffLabourAgreementFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        long startTime = System.nanoTime();

        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<StaffLabourAgreementDto> importResults = staffLabourAgreementService.readDataFromExcel(bis);

            if (importResults == null || importResults.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            if (importResults.get(0).getErrorMessage() != null) {
                return new ResponseEntity<>(importResults.get(0).getErrorMessage(), HttpStatus.BAD_REQUEST);
            }

            Integer result = staffLabourAgreementService.saveStaffLabourAgreementImportFromExcel(importResults);
            long endTime = System.nanoTime();
            long totalDuration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
            logger.info("Total import process time: {} ms ({} seconds)",
                    totalDuration,
                    String.format("%.2f", totalDuration / 1000.0)
            );
            if (result == null) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("result", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/export-excel-staff-labour-agreement-template")
    public void exportExcelStaffLabourAgreementTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=HOP_DONG.xlsx");

        try (InputStream inputStream = new ClassPathResource("Excel/HOP_DONG.xlsx").getInputStream()) {
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/export-excel-staff-labour-agreement", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void exportExcelStaffLabourAgreementData(@RequestBody SearchStaffLabourAgreementDto searchDto, HttpServletResponse response) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            Workbook workbook = staffLabourAgreementService.exportExcelStaffLabourAgreement(searchDto);
            setupResponseHeaders(response, generateFileName("HOP_DONG"), CONTENT_TYPE, FILE_EXTENSION);
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
    }

    private String generateFileNameToUpperCase(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        // Ánh xạ các ký tự đặc biệt tiếng Việt
        Map<Character, Character> vietnameseCharMap = new HashMap<>();
        vietnameseCharMap.put('à', 'a');
        vietnameseCharMap.put('À', 'A');
        vietnameseCharMap.put('á', 'a');
        vietnameseCharMap.put('Á', 'A');
        vietnameseCharMap.put('ả', 'a');
        vietnameseCharMap.put('Ả', 'A');
        vietnameseCharMap.put('ã', 'a');
        vietnameseCharMap.put('Ã', 'A');
        vietnameseCharMap.put('ạ', 'a');
        vietnameseCharMap.put('Ạ', 'A');
        vietnameseCharMap.put('ă', 'a');
        vietnameseCharMap.put('Ă', 'A');
        vietnameseCharMap.put('ằ', 'a');
        vietnameseCharMap.put('Ằ', 'A');
        vietnameseCharMap.put('ắ', 'a');
        vietnameseCharMap.put('Ắ', 'A');
        vietnameseCharMap.put('ẳ', 'a');
        vietnameseCharMap.put('Ẳ', 'A');
        vietnameseCharMap.put('ẵ', 'a');
        vietnameseCharMap.put('Ẵ', 'A');
        vietnameseCharMap.put('ặ', 'a');
        vietnameseCharMap.put('Ặ', 'A');
        vietnameseCharMap.put('â', 'a');
        vietnameseCharMap.put('Â', 'A');
        vietnameseCharMap.put('ầ', 'a');
        vietnameseCharMap.put('Ầ', 'A');
        vietnameseCharMap.put('ấ', 'a');
        vietnameseCharMap.put('Ấ', 'A');
        vietnameseCharMap.put('ẩ', 'a');
        vietnameseCharMap.put('Ẩ', 'A');
        vietnameseCharMap.put('ẫ', 'a');
        vietnameseCharMap.put('Ẫ', 'A');
        vietnameseCharMap.put('ậ', 'a');
        vietnameseCharMap.put('Ậ', 'A');
        vietnameseCharMap.put('è', 'e');
        vietnameseCharMap.put('È', 'E');
        vietnameseCharMap.put('é', 'e');
        vietnameseCharMap.put('É', 'E');
        vietnameseCharMap.put('ẻ', 'e');
        vietnameseCharMap.put('Ẻ', 'E');
        vietnameseCharMap.put('ẽ', 'e');
        vietnameseCharMap.put('Ẽ', 'E');
        vietnameseCharMap.put('ẹ', 'e');
        vietnameseCharMap.put('Ẹ', 'E');
        vietnameseCharMap.put('ê', 'e');
        vietnameseCharMap.put('Ê', 'E');
        vietnameseCharMap.put('ề', 'e');
        vietnameseCharMap.put('Ề', 'E');
        vietnameseCharMap.put('ế', 'e');
        vietnameseCharMap.put('Ế', 'E');
        vietnameseCharMap.put('ể', 'e');
        vietnameseCharMap.put('Ể', 'E');
        vietnameseCharMap.put('ễ', 'e');
        vietnameseCharMap.put('Ễ', 'E');
        vietnameseCharMap.put('ệ', 'e');
        vietnameseCharMap.put('Ệ', 'E');
        vietnameseCharMap.put('ì', 'i');
        vietnameseCharMap.put('Ì', 'I');
        vietnameseCharMap.put('í', 'i');
        vietnameseCharMap.put('Í', 'I');
        vietnameseCharMap.put('ỉ', 'i');
        vietnameseCharMap.put('Ỉ', 'I');
        vietnameseCharMap.put('ĩ', 'i');
        vietnameseCharMap.put('Ĩ', 'I');
        vietnameseCharMap.put('ị', 'i');
        vietnameseCharMap.put('Ị', 'I');
        vietnameseCharMap.put('ò', 'o');
        vietnameseCharMap.put('Ò', 'O');
        vietnameseCharMap.put('ó', 'o');
        vietnameseCharMap.put('Ó', 'O');
        vietnameseCharMap.put('ỏ', 'o');
        vietnameseCharMap.put('Ỏ', 'O');
        vietnameseCharMap.put('õ', 'o');
        vietnameseCharMap.put('Õ', 'O');
        vietnameseCharMap.put('ọ', 'o');
        vietnameseCharMap.put('Ọ', 'O');
        vietnameseCharMap.put('ô', 'o');
        vietnameseCharMap.put('Ô', 'O');
        vietnameseCharMap.put('ồ', 'o');
        vietnameseCharMap.put('Ồ', 'O');
        vietnameseCharMap.put('ố', 'o');
        vietnameseCharMap.put('Ố', 'O');
        vietnameseCharMap.put('ổ', 'o');
        vietnameseCharMap.put('Ổ', 'O');
        vietnameseCharMap.put('ỗ', 'o');
        vietnameseCharMap.put('Ỗ', 'O');
        vietnameseCharMap.put('ộ', 'o');
        vietnameseCharMap.put('Ộ', 'O');
        vietnameseCharMap.put('ơ', 'o');
        vietnameseCharMap.put('Ơ', 'O');
        vietnameseCharMap.put('ờ', 'o');
        vietnameseCharMap.put('Ờ', 'O');
        vietnameseCharMap.put('ớ', 'o');
        vietnameseCharMap.put('Ớ', 'O');
        vietnameseCharMap.put('ở', 'o');
        vietnameseCharMap.put('Ở', 'O');
        vietnameseCharMap.put('ỡ', 'o');
        vietnameseCharMap.put('Ỡ', 'O');
        vietnameseCharMap.put('ợ', 'o');
        vietnameseCharMap.put('Ợ', 'O');
        vietnameseCharMap.put('ù', 'u');
        vietnameseCharMap.put('Ù', 'U');
        vietnameseCharMap.put('ú', 'u');
        vietnameseCharMap.put('Ú', 'U');
        vietnameseCharMap.put('ủ', 'u');
        vietnameseCharMap.put('Ủ', 'U');
        vietnameseCharMap.put('ũ', 'u');
        vietnameseCharMap.put('Ũ', 'U');
        vietnameseCharMap.put('ụ', 'u');
        vietnameseCharMap.put('Ụ', 'U');
        vietnameseCharMap.put('ư', 'u');
        vietnameseCharMap.put('Ư', 'U');
        vietnameseCharMap.put('ừ', 'u');
        vietnameseCharMap.put('Ừ', 'U');
        vietnameseCharMap.put('ứ', 'u');
        vietnameseCharMap.put('Ứ', 'U');
        vietnameseCharMap.put('ử', 'u');
        vietnameseCharMap.put('Ử', 'U');
        vietnameseCharMap.put('ữ', 'u');
        vietnameseCharMap.put('Ữ', 'U');
        vietnameseCharMap.put('ự', 'u');
        vietnameseCharMap.put('Ự', 'U');
        vietnameseCharMap.put('ỳ', 'y');
        vietnameseCharMap.put('Ỳ', 'Y');
        vietnameseCharMap.put('ý', 'y');
        vietnameseCharMap.put('Ý', 'Y');
        vietnameseCharMap.put('ỷ', 'y');
        vietnameseCharMap.put('Ỷ', 'Y');
        vietnameseCharMap.put('ỹ', 'y');
        vietnameseCharMap.put('Ỹ', 'Y');
        vietnameseCharMap.put('ỵ', 'y');
        vietnameseCharMap.put('Ỵ', 'Y');
        vietnameseCharMap.put('đ', 'd');
        vietnameseCharMap.put('Đ', 'D');

        // Chuẩn hóa chuỗi và loại bỏ dấu
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");

        // Chuyển đổi các ký tự đặc biệt còn lại và viết thường
        StringBuilder resultWithSpecialChars = new StringBuilder();
        for (char c : normalized.toCharArray()) {
            if (vietnameseCharMap.containsKey(c)) {
                resultWithSpecialChars.append(vietnameseCharMap.get(c));
            } else {
                resultWithSpecialChars.append(c);
            }
        }

        // Tách từng từ và viết hoa chữ cái đầu
        String[] words = resultWithSpecialChars.toString().split("\\s+");
        StringBuilder finalResult = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                // Viết hoa chữ cái đầu tiên của mỗi từ
                finalResult.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    finalResult.append(word.substring(1).toLowerCase());
                }
            }
        }

        return finalResult.toString();
    }

}
