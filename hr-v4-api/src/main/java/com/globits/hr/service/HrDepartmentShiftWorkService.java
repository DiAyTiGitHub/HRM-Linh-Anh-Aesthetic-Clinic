package com.globits.hr.service;

import com.globits.core.service.GenericService;
import com.globits.hr.domain.Bank;
import com.globits.hr.domain.HRDepartment;
import com.globits.hr.domain.HrDepartmentShiftWork;
import com.globits.hr.domain.ShiftWork;
import com.globits.hr.dto.BankDto;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.ShiftWorkDto;
import com.globits.hr.dto.search.SearchDto;
import com.globits.salary.domain.SalaryResult;
import com.globits.salary.dto.SalaryResultDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface HrDepartmentShiftWorkService extends GenericService<HrDepartmentShiftWork, UUID> {
    List<HRDepartmentDto> getDepartmentsHasShiftWork(UUID shiftWorkId);

    List<ShiftWorkDto> getShiftWorksOfDepartment(UUID departmentId);

    void generateHrDepartmentShiftWork(HRDepartmentDto dto, HRDepartment entity);

    void generateHrDepartmentShiftWork(ShiftWorkDto dto, ShiftWork entity);

}
