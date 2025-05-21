package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffAnnualLeaveHistory;
import com.globits.hr.domain.StaffMonthlyLeaveHistory;
import com.globits.hr.dto.StaffAnnualLeaveHistoryDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.salary.dto.SalaryTemplateDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StaffMonthlyLeaveHistoryService extends GenericService<StaffMonthlyLeaveHistory, UUID> {
    void handleSetMonthlyLeaveHistoryForAnnualLeave(StaffAnnualLeaveHistory entity, StaffAnnualLeaveHistoryDto dto);
}
