package com.globits.hr.rest;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.EvaluationItemDto;
import com.globits.hr.dto.ShiftWorkDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.EvaluationItemService;
import com.globits.hr.utils.ImportExportExcelUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
@RequestMapping("/api/evaluation-item")
public class EvaluationItemController {

    @Autowired
    private EvaluationItemService evaluationItemService;

    /**
     * Tạo mới hoặc cập nhật tiêu chí cho Biểu mẫu đánh giá
     * @param dto Dữ liệu tiêu chí cho Biểu mẫu đánh giá cần tạo hoặc cập nhật
     * @return ApiResponse chứa tiêu chí Biểu mẫu đánh giá đã được tạo hoặc cập nhật
     */
    @PostMapping("/save")
    public ApiResponse<EvaluationItemDto> saveOrUpdate(@RequestBody EvaluationItemDto dto) {
        return evaluationItemService.save(dto);
    }

    /**
     * Lấy thông tin tiêu chí theo ID
     * @param id UUID của tiêu chí
     * @return ApiResponse chứa thông tin tiêu chí
     */
    @GetMapping("/{id}")
    public ApiResponse<EvaluationItemDto> getById(@PathVariable UUID id) {
        return evaluationItemService.getById(id);
    }

    /**
     * Lấy danh sách tất cả tiêu chí đánh giá
     * @return ApiResponse chứa danh sách tiêu chí đánh giá
     */
    @GetMapping
    public ApiResponse<List<EvaluationItemDto>> getAll() {
        return evaluationItemService.getAll();
    }

    /**
     * Xoá tiêu chí đánh giá theo ID
     * @param id UUID của tiêu chí
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        evaluationItemService.delete(id);
    }

    /**
     * Đánh dấu tiêu chí là đã xoá (soft delete)
     * @param id UUID của tiêu chí
     * @return ApiResponse xác nhận đã đánh dấu xoá
     */
    @PutMapping("/mark-deleted/{id}")
    public ApiResponse<Boolean> markDeleted(@PathVariable UUID id) {
        return evaluationItemService.markDeleted(id);
    }
    /**
     * Phân trang danh sách tiêu chí
     * @author anhpdk
     * @param searchDto body bao gồm pageIndex và pageSize
     * @return ApiResponse chứa danh sách các tiêu chí đánh giá
     */
    @PostMapping("/paging")
    public ApiResponse<Page<EvaluationItemDto>> paging(@RequestBody SearchDto searchDto) {
        return evaluationItemService.paging(searchDto);
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public ResponseEntity<?> importShiftWorkFromInputStream(@RequestParam("uploadfile") MultipartFile uploadfile) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(uploadfile.getBytes());
            List<EvaluationItemDto> list = ImportExportExcelUtil.readEvaluationItemDtoFile(bis);
            Integer count = evaluationItemService.saveList(list);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Secured({HrConstants.ROLE_HR_MANAGEMENT, Constants.ROLE_ADMIN, HrConstants.HR_MANAGER})
    @PostMapping("/download-template")
    public void downloadTemplateFileExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=MAU_TIEU_CHI_DANH_GIA.xlsx");
        ClassPathResource resource = new ClassPathResource("Excel/MAU_TIEU_CHI_DANH_GIA.xlsx");
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

    @GetMapping(value = "/auto-gen-code/{configKey}")
    public ResponseEntity<?> autoGenCode(@PathVariable("configKey") String configKey) {
        return new ResponseEntity<>(evaluationItemService.autoGenerateCode(configKey), HttpStatus.OK);
    }
}
