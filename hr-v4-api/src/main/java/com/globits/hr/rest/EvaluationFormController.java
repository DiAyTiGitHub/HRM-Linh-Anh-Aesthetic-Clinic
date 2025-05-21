package com.globits.hr.rest;

import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.EvaluationFormDto;
import com.globits.hr.dto.StaffEvaluationDto;
import com.globits.hr.dto.search.EvaluationFormSearchDto;
import com.globits.hr.dto.view.EvaluationFormViewDto;
import com.globits.hr.service.EvaluationFormFileExportService;
import com.globits.hr.service.EvaluationFormService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/evaluation-forms")
public class EvaluationFormController {
    private static final Logger logger = LoggerFactory.getLogger(EvaluationFormController.class);

    @Autowired
    private EvaluationFormService evaluationFormService;
    @Autowired
    private EvaluationFormFileExportService evaluationFormFileExportService;

    /**
     * Lấy Biểu mẫu đánh giá theo ID
     *
     * @param id ID của Biểu mẫu đánh giá cần lấy
     * @return ApiResponse chứa Biểu mẫu đánh giá
     * @author anhpdk
     */
    @GetMapping("/{id}")
    public ApiResponse<EvaluationFormDto> getById(@PathVariable UUID id) {
        return evaluationFormService.getById(id);
    }

    /**
     * Tạo mới hoặc cập nhật Biểu mẫu đánh giá
     *
     * @param dto Dữ liệu Biểu mẫu đánh giá cần tạo hoặc cập nhật
     * @return ApiResponse chứa Biểu mẫu đánh giá đã được tạo hoặc cập nhật
     * @author anhpdk
     */
    @PostMapping("/save")
    public ApiResponse<EvaluationFormDto> saveOrUpdate(@RequestBody EvaluationFormDto dto) {
        return evaluationFormService.saveOrUpdate(dto);
    }

    /**
     * Xóa Biểu mẫu đánh giá theo ID
     *
     * @param id ID của Biểu mẫu đánh giá cần xóa
     * @return ApiResponse với giá trị true nếu xóa thành công, false nếu không tìm thấy
     * @author anhpdk
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteById(@PathVariable UUID id) {
        return evaluationFormService.deleteById(id);
    }

    /**
     * Lấy tất cả các Biểu mẫu đánh giá
     *
     * @return ApiResponse chứa danh sách các Biểu mẫu đánh giá
     * @author anhpdk
     */
    @GetMapping("/get-all")
    public ApiResponse<List<EvaluationFormDto>> getAll() {
        return evaluationFormService.getAll();
    }

    /**
     * Đánh dấu Biểu mẫu đánh giá là đã xóa (soft delete)
     *
     * @param id ID của Biểu mẫu đánh giá cần đánh dấu đã xóa
     * @return ApiResponse với giá trị true nếu thành công, false nếu không tìm thấy
     * @author anhpdk
     */
    @PutMapping("/{id}/mark-delete")
    public ApiResponse<Boolean> markDeleteById(@PathVariable UUID id) {
        return evaluationFormService.markDeleteById(id);
    }

    /**
     * Phân trang danh sách biểu mẫu
     *
     * @param searchDto body bao gồm pageIndex và pageSize
     * @return ApiResponse chứa danh sách các Biểu mẫu đánh giá
     * @author anhpdk
     */
    @PostMapping("/paging")
    public ApiResponse<Page<EvaluationFormViewDto>> paging(@RequestBody EvaluationFormSearchDto searchDto) {
        return evaluationFormService.paging(searchDto);
    }

    @GetMapping("/export-word/{formId}")
    public void exportWord(HttpServletResponse response, @PathVariable UUID formId) {
        try {
            // Lấy tài liệu Word đã xử lý từ service
            XWPFDocument doc = evaluationFormFileExportService.exportWord(formId);
            // Ghi nội dung vào ByteArrayOutputStream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.write(out);
            doc.close(); // đóng để tránh leak
            // Thiết lập HTTP response headers
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition", "attachment; filename=Tai_Ki_Hop_Dong.docx");
            // Ghi file ra OutputStream
            response.getOutputStream().write(out.toByteArray());
            response.getOutputStream().flush();
            out.close();
        } catch (Exception e) {
            logger.error("Lỗi khi xuất file Word", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể xuất file Word");
            } catch (IOException ioException) {
                logger.error("Lỗi khi xuất file Word", ioException);
            }
        }
    }

    @GetMapping("/transfer-evaluation-form/{id}")
    public ApiResponse<Boolean> transferEvaluationForm(@PathVariable UUID id) {
        return evaluationFormService.transferEvaluationForm(id);
    }

    @PostMapping("/staff-evaluate")
    public ApiResponse<Boolean> staffEvaluate(@RequestBody List<StaffEvaluationDto> dto) {
        return evaluationFormService.staffEvaluate(dto);
    }

    @PostMapping("/export-contract-approval-list")
    public void exportContractApprovalList(HttpServletResponse response, @RequestBody EvaluationFormSearchDto search) {
        try {
            // Gọi service để tạo workbook
            Workbook workbook = evaluationFormFileExportService.exportContractApprovalList(search);
            // Thiết lập header trả về file Excel
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=contract_approval_list.xlsx");
            // Ghi workbook vào response output stream
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close(); // đóng lại để tránh memory leak
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace(); // log lỗi
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


}
