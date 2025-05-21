package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.StaffSocialInsurance;
import com.globits.hr.dto.search.SearchStaffSocialInsuranceDto;
import com.globits.hr.dto.staff.StaffSocialInsuranceDto;
import com.globits.salary.domain.SalaryResultStaff;
import com.globits.salary.dto.search.SearchSalaryResultStaffDto;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface StaffSocialInsuranceService extends GenericService<StaffSocialInsurance, UUID> {
    StaffSocialInsuranceDto saveStaffSocialInsurance(StaffSocialInsuranceDto dto);

    Boolean deleteStaffSocialInsurance(UUID id);

    StaffSocialInsuranceDto getStaffSocialInsurance(UUID id);

    Boolean updateStaffSocialInsurancePaidStatus(SearchStaffSocialInsuranceDto dto) throws Exception;

    SearchStaffSocialInsuranceDto getInitialFilter();

    Page<StaffSocialInsuranceDto> searchByPage(SearchStaffSocialInsuranceDto dto);

    StaffSocialInsuranceDto generateFromResultStaff(SalaryResultStaff resultStaff);

    void handleSocialInsuranceByChangingStatus(SalaryResultStaff entity, Integer newApprovalStatus);

    Boolean deleteMultiple(List<UUID> ids);

    Object calculateInsAmmount(SearchStaffSocialInsuranceDto dto);

    Workbook exportStaffSocialInsurance(SearchStaffSocialInsuranceDto dto);

    List<UUID> generateSocialInsuranceTicketsForStaffsBySalaryPeriod(SearchStaffSocialInsuranceDto dto);

    StaffSocialInsuranceDto generateSingleSocialInsuranceTicket(SearchStaffSocialInsuranceDto dto);
}
