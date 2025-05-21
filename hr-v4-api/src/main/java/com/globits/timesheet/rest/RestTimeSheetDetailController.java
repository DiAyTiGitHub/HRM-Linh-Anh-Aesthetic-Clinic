package com.globits.timesheet.rest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.PositionTitleSearchDto;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.service.StaffService;
import com.globits.hr.service.UserExtService;
import com.globits.hr.utils.ExportExcelUtil;
import com.globits.timesheet.dto.importExcel.ImportTimesheetDetailDto;
import com.globits.timesheet.dto.search.LeaveRequestSearchDto;
import com.globits.timesheet.dto.search.SearchTimeSheetDto;
import com.globits.timesheet.dto.TimeSheetCalendarDto;
import com.globits.timesheet.dto.TimeSheetCalendarItemDto;
import com.globits.timesheet.dto.TimeSheetDetailDto;
import com.globits.timesheet.dto.api.SearchTimeSheetApiDto;
import com.globits.timesheet.service.TimeSheetDetailService;
import com.globits.timesheet.service.TimeSheetService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/timesheetdetail")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestTimeSheetDetailController {

    private static final Logger logger = LoggerFactory.getLogger(TimeSheetDetailDto.class);

    @Autowired
    private TimeSheetDetailService timeSheetDetailService;

    @Autowired
    private TimeSheetService timeSheetService;

    @Autowired
    private StaffService staffService;
    @Autowired
    private UserExtService userExtService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<TimeSheetDetailDto> findTimeSheetDetailById(@PathVariable("id") UUID id) {
        TimeSheetDetailDto result = timeSheetDetailService.findTimeSheetDetailById(id);
        return new ResponseEntity<TimeSheetDetailDto>(result, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<TimeSheetDetailDto> saveTimeSheetDetail(@RequestBody TimeSheetDetailDto dto) {
        dto = timeSheetDetailService.saveTimeSheetDetail(dto, null);
        if (dto == null) {
            return new ResponseEntity<TimeSheetDetailDto>(dto, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<TimeSheetDetailDto>(dto, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteTimeSheetDetail(@RequestBody List<TimeSheetDetailDto> list) {
        Boolean ret = timeSheetDetailService.deleteTimeSheetDetails(list);
        return new ResponseEntity<Boolean>(ret, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<TimeSheetDetailDto> update(@RequestBody TimeSheetDetailDto dto, @PathVariable UUID id) {
        TimeSheetDetailDto result = timeSheetDetailService.saveTimeSheetDetail(dto, id);
        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-time-sheet/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<TimeSheetDetailDto>> getTimeSheetByTime(@PathVariable("id") UUID id) {
        List<TimeSheetDetailDto> result = timeSheetDetailService.getTimeSheetDetailByTimeSheetId(id);
        if (result == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/search-by-page", method = RequestMethod.POST)
    public ResponseEntity<Page<TimeSheetDetailDto>> searchByPage(@RequestBody SearchTimeSheetDto searchDto) {
        Page<TimeSheetDetailDto> page = timeSheetDetailService.searchByPage(searchDto);
        if (page == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping(value = "/initial-filter")
    public ResponseEntity<?> getInitialFilter() {
        SearchTimeSheetDto response = timeSheetDetailService.getInitialFilter();

        if (response == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/getListTimeSheetDetailByProjectActivityId/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<TimeSheetDetailDto>> getListTimeSheetDetailByProjectActivityId(@PathVariable UUID id) {
        List<TimeSheetDetailDto> list = timeSheetDetailService.getListTimeSheetDetailByProjectActivityId(id);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/getListTimeSheetDetailByProjectId/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<TimeSheetDetailDto>> getListTimeSheetDetailByProjectId(@PathVariable UUID id) {
        List<TimeSheetDetailDto> list = timeSheetDetailService.getListTimeSheetDetailByProjectId(id);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "/exportExcel", method = RequestMethod.POST)
    public void exportTimeSheetsToExcel(HttpServletResponse response,
                                        @RequestBody SearchTimeSheetDto searchTimeSheetDto) throws IOException {
        Page<TimeSheetDetailDto> data = timeSheetDetailService.searchByPage(searchTimeSheetDto);
        List<TimeSheetDetailDto> dataList = data.getContent();
        ByteArrayResource excelFile;
        if (!dataList.isEmpty()) {
            excelFile = ExportExcelUtil.exportTimeSheetDetailToExcelTable(dataList, true);
            InputStream ins = null;
            if (excelFile != null) {
                ins = new ByteArrayInputStream(excelFile.getByteArray());
            }
            if (ins != null) {
                org.apache.commons.io.IOUtils.copy(ins, response.getOutputStream());
            }
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.addHeader("Content-Disposition", "attachment; filename=TimeSheetDetail.xlsx");
    }

    @RequestMapping(value = "/update-status/{id}/{workingStatusId}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateStatus(@PathVariable UUID id, @PathVariable UUID workingStatusId) {
        String result = timeSheetDetailService.updateStatus(id, workingStatusId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteTimeSheetDetail(@PathVariable("id") UUID id) {
        Boolean ret = timeSheetDetailService.deleteTimeSheetDetailById(id);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @RequestMapping(value = "/updateDetail", method = RequestMethod.GET)
    public boolean updateDetail() {
        return timeSheetDetailService.updateTimesheetDetail();
    }

    @RequestMapping(value = "/get-list-timesheet-detail", method = RequestMethod.POST)
    public ResponseEntity<?> getTimesheetDetail(@RequestBody SearchTimeSheetDto searchDto) {
        StaffDto currentStaff = userExtService.getCurrentStaff();
        if (searchDto.getStaffId() == null) {
            if (currentStaff != null && currentStaff.getId() != null) {
                searchDto.setStaffId(currentStaff.getId());
            }
        }
        if (searchDto.getStaffId() != null) {
            Staff staff = staffService.getEntityById(searchDto.getStaffId());
            currentStaff = new StaffDto(staff);
        } else {
            // handle case for admin user
            return new ResponseEntity<>(new TimeSheetCalendarDto(new ArrayList<>(), currentStaff), HttpStatus.OK);
        }
        List<TimeSheetCalendarItemDto> timeSheetDetails = timeSheetDetailService.getListTimesheetDetail(searchDto);
        return new ResponseEntity<>(new TimeSheetCalendarDto(timeSheetDetails, currentStaff), HttpStatus.OK);
    }

    @RequestMapping(value = "/get-list-timesheet-detail-of-all-staff", method = RequestMethod.POST)
    public ResponseEntity<?> getListTimesheetDetailOfAllStaff(@RequestBody SearchTimeSheetDto searchDto) {
        List<TimeSheetCalendarItemDto> timeSheetDetails = timeSheetDetailService
                .getListTimesheetDetailOfAllStaff(searchDto);
        if (timeSheetDetails == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(timeSheetDetails, HttpStatus.OK);
    }

    @RequestMapping(value = "/auto-generate", method = RequestMethod.POST)
    public ResponseEntity<List<TimeSheetDetailDto>> autoGenerateTimeSheetDetails(
            @RequestBody SearchTimeSheetDto searchDto) {
        List<TimeSheetDetailDto> autoGeneratedTimesheetDetails =
//                timeSheetDetailService.autoGenerateTimeSheetDetailInRangeTime(searchDto);
                timeSheetDetailService.autoGenerateTimeSheetDetailInRangeTimeV2(searchDto);
        if (autoGeneratedTimesheetDetails == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(autoGeneratedTimesheetDetails, HttpStatus.OK);
    }

    @RequestMapping(value = "/download-timeSheet-detail-template", method = RequestMethod.GET)
    public ResponseEntity<?> downloadLeadTemplate() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("import_cham_cong.xlsx");
            if (is == null) {
                return ResponseEntity.notFound().build();
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=templates_cham_cong.xlsx");

            return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(is));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/import-timeSheet-detail", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<?> importTimeSheetDetail(@RequestParam("upload") MultipartFile file, HttpServletResponse response) {
        try {
            // Gọi service xử lý và nhận file output
            ByteArrayOutputStream excelOutput = timeSheetDetailService.exportImportResultTimeSheet(file);

            // Thiết lập response trả về file Excel
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=result_timeSheet_detail.xlsx");

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


    @RequestMapping(value = "/save-or-update", method = RequestMethod.POST)
    public ResponseEntity<TimeSheetDetailDto> saveOrUpdate(
            @RequestBody TimeSheetDetailDto dto,
            HttpServletRequest request) {

        if (dto == null) {
            return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra lần chấm công mới có hợp lệ hay không
        if (dto.getId() == null) {
            // TODO: Thêm logic kiểm tra nếu cần
        }

        // Lấy địa chỉ IP của client từ request
        String clientIp = request.getRemoteAddr();
        String forwardedIp = request.getHeader("X-Forwarded-For");

        if (forwardedIp != null && !forwardedIp.isEmpty()) {
            clientIp = forwardedIp.split(",")[0]; // Lấy IP đầu tiên nếu có nhiều IP
        }

        if (dto.getStartTime() != null && dto.getAddressIPCheckIn() == null) {
            dto.setAddressIPCheckIn(clientIp);
        }

        if (dto.getEndTime() != null && dto.getAddressIPCheckOut() == null) {
            dto.setAddressIPCheckOut(clientIp);
        }

        dto = timeSheetDetailService.saveOrUpdate(dto, request);

        // Kiểm tra kết quả
        if (dto == null) {
            return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, value = "/delete-multiple", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> listId) {
        Boolean deleted = timeSheetDetailService.deleteMultiple(listId);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
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

    // Xuất dữ liệu chấm công với mẫu của hệ thống
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-with-system-template")
    public ResponseEntity<?> exportDataWithSystemTemplate(HttpServletResponse response, @RequestBody SearchTimeSheetDto dto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            Workbook workbook = timeSheetDetailService.exportDataWithSystemTemplate(dto);
            setupResponseHeaders(response, generateFileName("DuLieuChamCongTheoMauHeThong"), CONTENT_TYPE, FILE_EXTENSION);
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error generating Excel file.");
        }
    }

    // Nhập dữ liệu chấm công với mẫu của hệ thống
    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/import-data-with-system-template", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<?> importDataWithSystemTemplate(@RequestParam("uploadfile") MultipartFile file, HttpServletResponse response) {
        try {
            // Gọi service xử lý và nhận file output
            ByteArrayOutputStream excelOutput = timeSheetDetailService.exportImportResultTimeSheetDetailSystem(file);

            // Thiết lập response trả về file Excel
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=result_timeSheet_detail.xlsx");

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

    @RequestMapping(value = "/convert-timesheet-by-api", method = RequestMethod.POST)
    public ResponseEntity<List<TimeSheetDetailDto>> convertTimeSheetDetailByApiTimeSheet(@RequestBody SearchTimeSheetApiDto searchTimeSheetDto) throws IOException {
        List<TimeSheetDetailDto> data = timeSheetDetailService.convertTimeSheetDetailByApiTimeSheet(searchTimeSheetDto);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-excel-LA-timekeeping-data")
    public ResponseEntity<?> exportExcelLATimekeepingData(HttpServletResponse response, @RequestBody SearchTimeSheetApiDto dto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            Workbook workbook = timeSheetDetailService.exportExcelLATimekeepingData(dto);
            setupResponseHeaders(response, generateFileName("DuLieuChamCongLinhAnh"), CONTENT_TYPE, FILE_EXTENSION);
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
