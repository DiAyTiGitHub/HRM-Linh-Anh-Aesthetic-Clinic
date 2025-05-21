package com.globits.timesheet.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.Bank;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.hr.dto.BankDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchLeaveTypeDto;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.timesheet.domain.LeaveRequest;
import com.globits.timesheet.domain.LeaveType;
import com.globits.timesheet.dto.LeaveTypeDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface LeaveTypeService extends GenericService<LeaveType, UUID> {
    LeaveTypeDto getById(UUID id);

    LeaveTypeDto findOneByCode(String code);

    Boolean isValidCode(LeaveTypeDto dto);

    LeaveTypeDto saveOrUpdate(LeaveTypeDto dto);

    Boolean deleteById(UUID id);

    Integer deleteMultiple(List<UUID> ids);

    Page<LeaveTypeDto> searchByPage(SearchLeaveTypeDto dto);

    List<LeaveTypeDto> getListLeaveTypeDto();

    void handleSetLeaveTypeForStaffWorkSchedule(StaffWorkSchedule entity);

    // Lấy loại nghỉ nửa ngày của loại nghỉ tương ứng
    LeaveType getHalfLeaveOfLeaveType(LeaveType leaveType);

    // Lấy loại nghỉ cả ngày của loại nghỉ tương ứng
    LeaveType getFullLeaveOfLeaveType(LeaveType leaveType);
}
