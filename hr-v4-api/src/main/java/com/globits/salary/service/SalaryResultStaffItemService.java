package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Staff;
import com.globits.salary.domain.SalaryPeriod;
import com.globits.salary.domain.SalaryResult;
import com.globits.salary.domain.SalaryResultStaff;
import com.globits.salary.domain.SalaryResultStaffItem;
import com.globits.salary.dto.SalaryResultStaffItemDto;
import com.globits.salary.dto.excel.ImportSalaryResultStaffDto;
import com.globits.salary.dto.excel.SalaryResultStaffItemImportDto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface SalaryResultStaffItemService extends GenericService<SalaryResultStaffItem, UUID> {
    void detectAndAutoGenerateValue(SalaryResultStaffItem cell);

    void generateResultStaffItems(SalaryResultStaff row, SalaryResult entity);

    void importItemValueAndRecalculateStaffPayslip(List<ImportSalaryResultStaffDto> staffAndImportValues);

    List<SalaryResultStaffItemDto> importSalaryResultStaffItemValue(SalaryResultStaffItemImportDto dto);

    void updateTimekeepingDataForPayslips(UUID staffId, Date requestDate);

    void updateTimekeepingDataForPayslips(Staff staff, Date date);

    void createAndImportSalaryResultItem(Staff staff, SalaryPeriod salaryPeriod, Double value,
                                         String salaryItemCode);
}