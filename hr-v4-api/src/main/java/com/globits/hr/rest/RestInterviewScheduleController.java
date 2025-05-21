package com.globits.hr.rest;

import com.globits.hr.dto.ApiResponse;
import com.globits.hr.dto.CreateInterviewSchedulesDto;
import com.globits.hr.dto.InterviewScheduleDto;
import com.globits.hr.dto.search.InterviewScheduleSearchDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.service.InterviewScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/interview-schedule")
public class RestInterviewScheduleController {

    @Autowired
    private InterviewScheduleService interviewScheduleService;

    /**
     * Tạo mới hoặc cập nhật lịch phỏng vấn
     * @param dto Dữ liệu lịch phỏng vấn cần tạo hoặc cập nhật
     * @return ApiResponse chứa lịch phỏng vấn đã được tạo hoặc cập nhật
     */
    @PostMapping("/save")
    public ApiResponse<InterviewScheduleDto> saveOrUpdate(@RequestBody InterviewScheduleDto dto) {
        return interviewScheduleService.save(dto);
    }

    @PostMapping("/save-multiple")
    public ApiResponse<Integer> saveInterviewSchedules(@RequestBody CreateInterviewSchedulesDto dto) {
        return interviewScheduleService.saveMultiple(dto);
    }

    /**
     * Lấy thông tin lịch phỏng vấn theo ID
     * @param id UUID của lịch phỏng vấn
     * @return ApiResponse chứa thông tin lịch phỏng vấn
     */
    @GetMapping("/{id}")
    public ApiResponse<InterviewScheduleDto> getById(@PathVariable UUID id) {
        return interviewScheduleService.getById(id);
    }

    /**
     * Lấy danh sách tất cả lịch phỏng vấn
     * @return ApiResponse chứa danh sách lịch phỏng vấn
     */
    @GetMapping
    public ApiResponse<List<InterviewScheduleDto>> getAll() {
        return interviewScheduleService.getAll();
    }

    /**
     * Xoá lịch phỏng vấn theo ID
     * @param id UUID của lịch phỏng vấn
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        interviewScheduleService.delete(id);
    }

    /**
     * Đánh dấu lịch phỏng vấn là đã xoá (soft delete)
     * @param id UUID của lịch phỏng vấn
     * @return ApiResponse xác nhận đã đánh dấu xoá
     */
    @PutMapping("/mark-deleted/{id}")
    public ApiResponse<Boolean> markDeleted(@PathVariable UUID id) {
        return interviewScheduleService.markDeleted(id);
    }

    /**
     * Phân trang danh sách lịch phỏng vấn
     * @param searchDto body bao gồm pageIndex và pageSize
     * @return ApiResponse chứa danh sách các lịch phỏng vấn
     */
    @PostMapping("/paging")
    public ApiResponse<Page<InterviewScheduleDto>> paging(@RequestBody InterviewScheduleSearchDto searchDto) {
        return interviewScheduleService.paging(searchDto);
    }
}
