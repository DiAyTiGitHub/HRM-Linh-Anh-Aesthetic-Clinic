package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.salary.domain.StaffAdvancePayment;
import com.globits.salary.dto.StaffAdvancePaymentDto;
import com.globits.salary.dto.search.SearchStaffAdvancePaymentDto;
import com.globits.timesheet.dto.search.SearchWorkScheduleCalendarDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StaffAdvancePaymentService extends GenericService<StaffAdvancePayment, UUID> {
    StaffAdvancePaymentDto saveStaffAdvancePayment(StaffAdvancePaymentDto dto);

    Boolean deleteStaffAdvancePayment(UUID id);

    StaffAdvancePaymentDto getStaffAdvancePayment(UUID id);

    Boolean updateStaffAdvancePaymentApprovalStatus(SearchStaffAdvancePaymentDto dto) throws Exception;

    Page<StaffAdvancePaymentDto> searchByPage(SearchStaffAdvancePaymentDto dto);

    Boolean deleteMultiple(List<UUID> ids);

    SearchStaffAdvancePaymentDto getInitialFilter();
}
