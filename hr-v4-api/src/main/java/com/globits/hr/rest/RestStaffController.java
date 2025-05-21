package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.core.dto.DepartmentTreeDto;
import com.globits.core.service.DepartmentService;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffWorkingLocation;
import com.globits.hr.dto.*;
import com.globits.hr.dto.function.ImportStaffDto;
import com.globits.hr.dto.importExcel.StaffLAImport;
import com.globits.hr.dto.importExcel.StaffLAImportResult;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.dto.staff.StaffLabourManagementDto;
import com.globits.hr.dto.staff.StaffLabourUtilReportDto;
import com.globits.hr.service.*;
import com.globits.hr.utils.ExportExcelUtil;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.search.SearchSalaryItemDto;
import com.globits.timesheet.dto.search.SearchStaffWorkScheduleDto;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestStaffController {
    @Autowired
    private StaffService staffService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private StaffServiceV3 staffServiceV3;

    @Autowired
    private StaffFamilyRelationshipService staffFamilyRelationshipService;

    @Autowired
    private PersonBankAccountService personBankAccountService;

    @Autowired
    private StaffWorkingLocationService staffWorkingLocationService;

    private static final Logger logger = LoggerFactory.getLogger(StaffDto.class);

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/dismiss-positions", method = RequestMethod.POST)
    public ResponseEntity<StaffDto> dismissPositions(@RequestBody StaffDto staff) {
        StaffDto result = staffService.dismissAllPositionsOfStaff(staff);
        return new ResponseEntity<>(result, result == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @RequestMapping(value = "/departmenttree", method = RequestMethod.GET)
    public List<DepartmentTreeDto> getTreeData() {
        return departmentService.getTreeData();
    }

    //    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @RequestMapping(value = "/{staffId}", method = RequestMethod.GET)
    public StaffDto getStaff(@PathVariable("staffId") UUID staffId) {
        if (staffId == null) {
            return null;
        }
        return staffService.getStaff(staffId);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteStaffs(@RequestBody Staff[] staffs) {
        Boolean deleted = staffService.deleteMultiple(staffs);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @RequestMapping(value = "/{pageIndex}/{pageSize}", method = RequestMethod.GET)
    public Page<StaffDto> getStaffs(@PathVariable int pageIndex, @PathVariable int pageSize) {
        return staffService.findByPageBasicInfo(pageIndex, pageSize);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public StaffDto saveStaff(@RequestBody StaffDto staff) {
        return staffService.createStaffAndAccountByCode(staff, null);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{staffId}", method = RequestMethod.PUT)
    public StaffDto updateItem(@PathVariable("staffId") UUID staffId, @RequestBody StaffDto staff) {
        return staffService.createStaffAndAccountByCode(staff, staffId);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{staffId}", method = RequestMethod.DELETE)
    public StaffDto removeStaff(@PathVariable("staffId") UUID staffId) {
        return staffService.deleteStaff(staffId);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @RequestMapping(value = "/department/{departmentId}/{pageIndex}/{pageSize}", method = RequestMethod.GET)
    public Page<PositionStaffDto> findTeacherByDepartment(@PathVariable("departmentId") UUID departmentId, @PathVariable int pageIndex, @PathVariable int pageSize) {
        return staffService.findTeacherByDepartment(departmentId, pageIndex, pageSize);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public ResponseEntity<List<StaffDto>> getAllStaffs() {
        List<StaffDto> staffs = staffService.getAll();
        return new ResponseEntity<>(staffs, HttpStatus.OK);
    }

    @RequestMapping(value = "/find/{pageIndex}/{pageSize}", method = RequestMethod.POST)
    public ResponseEntity<Page<StaffDto>> findStaffs(@RequestBody StaffSearchDto dto, @PathVariable int pageIndex, @PathVariable int pageSize) {
        Page<StaffDto> page = staffService.searchStaff(dto, pageSize, pageIndex);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/staffcode/{textSearch}/{pageIndex}/{pageSize}", method = RequestMethod.GET)
    public Page<StaffDto> getStaffsByCode(@PathVariable String textSearch, @PathVariable int pageIndex, @PathVariable int pageSize) {
        return staffService.findPageByCode(textSearch, pageIndex, pageSize);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<StaffDto> getSimpleSearch(String textSearch) {
        int pageIndex = 1, pageSize = 10;
        Page<StaffDto> page = staffService.findPageByCode(textSearch, pageIndex, pageSize);
        return page.getContent();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/validatestaffcode/{staffId}")
    public Boolean validateStaffCode(@RequestParam String staffCode, @PathVariable UUID staffId) {
        StaffDto staffDto = new StaffDto();

        staffDto.setId(staffId);
        staffDto.setStaffCode(staffCode);

        return staffService.validateStaffCode(staffDto);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/validateusername/{userId}")
    public Boolean validateUserName(@RequestParam String userName, @PathVariable("userId") UUID userId) {
        return staffService.validateUserName(userName, userId);
    }

    @GetMapping(value = "/initial-filter")
    public ResponseEntity<SearchStaffDto> getInitialFilter() {
        SearchStaffDto response = staffService.getInitialFilter();

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/searchByPage", method = RequestMethod.POST)
    public ResponseEntity<Page<StaffDto>> searchByPage(@RequestBody SearchStaffDto searchDto) {
        Page<StaffDto> page = this.staffService.searchByPage(searchDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/paging-staff-labour-management", method = RequestMethod.POST)
    public ResponseEntity<Page<StaffLabourManagementDto>> pagingStaffLabourManagement(@RequestBody SearchStaffDto searchDto) {
        Page<StaffLabourManagementDto> response = staffServiceV3.pagingStaffLabourManagement(searchDto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/paging-staff-labour-util-report", method = RequestMethod.POST)
    public ResponseEntity<Page<StaffLabourUtilReportDto>> pagingStaffLabourUtilReport(@RequestBody SearchStaffDto searchDto) {
        Page<StaffLabourUtilReportDto> response = staffServiceV3.pagingStaffLabourUtilReport(searchDto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/checkIdNumber", method = RequestMethod.POST)
    public ResponseEntity<Boolean> checkIdNumber(@RequestBody StaffDto dto) {
        Boolean rs = staffService.checkIdNumber(dto);
        return new ResponseEntity<>(rs, rs == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/imagePath/{id}")
    public StaffDto updateStaffImage(@RequestParam String imagePath, @PathVariable UUID id) {
        return staffService.updateStaffImage(id, imagePath);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, Constants.ROLE_USER})
    @RequestMapping(method = RequestMethod.POST, value = "/save-staff-without-account")
    public ResponseEntity<StaffDto> saveStaffWithoutAccount(@RequestBody StaffDto staff) {
        // Kiểm tra mã nhân viên
        Boolean isValidCode = staffService.validateStaffCode(staff);
        if (isValidCode == null || !isValidCode) {
            staff.setError("Mã nhân viên đã được sử dụng, vui lòng sử dụng mã nhân viên khác.");
            return new ResponseEntity<>(staff, HttpStatus.CONFLICT);
        }

        // Kiểm tra CCCD
        Boolean isCccdValid = staffService.validateCccd(staff);
        if (isCccdValid == null || !isCccdValid) {
            staff.setError("CCCD nhân viên đã được sử dụng, vui lòng thử lại.");
            return new ResponseEntity<>(staff, HttpStatus.CONFLICT);
        }

        // Kiểm tra CMND
//        Boolean isCmndValid = staffService.validateCmnd(staff);
//        if (isCmndValid == null || !isCmndValid) {
//            staff.setError("CMND nhân viên đã được sử dụng, vui lòng sử dụng mã nhân viên khác..");
//            return new ResponseEntity<>(staff, HttpStatus.CONFLICT);
//        }

        // Nếu tất cả đều hợp lệ, lưu thông tin nhân viên
        StaffDto savedStaff = staffService.saveStaffWithoutAccount(staff);
        return new ResponseEntity<>(savedStaff, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, Constants.ROLE_USER})
    @RequestMapping(method = RequestMethod.POST, value = "/validate-tax-code")
    public Boolean validateTaxCode(@RequestBody StaffDto staff) {
        return staffService.validateTaxCode(staff);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, Constants.ROLE_USER})
    @RequestMapping(method = RequestMethod.POST, value = "/validate-social-insurance-number")
    public Boolean validateSocialInsuranceNumber(@RequestBody StaffDto staff) {
        return staffService.validateSocialInsuranceNumber(staff);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, Constants.ROLE_USER})
    @RequestMapping(method = RequestMethod.POST, value = "/validate-health-insurance-number")
    public Boolean validateHealthInsuranceNumber(@RequestBody StaffDto staff) {
        return staffService.validateHealthInsuranceNumber(staff);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, Constants.ROLE_USER})
    @RequestMapping(method = RequestMethod.POST, value = "/create-users-for-staff")
    public ResponseEntity<List<StaffDto>> createUsersForStaff(@RequestBody List<StaffDto> staffs, @RequestParam(name = "allowCreate", defaultValue = "false") boolean allowCreate) {

        List<StaffDto> res = staffService.createUsersForStaff(staffs, allowCreate);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    //lấy ra những nhân viên có sinh nhật nhập từ tháng và năm và trạng thái đăng làm việc
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.GET, value = "/birthDay-by-month")
    public ResponseEntity<List<StaffDto>> findStaffsHaveBirthDayByMonth(@RequestParam int month) {
        List<StaffDto> staffList = staffService.findStaffsHaveBirthDayByMonth(month);
        return ResponseEntity.ok(staffList);
    }

    //lấy ra những nhân viên có mẫu bảng lương (salaryTemplateId) và chưa có trong kỳ lương (salaryPeriodId)
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, value = "/find-by-salary-template-period")
    public ResponseEntity<List<StaffDto>> findBySalaryTemplatePeriod(@RequestBody SearchStaffDto searchDto) {
        List<StaffDto> staffList = staffService.findBySalaryTemplatePeriod(searchDto);
        return ResponseEntity.ok(staffList);
    }

    // Dòng tổng tiền trong bảng danh sách đóng bảo hiểm
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, value = "/get-total-has-social-ins")
    public ResponseEntity<StaffDto> getTotalHasSocialIns(@RequestBody SearchStaffDto searchDto) {
        StaffDto sumIns = staffService.getTotalHasSocialIns(searchDto);
        return ResponseEntity.ok(sumIns);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-has-insurance-staff")
    public ResponseEntity<?> handleExcel(HttpServletResponse response, @RequestBody SearchStaffDto dto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            dto.setHasSocialIns(true);
            Workbook workbook = staffService.handleExcel(dto);

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

    // Cập nhật cho phép nhân viên chấm công ngoài IP chỉ định
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/enable-external-ip-timekeeping")
    public ResponseEntity<?> enableExternalIpTimekeeping(@RequestBody List<StaffDto> staffs) {
        Integer result = staffService.updateAllowExternalIpTimekeeping(staffs, true);
        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Cập nhật không cho phép nhân viên chấm công ngoài IP chỉ định
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/disable-external-ip-timekeeping")
    public ResponseEntity<?> disableExternalIpTimekeeping(@RequestBody List<StaffDto> staffs) {
        Integer result = staffService.updateAllowExternalIpTimekeeping(staffs, false);
        if (result != null) {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private void setupResponseHeaders(HttpServletResponse response, String fileName, String contentType, String extension) {
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + extension);
    }

    private String generateFileName(String baseName) {
        String dateTimeString = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
        return baseName + "-" + dateTimeString;
    }


    @GetMapping("/export-hic-info-to-word/{staffId}")
    public void exportHICInfoToWord(HttpServletResponse response, @PathVariable UUID staffId) {
        try {
            staffService.exportHICInfoToWord(response, staffId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/export-labor-management-book", method = RequestMethod.POST)
    public ResponseEntity<?> exportLaborManagementBook(HttpServletResponse response, @RequestBody SearchStaffDto dto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            Workbook workbook = staffServiceV3.exportLaborManagementBook(dto);
            setupResponseHeaders(response, generateFileName("SO_QUAN_LY_LAO_DONG"), CONTENT_TYPE, FILE_EXTENSION);
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
        }
    }

    @RequestMapping(value = "/excel-report-on-labor-use-situation", method = RequestMethod.POST)
    public ResponseEntity<?> exportReportLabourUsage(HttpServletResponse response, @RequestBody SearchStaffDto dto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            Workbook workbook = staffServiceV3.exportReportLabourUsage(dto);
            setupResponseHeaders(response, generateFileName("BAO_CAO_TINH_HINH_SU_DUNG_LAO_DONG"), CONTENT_TYPE, FILE_EXTENSION);
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-excel-list-staff")
    public ResponseEntity<?> exportExcelListStaff(HttpServletResponse response, @RequestBody SearchStaffDto dto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            Workbook workbook = staffService.exportExcelListStaff(dto);
            setupResponseHeaders(response, generateFileName("DanhSachNhanVien"), CONTENT_TYPE, FILE_EXTENSION);
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
        }
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/import-excel-list-staff", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importExcelListStaff(HttpServletResponse response, @RequestParam("uploadfile") MultipartFile uploadfile) throws IOException {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());

            ImportStaffDto importStaffDto = staffService.importExcelListStaff(bis);

            if (importStaffDto.getStaffImports() != null && !importStaffDto.getStaffImports().isEmpty()) {
                List<StaffLAImport> successRows = staffServiceV3.saveStaffLAImportFromExcel(importStaffDto.getStaffImports(), false);
            }

            if (importStaffDto.getStaffFamilyRelationshipImports() != null && !importStaffDto.getStaffFamilyRelationshipImports().isEmpty()) {
                List<StaffFamilyRelationshipDto> importedStaffRelationships = staffFamilyRelationshipService.saveStaffFamilyRelationshipImportFromExcel(importStaffDto.getStaffFamilyRelationshipImports());
            }

            if (importStaffDto.getStaffBankAccountImports() != null && !importStaffDto.getStaffBankAccountImports().isEmpty()) {
                List<PersonBankAccountDto> importedBankAccounts = personBankAccountService.saveStaffBankAccountImportFromExcel(importStaffDto.getStaffBankAccountImports());
            }


            if (importStaffDto.getStaffWorkingLocationImports() != null && !importStaffDto.getStaffWorkingLocationImports().isEmpty()) {
                List<StaffWorkingLocationDto> importedWorkingLocation = staffWorkingLocationService.saveStaffWorkingLocationImportFromExcel(importStaffDto.getStaffWorkingLocationImports());
            }

            final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
            final String FILE_EXTENSION = ".xlsx";
            try {
                Workbook workbook = staffServiceV3.exportImportStaffLAResults(importStaffDto);
                setupResponseHeaders(response, generateFileName("KetQuaNhapDuLieuNhanSu"), CONTENT_TYPE, FILE_EXTENSION);
                try (ServletOutputStream outputStream = response.getOutputStream()) {
                    workbook.write(outputStream);
                }
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/import-excel-list-new-staff", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> importExcelListNewStaff(HttpServletResponse response, @RequestParam("uploadfile") MultipartFile uploadfile) throws IOException {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());

            ImportStaffDto importStaffDto = staffService.importExcelListStaff(bis);

            if (importStaffDto.getStaffImports() != null && !importStaffDto.getStaffImports().isEmpty()) {
                List<StaffLAImport> successRows = staffServiceV3.saveStaffLAImportFromExcel(importStaffDto.getStaffImports(), true);
            }

            if (importStaffDto.getStaffFamilyRelationshipImports() != null && !importStaffDto.getStaffFamilyRelationshipImports().isEmpty()) {
                List<StaffFamilyRelationshipDto> importedStaffRelationships = staffFamilyRelationshipService.saveStaffFamilyRelationshipImportFromExcel(importStaffDto.getStaffFamilyRelationshipImports());
            }

            if (importStaffDto.getStaffBankAccountImports() != null && !importStaffDto.getStaffBankAccountImports().isEmpty()) {
                List<PersonBankAccountDto> importedBankAccounts = personBankAccountService.saveStaffBankAccountImportFromExcel(importStaffDto.getStaffBankAccountImports());
            }

            if (importStaffDto.getStaffWorkingLocationImports() != null && !importStaffDto.getStaffWorkingLocationImports().isEmpty()) {
                List<StaffWorkingLocationDto> importedStaffWorkingLocations = staffWorkingLocationService.saveStaffWorkingLocationImportFromExcel(importStaffDto.getStaffWorkingLocationImports());
            }

            final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
            final String FILE_EXTENSION = ".xlsx";
            try {
                Workbook workbook = staffServiceV3.exportImportStaffLAResults(importStaffDto);
                setupResponseHeaders(response, generateFileName("KetQuaNhapDuLieuNhanSu"), CONTENT_TYPE, FILE_EXTENSION);
                try (ServletOutputStream outputStream = response.getOutputStream()) {
                    workbook.write(outputStream);
                }
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-excel-introduce-cost")
    public ResponseEntity<?> exportExcelListHrIntroduceCost(HttpServletResponse response, @RequestBody SearchStaffDto dto) throws IOException {
        dto.setExportExcel(true);
        Page<StaffDto> page = staffService.searchByPage(dto);
        List<StaffDto> datas = page.getContent();

        ByteArrayResource excelFile;
        if (!datas.isEmpty()) {
            try {
                // Cấu hình response header
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.addHeader("Content-Disposition", "attachment; filename=DANH_SACH_PHI_GIOI_THIEU_HO_SO.xlsx");

                excelFile = ExportExcelUtil.handleExportIntroduceCost(datas);
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

    @RequestMapping(method = RequestMethod.POST, value = "/export-family-relationship")
    public ResponseEntity<?> exportFamilyRelationship(HttpServletResponse response, @RequestBody List<UUID> ids) {
        List<Staff> datas = staffService.getListStaffByIds(ids);

        ByteArrayResource excelFile;
        if (!datas.isEmpty()) {
            try {
                // Cấu hình response header
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.addHeader("Content-Disposition", "attachment; filename=QUAN_HE_NHAN_THAN.xlsx");

                InputStream inputStream = new ClassPathResource("Excel/QUAN_HE_NHAN_THAN.xlsx").getInputStream();
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
                excelFile = ExportExcelUtil.handleExportFamilyRelationship(datas, workbook);
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

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-excel-template-import-staff")
    public void exportExcelTemplateImportStaff(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=EmployeeList.xlsx");

        try (InputStream inputStream = new ClassPathResource("EmployeeList.xlsx").getInputStream()) {
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        }
    }

    @GetMapping("/get-last-labour-agreement/{staff}")
    public ApiResponse<StaffLabourAgreementDto> getLastLabourAgreement(@PathVariable UUID staff) {
        return staffService.getLastLabourAgreement(staff);
    }


    //    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @RequestMapping(value = "/generate-new-staff-code", method = RequestMethod.POST)
    public ResponseEntity<String> generateNewStaffCode(@RequestBody SearchStaffDto dto) {
//        String response = staffServiceV3.generateNewStaffCode(dto);
        String response = staffServiceV3.generateNewStaffCodeV2(dto);

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/generate-fix-schedules", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UUID>> generateFixScheduleForChosenStaffs(@RequestBody SearchStaffWorkScheduleDto dto) {
        List<UUID> response = staffServiceV3.generateFixScheduleForChosenStaffs(dto);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/calculate-remainin-annual-leave", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> calculateRemainingAnnualLeave(@RequestBody SearchStaffDto dto) {
        StaffDto response = staffService.calculateRemainingAnnualLeave(dto);
        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
