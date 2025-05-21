package com.globits.hr.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchPositionDto;
import com.globits.hr.utils.ImportExportExcelUtil;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
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
import com.globits.hr.service.PositionService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/position")
public class RestPositionController {
    @Autowired
    private PositionService positionService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/savePosition")
    public ResponseEntity<PositionDto> savePosition(@RequestBody PositionDto dto) {
        // Position's code is duplicated
        Boolean isValidCode = positionService.isValidCode(dto);
        if (isValidCode == null || isValidCode.equals(false)) {
            return new ResponseEntity<PositionDto>(dto, HttpStatus.CONFLICT);
        }

        PositionDto response = positionService.savePosition(dto);
        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/transfer-position")
    public ResponseEntity<List<PositionDto>> transferPositions(@RequestBody TransferPositionsDto dto) {
        List<PositionDto> response = positionService.transferPositions(dto);

        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST, path = "/transfer-staff")
    public ResponseEntity<List<PositionDto>> transferStaff(@RequestBody TransferStaffDto dto) {
        List<PositionDto> response = positionService.transferStaff(dto);

        if (response == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/pagingPosition", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<PositionDto>> pagingPosition(@RequestBody SearchPositionDto searchDto) {
        Page<PositionDto> page = positionService.pagingPosition(searchDto);
        if (page == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER})
    @RequestMapping(value = "/import-relationship", method = RequestMethod.POST)
    public ResponseEntity<?> importAdministrativeUnitFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            long startTime = System.currentTimeMillis();
            // Đọc dữ liệu từ file
            List<ImportPositionRelationShipDto> list = ImportExportExcelUtil.readAllPositionRelationShipFile(bis);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Thời gian đọc file và đẩy vào list (ms): " + duration);
            startTime = System.currentTimeMillis();
            int result = positionService.saveListImportExcel(list);
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            System.out.println("Thời gian ghi file và đẩy vào db (ms): " + duration);

            if (result == -1) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/count-position-with-position-title", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> countNumberOfPositionInDepartmentWithPositionTitle(@RequestBody SearchPositionDto searchDto) {
        Long count = positionService.countNumberOfPositionInDepartmentWithPositionTitle(searchDto);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @GetMapping(value = "/get-resource-plan-item/{departmentId}")
    public ResponseEntity<Set<HrResourcePlanItemDto>> getResourcePlanItem(@PathVariable("departmentId") UUID departmentId) {
        Set<HrResourcePlanItemDto> res = positionService.getResourcePlanItem(departmentId);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> deletePosition(@PathVariable("id") UUID positionId) {
        Boolean res = positionService.deletePosition(positionId);
        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<PositionDto> getById(@PathVariable("id") UUID id) {
        PositionDto result = positionService.getById(id);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/get-by-staff/{id}", method = RequestMethod.GET)
    public ResponseEntity<PositionDto> getByStaffId(@PathVariable("id") UUID staffId) {
        PositionDto result = positionService.getByStaffId(staffId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/deleteMultiple", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteMultiple(@RequestBody List<UUID> ids) {
        Boolean deleted = positionService.deleteMultiplePositions(ids);
        if (deleted) {
            return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-excel-position-template")
    public void exportExcelPositionTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_CHUC_VU.xlsx");

        try (InputStream inputStream = new ClassPathResource("Excel/MAU_CHUC_VU.xlsx").getInputStream()) {
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-excel-position-relationship-template")
    public void exportExcelPositionRelationshipTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_QUAN_HE_VI_TRI.xlsx");

        try (InputStream inputStream = new ClassPathResource("Excel/MAU_QUAN_HE_VI_TRI.xlsx").getInputStream()) {
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/import-excel-position", method = RequestMethod.POST)
    public ResponseEntity<?> importPositionFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<PositionDto> list = ImportExportExcelUtil.readPositionFile(bis);
            if (list == null || list.isEmpty()) {
                return new ResponseEntity<>("File import không có dữ liệu.", HttpStatus.BAD_REQUEST);
            }
            if (list.get(0) != null && list.get(0).getErrorMessage() != null) {
                return new ResponseEntity<>(list.get(0).getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
            int countSavePosition = positionService.saveListPosition(list);
            return new ResponseEntity<>(countSavePosition, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @DeleteMapping(value = "/remove-staff-from-position/{id}")
    public ResponseEntity<PositionDto> removeStaffFromPosition(@PathVariable("id") UUID positionId) {
        PositionDto res = positionService.removeStaffFromPosition(positionId);

        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(path = "/assign-positions-for-staff", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PositionDto>> assignPositionsForStaff(@RequestBody SearchPositionDto dto) {
        List<PositionDto> res = positionService.assignPositionsForStaff(dto);

        if (res == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @PostMapping("/export-excel-position")
    public ResponseEntity<?> exportExcelOrgData(HttpServletResponse response, @RequestBody SearchPositionDto dto) {
        final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
        final String FILE_EXTENSION = ".xlsx";
        try {
            Workbook workbook = positionService.exportExcelPosition(dto);
            setupResponseHeaders(response, generateFileName("DuLieuChucVu"), CONTENT_TYPE, FILE_EXTENSION);
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

    @GetMapping(value = "/auto-gen-code/{configKey}")
    public ResponseEntity<?> autoGenCode(@PathVariable("configKey") String configKey) {
        return new ResponseEntity<>(positionService.autoGenerateCode(configKey), HttpStatus.OK);
    }
}
