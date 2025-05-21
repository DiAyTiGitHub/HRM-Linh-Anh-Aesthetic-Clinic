package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.SalaryCalculationRequestDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchStaffSocialInsuranceDto;
import com.globits.salary.domain.SalaryResult;
import com.globits.salary.domain.SalaryResultStaff;
import com.globits.salary.domain.SalaryTemplate;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.dto.SalaryResultDto;
import com.globits.salary.dto.SalaryResultStaffDto;
import com.globits.salary.dto.SalaryResultStaffItemDto;
import com.globits.salary.dto.SalaryResultStaffPaySlipDto;
import com.globits.salary.dto.SalaryTemplateDto;
import com.globits.salary.dto.excel.ImportSalaryResultStaffDto;
import com.globits.salary.dto.search.CalculateSalaryRequest;
import com.globits.salary.dto.search.SearchSalaryResultDto;
import com.globits.salary.dto.search.SearchSalaryResultStaffDto;
import org.springframework.data.domain.Page;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

public interface SalaryResultStaffService extends GenericService<SalaryResultStaff, UUID> {
    void generateSalaryResultStaffs(SalaryResultDto dto, SalaryResult entity);

    SalaryResultStaffDto saveResultStaff(SalaryResultStaffDto dto);

    Boolean deleteSalaryResultStaff(UUID resultStaffId);

    Page<SalaryResultStaffDto> pagingSalaryResultStaff(SearchSalaryResultStaffDto searchDto);

    Boolean removeMultiple(List<UUID> ids);

    Boolean updateApprovalStatus(SearchSalaryResultStaffDto dto) throws Exception;

    Boolean updatePaidStatus(SearchSalaryResultStaffDto dto) throws Exception;

    SalaryResultDto viewSalaryResult(SalaryResultDto dto);

    void importSalaryResultStaffItemTemplate(InputStream is, List<ImportSalaryResultStaffDto> list) throws IOException;

    SalaryResultDto calculateSalaryStaffs(SearchSalaryResultDto dto);

    byte[] generatePayslipPdf(UUID salaryResultStaffId, UUID staffSignatureId);

    byte[] exportPdf(SearchSalaryResultStaffDto dto);

    // Tạo và tính toán phiếu lương
    SalaryResultStaffDto calculateSalaryStaff(SalaryResultStaffDto dto);

    // Cập nhật phiếu lương
    SalaryResultStaffDto recalculateSalaryStaff(SalaryResultStaffDto dto);

    SalaryResultStaffDto handleSetFullSalaryTemplate(SalaryResultStaffDto dto);

    SalaryResultStaffDto getTotalSalaryResultStaff(SearchSalaryResultStaffDto searchDto);

    // Tự động tạo mới/cập nhật toàn bộ các phiếu lương của nhân viên trong kỳ lương
//    List<SalaryResultStaff> autoGenerateStaffPaySlips(CalculateSalaryRequest dto);
}