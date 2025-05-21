package com.globits.salary.service;

import java.util.List;
import java.util.UUID;

import com.globits.core.Constants;
import com.globits.hr.HrConstants;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.salary.dto.SalaryResultStaffDto;
import com.globits.salary.dto.SalaryTemplateItemDto;
import com.globits.salary.dto.excel.ImportSalaryResultStaffDto;
import com.globits.salary.dto.search.SearchSalaryResultStaffDto;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;

import com.globits.core.service.GenericService;
import com.globits.salary.domain.SalaryResult;
import com.globits.salary.dto.SalaryResultDto;
import com.globits.salary.dto.search.SearchSalaryResultDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface SalaryResultService extends GenericService<SalaryResult, UUID> {

    SalaryResultDto saveOrUpdate(SalaryResultDto dto);

    // Khóa bảng lương
    Boolean lockPayroll(UUID salaryResultId);

    // Hủy khóa bảng lương
    Boolean unlockPayroll(UUID salaryResultId);

    List<SalaryResultDto> updateStatus(List<UUID> ids, int status);

    SalaryResultDto saveBoardConfigOfSalaryResultV2(SalaryResultDto dto);

    Page<SalaryResultDto> searchByPage(SearchSalaryResultDto searchDto);

    SalaryResultDto getById(UUID id);

    SalaryResultDto getBasicInfoById(UUID id);


    Boolean remove(UUID id);

    Boolean removeMultiple(List<UUID> ids);

    SalaryResultDto findByCode(String code);

    Boolean isValidCode(SalaryResultDto dto);

    // view salary result board detail
    SalaryResultDto getSalaryResultBoard(UUID id);

    // view config of salary result board
    SalaryResultDto getConfigSalaryResult(UUID id);

    Workbook handleExcel(UUID id);

    public Boolean isValidToCreateSalaryBoard(SalaryResultDto dto);

    SalaryResultDto createSalaryBoardByPeriodAndTemplate(SalaryResultDto dto);

    SalaryResultDto recalculateSalaryBoard(UUID salaryResultId);

    List<SalaryTemplateItemDto> getListTemplateItem(UUID salaryResultId);

    // Lấy mẫu import excel giá trị lương theo bộ lọc
    Workbook exportFileImportSalaryValueByFilter(SearchSalaryResultStaffDto dto);

    // view salary result board by search
    SalaryResultDto searchSalaryResultBoard(SearchSalaryResultStaffDto dto);


}