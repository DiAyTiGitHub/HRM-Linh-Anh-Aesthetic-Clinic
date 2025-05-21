package com.globits.hr.rest;

import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.OtherIncomeDto;
import com.globits.hr.dto.search.SearchOtherIncomeDto;
import com.globits.hr.service.OtherIncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/other-income")
public class RestOtherIncomeController {

    @Autowired
    private OtherIncomeService otherIncomeService;

    /**
     * Tạo mới hoặc cập nhật lịch phỏng vấn
     * @param dto Dữ liệu lịch phỏng vấn cần tạo hoặc cập nhật
     * @return ApiResponse chứa lịch phỏng vấn đã được tạo hoặc cập nhật
     */
    @PostMapping("/save")
    public ApiResponse<OtherIncomeDto> saveOrUpdate(@RequestBody OtherIncomeDto dto) {
        return otherIncomeService.save(dto);
    }

    /**
     * Lấy thông tin lịch phỏng vấn theo ID
     * @param id UUID của lịch phỏng vấn
     * @return ApiResponse chứa thông tin lịch phỏng vấn
     */
    @GetMapping("/{id}")
    public ApiResponse<OtherIncomeDto> getById(@PathVariable UUID id) {
        return otherIncomeService.getById(id);
    }

    /**
     * Lấy danh sách tất cả lịch phỏng vấn
     * @return ApiResponse chứa danh sách lịch phỏng vấn
     */
    @GetMapping
    public ApiResponse<List<OtherIncomeDto>> getAll() {
        return otherIncomeService.getAll();
    }

    /**
     * Xoá lịch phỏng vấn theo ID
     * @param id UUID của lịch phỏng vấn
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        otherIncomeService.delete(id);
    }

    /**
     * Đánh dấu lịch phỏng vấn là đã xoá (soft delete)
     * @param id UUID của lịch phỏng vấn
     * @return ApiResponse xác nhận đã đánh dấu xoá
     */
    @PutMapping("/mark-deleted/{id}")
    public ApiResponse<Boolean> markDeleted(@PathVariable UUID id) {
        return otherIncomeService.markDeleted(id);
    }

    /**
     * Phân trang danh sách lịch phỏng vấn
     * @param searchDto body bao gồm pageIndex và pageSize
     * @return ApiResponse chứa danh sách các lịch phỏng vấn
     */
    @PostMapping("/paging")
    public ApiResponse<Page<OtherIncomeDto>> paging(@RequestBody SearchOtherIncomeDto searchDto) {
        return otherIncomeService.paging(searchDto);
    }

    @PostMapping("/deleteLists")
    public ApiResponse<Boolean> deleteLists(@RequestBody List<UUID> ids) {
        return otherIncomeService.deleteLists(ids);
    }

}
