package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.core.dto.AdministrativeUnitDto;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.HrAdministrativeUnitDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchAdministrativeUnitDto;
import com.globits.hr.service.HrAdministrativeUnitService;
import com.globits.hr.utils.ExportExcelUtil;
import com.globits.hr.utils.ImportExportExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/hrAdministrativeUnit")
public class RestHrAdministrativeUnitController {
    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    HrAdministrativeUnitService hrAdministrativeUnitService;

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, Constants.ROLE_USER, HrConstants.HR_USER})
    @PostMapping("/searchByPage")
    public ResponseEntity<Page<HrAdministrativeUnitDto>> searchByPage(@RequestBody SearchAdministrativeUnitDto dto) {
        Page<HrAdministrativeUnitDto> page = hrAdministrativeUnitService.searchByPage(dto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, Constants.ROLE_USER, HrConstants.HR_USER})
    @RequestMapping(method = {RequestMethod.POST})
    public AdministrativeUnitDto saveAdministrativeUnit(@RequestBody AdministrativeUnitDto AdministrativeUnit) {
        return this.hrAdministrativeUnitService.saveAdministrativeUnit(AdministrativeUnit);
    }


//    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, Constants.ROLE_USER,HrConstants.HR_USER})
//    @RequestMapping(
//            value = {"/{administrativeUnitId}"},
//            method = {RequestMethod.PUT}
//    )
//    public AdministrativeUnit updateAdministrativeUnit(@RequestBody AdministrativeUnit AdministrativeUnit, @PathVariable("administrativeUnitId") String administrativeUnitId) {
//        return this.hrAdministrativeUnitService.updateAdministrativeUnit(AdministrativeUnit, UUID.fromString(administrativeUnitId));
//    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, "ROLE_STUDENT_MANAGERMENT", "ROLE_EDUCATION_MANAGERMENT", "ROLE_FINANCIAL_MANAGERMENT", "ROLE_EXAM_MANAGERMENT"})
    @PutMapping("/{id}")
    public ResponseEntity<AdministrativeUnitDto> update(@RequestBody AdministrativeUnitDto administrativeUnit, @PathVariable UUID id) {
        AdministrativeUnitDto result = hrAdministrativeUnitService.updateAdministrativeUnit(administrativeUnit, id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, Constants.ROLE_USER, HrConstants.HR_USER})
    @RequestMapping(value = {"/{administrativeUnitId}"}, method = {RequestMethod.DELETE})
    public AdministrativeUnitDto removeAdministrativeUnit(@PathVariable("administrativeUnitId") String administrativeUnitId) {
        AdministrativeUnitDto administrativeUnit = this.hrAdministrativeUnitService.deleteAdministrativeUnit(UUID.fromString(administrativeUnitId));
        return administrativeUnit;
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, Constants.ROLE_USER, HrConstants.HR_USER})
    @PostMapping("/checkCode")
    public ResponseEntity<Boolean> checkCode(@RequestBody AdministrativeUnitDto dto) {
        Boolean result = hrAdministrativeUnitService.checkCode(dto.getId(), dto.getCode());
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, Constants.ROLE_USER, HrConstants.HR_USER})
    @RequestMapping(value = {"/getAllChildByParentId/{id}"}, method = {RequestMethod.GET})
    public ResponseEntity<List<AdministrativeUnitDto>> getAllChildByParentId(@PathVariable UUID id) {
        List<AdministrativeUnitDto> result = hrAdministrativeUnitService.getAllChildByParentId(id);
        return new ResponseEntity<List<AdministrativeUnitDto>>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, Constants.ROLE_USER, HrConstants.HR_USER})
    @RequestMapping(value = {"/getAllByLevel/{level}"}, method = {RequestMethod.GET})
    public ResponseEntity<List<AdministrativeUnitDto>> getAllByLevel(@PathVariable Integer level) {
        List<AdministrativeUnitDto> result = hrAdministrativeUnitService.getAllByLevel(level);
        return new ResponseEntity<List<AdministrativeUnitDto>>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, Constants.ROLE_USER, HrConstants.HR_USER})
    @RequestMapping(value = {"/get-all-administrative-id-by-parent-id/{id}"}, method = {RequestMethod.GET})
    public ResponseEntity<List<UUID>> getAllAdministrativeUnitIdByParentId(@PathVariable UUID id) {
        List<UUID> result = hrAdministrativeUnitService.getAllAdministrativeIdByParentId(id, true);
        return new ResponseEntity<List<UUID>>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER})
    @PostMapping("/export-excel-administrative-unit-template")
    public void exportExcelAdministrativeUnitTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_DON_VI_HANH_CHINH.xlsx");
        ClassPathResource resource = new ClassPathResource("Excel/MAU_DON_VI_HANH_CHINH.xlsx");
        if (resource.exists()) {
            try (InputStream inputStream = resource.getInputStream()) {
                IOUtils.copy(inputStream, response.getOutputStream());
                response.flushBuffer();
            }
        } else {
            try (Workbook workbook = new XSSFWorkbook()) {
                workbook.createSheet("Đơn vị hành chính");
                workbook.write(response.getOutputStream());
                response.flushBuffer();
            }
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER})
    @PostMapping("/export-excel-administrative-unit")
    public ResponseEntity<?> handleExportAdministrativeUnit(HttpServletResponse response, @RequestBody SearchAdministrativeUnitDto dto) {
        dto.setExportExcel(true);
        Instant startFetch = Instant.now(); // Bắt đầu tính thời gian lấy dữ liệu
        Page<HrAdministrativeUnitDto> page = hrAdministrativeUnitService.searchByPage(dto);
        List<HrAdministrativeUnitDto> datas = page.getContent();
        Instant endFetch = Instant.now(); // Kết thúc lấy dữ liệu
        log.info("Finished fetching data. Time: {} ms", Duration.between(startFetch, endFetch).toMillis());

        ByteArrayResource excelFile;
        if (!datas.isEmpty()) {
            try {
                // Cấu hình response header
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.addHeader("Content-Disposition", "attachment; filename=DU_LIEU_DON_VI_HANH_CHINH.xlsx");

                InputStream inputStream = new ClassPathResource("Excel/DU_LIEU_DON_VI_HANH_CHINH.xlsx").getInputStream();
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

                startFetch = Instant.now(); // Bắt đầu ghi excel
                excelFile = ExportExcelUtil.handleExportAdministrativeUnit(datas, workbook);
                endFetch = Instant.now(); // Kết thúc ghi dữ liệu 
                log.info("Finished write data. Time: {} ms", Duration.between(startFetch, endFetch).toMillis());

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

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER})
    @RequestMapping(value = "/import-excel-administrative-unit", method = RequestMethod.POST)
    public ResponseEntity<?> importAdministrativeUnitFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            long startTime = System.currentTimeMillis();
            // Đọc dữ liệu từ file
//            List<HrAdministrativeUnitDto> list = ImportExportExcelUtil.readAdministrativeUnitFile(bis);
            List<HrAdministrativeUnitDto> list = ImportExportExcelUtil.readAllAdministrativeUnitFile(bis);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Thời gian đọc file và đẩy vào list (ms): " + duration);
            startTime = System.currentTimeMillis();
            int result = hrAdministrativeUnitService.saveListImportExcel(list);
            endTime = System.currentTimeMillis();
            duration = endTime - startTime;
            System.out.println("Thời gian ghi file và đẩy vào db (ms): " + duration);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
