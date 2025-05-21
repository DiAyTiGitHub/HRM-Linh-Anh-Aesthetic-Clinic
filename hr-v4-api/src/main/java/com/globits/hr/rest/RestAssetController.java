package com.globits.hr.rest;

import com.globits.hr.dto.AssetDto;
import com.globits.hr.dto.ShiftWorkDto;
import com.globits.hr.dto.TransferAssetDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.AssetService;
import com.globits.hr.utils.ImportExportExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/asset")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RestAssetController {
    @Resource
    private AssetService service;

    @GetMapping(value = "/get-all")
    public List<AssetDto> getAll() {
        return service.getAll();
    }

    @GetMapping(value = "/{id}")
    public AssetDto getById(@PathVariable("id") UUID id) {
        return service.getAsset(id);
    }

    @GetMapping(value = "/return/{id}")
    public AssetDto returnAsset(@PathVariable("id") UUID id) {
        return service.returnAsset(id);
    }


    @PostMapping(value = "/save")
    public AssetDto saveAsset(@RequestBody AssetDto dto) {
        return service.saveAsset(dto);
    }

    @PostMapping(value = "/transfer-asset")
    public AssetDto transferAsset(@RequestBody TransferAssetDto dto) {
        return service.transferAsset(dto);
    }

    @DeleteMapping(value = "/{id}")
    public Boolean deleteAsset(@PathVariable("id") UUID id) {
        return service.deleteAsset(id);
    }

    @PostMapping(value = "/paging")
    public Page<AssetDto> paging(@RequestBody SearchDto dto) {
        return service.paging(dto);
    }

    @GetMapping(value = "/get-by-staff/{staffId}")
    public List<AssetDto> getListByStaff(@PathVariable("staffId") UUID staffId) {
        return service.getListByStaff(staffId);
    }

    @GetMapping(value = "/get-by-product/{id}")
    public List<AssetDto> getListByProduct(@PathVariable("id") UUID id) {
        return service.getListByProduct(id);
    }

    @PostMapping("/export-excel-asset-template")
    public void exportExcelShiftWorkTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_CONG_CU_DUNG_CU.xlsx");

        try (InputStream inputStream = new ClassPathResource("Excel/MAU_CONG_CU_DUNG_CU.xlsx").getInputStream()) {
            org.apache.commons.io.IOUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        }
    }

    @RequestMapping(value = "/import-excel-asset", method = RequestMethod.POST)
    public ResponseEntity<?> importAssetFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<AssetDto> list = ImportExportExcelUtil.readAssetDtoFile(bis);
            if (list == null || list.isEmpty()) {
                return new ResponseEntity<>("File import không có dữ liệu.", HttpStatus.BAD_REQUEST);
            }
            if (list.get(0) != null && list.get(0).getErrorMessage() != null) {
                return new ResponseEntity<>(list.get(0).getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
            Integer countSaveShiftWork = service.saveListAsset(list);
            return new ResponseEntity<>(countSaveShiftWork, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
