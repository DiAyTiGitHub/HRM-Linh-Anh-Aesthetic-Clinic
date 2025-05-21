package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.HrOrganizationDto;
import com.globits.hr.dto.ShiftWorkDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.ShiftWorkService;
import com.globits.hr.utils.ImportExportExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shiftwork")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestShiftWorkController {
    @Autowired
    private ShiftWorkService shiftWorkService;


    @PostMapping("/search-by-page")
    public ResponseEntity<Page<ShiftWorkDto>> searchByPage(@RequestBody SearchDto dto) {
        Page<ShiftWorkDto> pageShiftWork = shiftWorkService.searchByPage(dto);
        return new ResponseEntity<>(pageShiftWork, HttpStatus.OK);
    }

    @RequestMapping(value = "/check-code", method = RequestMethod.GET)
    public ResponseEntity<Boolean> checkCode(@RequestParam(value = "id", required = false) UUID id, @RequestParam("code") String code) {
        Boolean result = shiftWorkService.checkCode(id, code);
        return new ResponseEntity<>(result, (result != null) ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ShiftWorkDto> save(@RequestBody ShiftWorkDto dto) {
        ShiftWorkDto result = shiftWorkService.saveOrUpdate(null, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ShiftWorkDto> update(@RequestBody ShiftWorkDto dto, @PathVariable UUID id) {
        ShiftWorkDto result = shiftWorkService.saveOrUpdate(id, dto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> delete(@PathVariable String id) {
        shiftWorkService.remove(UUID.fromString(id));
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftWorkDto> getShiftWorkById(@PathVariable String id) {
        ShiftWorkDto result = shiftWorkService.getById(UUID.fromString(id));
        return new ResponseEntity<>(result, result != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/export-excel-shift-work-template")
    public void exportExcelShiftWorkTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_PHONG_BAN_AP_DUNG_CA_LAM_VIEC.xlsx");

        try (InputStream inputStream = new ClassPathResource("Excel/MAU_PHONG_BAN_AP_DUNG_CA_LAM_VIEC.xlsx").getInputStream()) {
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        }
    }

    @RequestMapping(value = "/import-excel-shift-work", method = RequestMethod.POST)
    public ResponseEntity<?> importShiftWorkFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<ShiftWorkDto> list = ImportExportExcelUtil.readShiftWorkDtoFile(bis);
            if (list == null || list.isEmpty()) {
                return new ResponseEntity<>("File import không có dữ liệu.", HttpStatus.BAD_REQUEST);
            }
            if (list.get(0) != null && list.get(0).getErrorMessage() != null) {
                return new ResponseEntity<>(list.get(0).getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
            Integer countSaveShiftWork = shiftWorkService.saveListShiftWork(list);
            return new ResponseEntity<>(countSaveShiftWork, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
