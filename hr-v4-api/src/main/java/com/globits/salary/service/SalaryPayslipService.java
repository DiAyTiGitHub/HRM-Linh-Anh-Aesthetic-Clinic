package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.salary.domain.SalaryResult;
import com.globits.salary.domain.SalaryResultStaff;
import com.globits.salary.dto.SalaryResultStaffDto;
import com.globits.salary.dto.SalaryResultStaffPaySlipDto;
import com.globits.salary.dto.excel.ImportSalaryResultStaffDto;
import com.globits.salary.dto.search.SalaryCalculatePayslipDto;
import com.globits.salary.dto.search.SearchSalaryResultDto;
import com.globits.salary.dto.search.SearchSalaryResultStaffDto;
import com.globits.salary.dto.search.SearchStaffAdvancePaymentDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface SalaryPayslipService extends GenericService<SalaryResultStaff, UUID> {
    void autoCalculateCellValueInEachRow(SalaryResult entity);

    SalaryResultStaffDto reCalculateRowByChangingCellValue(SalaryCalculatePayslipDto dto);

    void importItemValueForPayslips(List<ImportSalaryResultStaffDto> staffAndImportValues, SearchSalaryResultStaffDto searchDto);

    // CRUD
    SalaryResultStaffPaySlipDto saveOrUpdate(SalaryResultStaffPaySlipDto dto);

    SalaryResultStaffPaySlipDto getById(UUID id);

    Page<SalaryResultStaffPaySlipDto> pagingSalaryPayslip(SearchSalaryResultStaffDto searchDto);

    // renew salary result board
    SalaryResultStaffDto renewPayslip(UUID payslipId);

    // Số lượng phiếu lương có thể thuộc bảng lương nhưng chưa được tổng hợp
    Integer hasAnyOrphanedPayslips(UUID salaryResultId);

    // Lấy danh sách phiếu lương có thể tổng hợp vào bảng lương
    List<SalaryResultStaffDto> getAllOrphanedPayslips(UUID salaryResultId);

    // Tổng hợp các phiếu lương được chọn vào bảng lương
    Boolean mergeOrphanedPayslips(SearchSalaryResultStaffDto dto);

    SearchSalaryResultStaffDto getInitialFilter();
}