package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffInsuranceHistory;
import com.globits.hr.domain.StaffSocialInsurance;
import com.globits.hr.dto.StaffLeaveDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.hr.dto.search.SearchStaffSocialInsuranceDto;
import com.globits.hr.dto.staff.StaffInsuranceHistoryDto;
import com.globits.hr.dto.staff.StaffSocialInsuranceDto;
import com.globits.salary.domain.SalaryResultStaff;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StaffInsuranceHistoryService extends GenericService<StaffInsuranceHistory, UUID> {
    Page<StaffInsuranceHistoryDto> searchByPage(SearchDto dto);

    StaffInsuranceHistoryDto getById(UUID id);

    StaffInsuranceHistoryDto saveOrUpdate(StaffInsuranceHistoryDto dto);

    Boolean deleteById(UUID id);

    Boolean deleteMultiple(List<UUID> ids);
}
