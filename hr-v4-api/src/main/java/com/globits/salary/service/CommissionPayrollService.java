package com.globits.salary.service;

import com.globits.core.service.GenericService;
import com.globits.salary.domain.SalaryResult;
import com.globits.salary.dto.SalaryResultDto;
import com.globits.salary.dto.SalaryTemplateItemDto;
import com.globits.salary.dto.excel.CommissionPayrollItem;
import com.globits.salary.dto.excel.CommissionPayrollItemDetail;
import com.globits.salary.dto.search.SearchSalaryResultDto;
import com.globits.salary.dto.search.SearchSalaryResultStaffDto;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

// Bảng lương hoa hồng
public interface CommissionPayrollService {
    // Lấy excel bảng lương hoa hồng
    Workbook exportExcelCommissionPayroll(SearchSalaryResultStaffDto dto);


    List<CommissionPayrollItemDetail> getTargetNhan(UUID staffId, UUID salaryPeriodId);
    double getSumTargetNhan(UUID staffId, UUID salaryPeriodId);

    List<CommissionPayrollItemDetail> getDoanhSoDatDuoc(UUID staffId, UUID salaryPeriodId);
    double getSumDoanhSoDatDuoc(UUID staffId, UUID salaryPeriodId);

    List<CommissionPayrollItemDetail> getTgKhachMoi(UUID staffId, UUID salaryPeriodId);
    double getSumTgKhachMoi(UUID staffId, UUID salaryPeriodId);

    List<CommissionPayrollItemDetail> getDsKhachMoi(UUID staffId, UUID salaryPeriodId);
    double getSumDsKhachMoi(UUID staffId, UUID salaryPeriodId);

    List<CommissionPayrollItemDetail> getTgKhachCu(UUID staffId, UUID salaryPeriodId);
    double getSumTgKhachCu(UUID staffId, UUID salaryPeriodId);

    List<CommissionPayrollItemDetail> getDsKhachCu(UUID staffId, UUID salaryPeriodId);
    double getSumDsKhachCu(UUID staffId, UUID salaryPeriodId);

    List<CommissionPayrollItemDetail> getDsVuot(UUID staffId, UUID salaryPeriodId);
    double getSumDsVuot(UUID staffId, UUID salaryPeriodId);

    List<CommissionPayrollItemDetail> getLuongKPI(UUID staffId, UUID salaryPeriodId);
    double getSumLuongKPI(UUID staffId, UUID salaryPeriodId);

    List<CommissionPayrollItemDetail> getTienTour(UUID staffId, UUID salaryPeriodId);
    double getSumTienTour(UUID staffId, UUID salaryPeriodId);

    List<CommissionPayrollItemDetail> getTrachNhiem(UUID staffId, UUID salaryPeriodId);
    double getSumTrachNhiem(UUID staffId, UUID salaryPeriodId);

    List<CommissionPayrollItemDetail> getThuong(UUID staffId, UUID salaryPeriodId);
    double getSumThuong(UUID staffId, UUID salaryPeriodId);

    List<CommissionPayrollItemDetail> getTruKhac(UUID staffId, UUID salaryPeriodId);
    double getSumTruKhac(UUID staffId, UUID salaryPeriodId);

    List<CommissionPayrollItemDetail> getLuongBoSung(UUID staffId, UUID salaryPeriodId);
    double getSumLuongBoSung(UUID staffId, UUID salaryPeriodId);

    List<CommissionPayrollItemDetail> getLuongThucLinh(UUID staffId, UUID salaryPeriodId, CommissionPayrollItem cmpItem);
    double getSumLuongThucLinh(UUID staffId, UUID salaryPeriodId, CommissionPayrollItem cmpItem);
}