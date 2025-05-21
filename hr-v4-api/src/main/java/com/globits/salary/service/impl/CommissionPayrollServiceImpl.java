package com.globits.salary.service.impl;

import com.globits.core.domain.Department;
import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.PersonBankAccount;
import com.globits.hr.domain.Staff;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.search.SearchHrDepartmentDto;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.dto.search.SearchStaffSalaryTemplateDto;
import com.globits.hr.dto.staff.StaffWithTitleDto;
import com.globits.hr.repository.PersonBankAccountRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.HRDepartmentService;
import com.globits.hr.service.StaffService;
import com.globits.hr.service.StaffSocialInsuranceService;
import com.globits.hr.utils.ExportExcelUtil;
import com.globits.salary.domain.*;
import com.globits.salary.dto.*;
import com.globits.salary.dto.excel.CommissionPayrollItem;
import com.globits.salary.dto.excel.CommissionPayrollItemDetail;
import com.globits.salary.dto.search.SearchSalaryResultDto;
import com.globits.salary.dto.search.SearchSalaryResultStaffDto;
import com.globits.salary.repository.*;
import com.globits.salary.service.*;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommissionPayrollServiceImpl implements CommissionPayrollService {
    private static final Logger logger = LoggerFactory.getLogger(CommissionPayrollServiceImpl.class);

    private static final List<String> TG_KHACH_CU = List.of(
            "TG_KH_CU_TCS_HCM",
            "TG_KH_CU_TVHD1_HCM",
            "TG_KH_CU_TVHD2_HCM",
            "TG_KH_CU_KTV_HCM",
            "TG_KH_CU_TCS_TINH",
            "TG_KH_CU_TVHD1_TINH",
            "TG_KH_CU_TVHD2_TINH",
            "TG_KH_CU_KTV_TINH"
    );

    private static final List<String> TG_KHACH_MOI = List.of(
            "TG_KH_MOI_TCS_HCM",
            "TG_KH_MOI_TVHD1_HCM",
            "TG_KH_MOI_TVHD2_HCM",
            "TG_KH_MOI_KTV_HCM",
            "TG_KH_MOI_TCS_TINH",
            "TG_KH_MOI_TVHD1_TINH",
            "TG_KH_MOI_TVHD2_TINH",
            "TG_KH_MOI_KTV_TINH"
    );

    private static final List<String> DS_KHACH_CU = List.of(
            "DS_KH_CU_TCS_HCM",
            "DS_KH_CU_TVHD1_HCM",
            "DS_KH_CU_TVHD2_HCM",
            "DS_KH_CU_KTV_HCM",
            "DS_KH_CU_TCS_TINH",
            "DS_KH_CU_TVHD1_TINH",
            "DS_KH_CU_TVHD2_TINH",
            "DS_KH_CU_KTV_TINH"
    );

    private static final List<String> DS_KHACH_MOI = List.of(
            "DS_KH_MOI_TCS_HCM",
            "DS_KH_MOI_TVHD1_HCM",
            "DS_KH_MOI_TVHD2_HCM",
            "DS_KH_MOI_KTV_HCM",
            "DS_KH_MOI_TCS_TINH",
            "DS_KH_MOI_TVHD1_TINH",
            "DS_KH_MOI_TVHD2_TINH",
            "DS_KH_MOI_KTV_TINH"
    );

    private static final List<String> TARGET_NHAN = List.of(
            "TONG_TG_TCS_HCM",
            "TONG_TG_TCS_TINH",
            "TONG_TG_TVHD1_HCM",
            "TONG_TG_TVHD1_TINH",
            "TONG_TG_TVHD2_HCM",
            "TONG_TG_TVHD2_TINH",
            "TONG_TG_TRUONG_TVHD2",
            "TONG_TG_KTV_HCM",
            "TONG_TG_KTV_TINH",
            "TONG_TG_TSL1_HCM",
            "TONG_TG_TSL1_TINH",
            "TONG_TG_TSL2_HCM",
            "TONG_TG_TSL2_TINH",
            "TONG_TG_CSKH",
            "TONG_TG_LEADER_TLS_CSKH"
    );

    private static final List<String> DOANH_SO_DAT_DUOC = List.of(
            "TONG_DS_TCS_HCM",
            "TONG_DS_TCS_TINH",
            "TONG_DS_TVHD1_HCM",
            "TONG_DS_TVHD1_TINH",
            "TONG_DS_TVHD2_HCM",
            "TONG_DS_TVHD2_TINH",
            "TONG_DS_TRUONG_TVHD2",
            "TONG_DS_KTV_HCM",
            "TONG_DS_KTV_TINH",
            "TONG_DS_TSL1_HCM",
            "TONG_DS_TSL1_TINH",
            "TONG_DS_TSL2_HCM",
            "TONG_DS_TSL2_TINH",
            "TONG_DS_CSKH",
            "TONG_DS_LEADER_TLS_CSKH",
            "DOANH_THU_TRUONG_FB_TINH"
    );

    private static final List<String> TIEN_TOUR = List.of(
            "TIEN_TOUR"
    );

    private static final List<String> DS_VUOT = List.of(
            "DS_VUOT_TLS1_TINH",
            "DS_VUOT_TLS1_HCM",
            "DS_VUOT_TLS2_HCM",
            "DS_VUOT_TLS2_TINH",
            "DS_VUOT_LEADER_TSL_CSKH"
    );

    private static final List<String> TRACH_NHIEM = List.of(
            "TRACH_NHIEM_HH"
    );

    private static final List<String> THUONG = List.of(
            "THUONG_HH"
    );

    private static final List<String> TRU_KHAC = List.of(
            "TRU_KHAC_HH"
    );

    private static final List<String> LUONG_BO_SUNG = List.of(
            "LUONG_BO_SUNG"
    );

    private static final List<String> LUONG_KPI = List.of(
            "LUONG_KPI_TCS_HCM",
            "LUONG_KPI_TCS_TINH",
            "LUONG_KPI_TVHD1_TINH",
            "LUONG_KPI_TVHD1_HCM",
            "LUONG_KPI_BSI",
            "LUONG_KPI_TVHD2_TINH",
            "LUONG_KPI_TVHD2_HCM",
            "LUONG_KPI_TRUONG_TVHD2",
            "LUONG_KPI_KTV_HCM",
            "LUONG_KPI_KTV_TINH",
            "LUONG_KPI_TLS1_TINH",
            "LUONG_KPI_TLS1_HCM",
            "LUONG_KPI_TLS2_TINH",
            "LUONG_KPI_TLS2_HCM",
            "LUONG_KPI_CSKH",
            "LUONG_KPI_LEADER_TLS_CSKH",
            "LUONG_KPI_TRUONG_FB",
            "LUONG_KPI_CV_FB",
            "LUONG_KPI_TRUONG_GGZL",
            "LUONG_KPI_CV_GGZL",
            "LUONG_KPI_TRUONG_TT",
            "LUONG_KPI_CV_TT",
            "LUONG_KPI_CHO_CAC_BO_PHAN_KHAC",
            "LUONG_KPI_NV_CONTENT_CREATOR",
            "LUONG_KPI_TRUONG_SANG_TAO",
            "LUONG_KPI_NV_CONTENT",
            "LUONG_KPI_CV_TT",
            "LUONG_KPI_GD_TT",
            "LUONG_KPI_TRUONG_TT",
            "LUONG_KPI_CV_GGZL",
            "LUONG_KPI_TRUONG_GGZL",
            "LUONG_KPI_TRUC_PAGE_HCM",
            "LUONG_KPI_TRUC_PAGE_TINH"
    );


    public static List<String> getAllFieldsInCommissionPayroll() {
        List<String> allFields = new ArrayList<>();
        allFields.addAll(TG_KHACH_CU);
        allFields.addAll(TG_KHACH_MOI);
        allFields.addAll(DS_KHACH_CU);
        allFields.addAll(DS_KHACH_MOI);
        allFields.addAll(TARGET_NHAN);
        allFields.addAll(DOANH_SO_DAT_DUOC);
        allFields.addAll(TIEN_TOUR);
        allFields.addAll(DS_VUOT);
        allFields.addAll(TRACH_NHIEM);
        allFields.addAll(THUONG);
        allFields.addAll(TRU_KHAC);
        allFields.addAll(LUONG_BO_SUNG);
        allFields.addAll(LUONG_KPI);
        return allFields;
    }


    private static LocalDateTime logTime(String message) {
        LocalDateTime currentTime = LocalDateTime.now();
        String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info(message + " at " + formattedTime);

        return currentTime;
    }

    private static LocalDateTime logTime(String message, LocalDateTime previousTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        String formattedTime = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String distance = previousTime == null ? "N/A" : Duration.between(previousTime, currentTime).toMillis() + " ms";

        logger.info(message + " at " + formattedTime + ", distance: " + distance);

        return currentTime;
    }

    @Autowired
    private SalaryPeriodRepository salaryPeriodRepository;

    @Autowired
    private SalaryResultRepository salaryResultRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SalaryResultStaffRepository salaryResultStaffRepository;

    @Autowired
    private SalaryResultStaffItemRepository salaryResultStaffItemRepository;

    @Autowired
    private SalaryTemplateRepository salaryTemplateRepository;

    @Autowired
    private SalaryTemplateItemRepository salaryTemplateItemRepository;

    @Autowired
    private SalaryResultStaffItemService salaryResultStaffItemService;

    @Autowired
    private SalaryResultItemRepository salaryResultItemRepository;

    @Autowired
    private SalaryResultItemGroupRepository salaryResultItemGroupRepository;

    @Autowired
    private SalaryResultItemGroupService salaryResultItemGroupService;

    @Autowired
    private SalaryResultItemService salaryResultItemService;

    @Autowired
    private SalaryItemRepository salaryItemRepository;

    @Autowired
    private SalaryItemService salaryItemService;

    @Autowired
    private StaffSocialInsuranceService staffSocialInsuranceService;

    @Autowired
    private SalaryPayslipService salaryPayslipService;

    @Autowired
    private SalaryResultStaffService salaryResultStaffService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private StaffSalaryTemplateService staffSalaryTemplateService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private HRDepartmentService hrDepartmentService;

    @Autowired
    private PersonBankAccountRepository personBankAccountRepository;


    public static void configExcelStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Times New Roman");
        font.setBold(true);

        cellStyle.setWrapText(true);

        Font fontNoBorder = workbook.createFont();
        fontNoBorder.setFontHeightInPoints((short) 10);
        fontNoBorder.setBold(true);
        fontNoBorder.setFontName("Times New Roman");

        CellStyle cellStyleNoBoder = workbook.createCellStyle();
        cellStyleNoBoder.setWrapText(true);
        cellStyleNoBoder.setFont(fontNoBorder);

        CellStyle cellStyleBoldTable = workbook.createCellStyle();
        cellStyleBoldTable.setWrapText(true);
        cellStyleBoldTable.setFont(font);
    }

    private List<HRDepartmentDto> getDepartmentsHasCMP() {
        SearchHrDepartmentDto searchHrDepartmentDto = new SearchHrDepartmentDto();
        searchHrDepartmentDto.setPageIndex(1);
        searchHrDepartmentDto.setPageSize(99999999);

        Page<HRDepartmentDto> pageDepartments = hrDepartmentService.pagingDepartments(searchHrDepartmentDto);

        if (pageDepartments == null || pageDepartments.isEmpty())
            return new ArrayList<>();

        return pageDepartments.getContent();
    }

    private List<StaffDto> getStaffsHaveRelatedPayslips(SearchSalaryResultStaffDto searchDto) {
        if (searchDto == null) {
            return null;
        }

        StringBuilder sql = new StringBuilder();
        sql.append("select new com.globits.hr.dto.staff.StaffWithTitleDto(entity.staff, pos.title.name) ")
                .append("from SalaryResultStaff as entity ")
                .append("join entity.salaryResultStaffItems payslipItem on payslipItem.salaryResultStaff.id = entity.id ")
                .append("join Position pos on pos.isMain = true and pos.staff.id = entity.staff.id ");

        StringBuilder whereClause = new StringBuilder(" where (1=1) ");
        if (searchDto.getPositionTitleId() != null) {
            whereClause.append(" and pos.title.id = :positionTitleId ");
        }

        if (searchDto.getDepartmentId() != null) {
            whereClause.append(" and pos.department.id = :departmentId ");
        }

        if (searchDto.getOrganizationId() != null) {
            whereClause.append(" and pos.department.parent.id = :organizationId ");
        }

        if (searchDto.getSalaryPeriod() != null || searchDto.getSalaryPeriodId() != null) {
            whereClause.append(" and entity.salaryPeriod.id = :salaryPeriodId ");
        }

        if (searchDto.getStaffId() != null) {
            whereClause.append(" and entity.staff.id = :staffId ");
        }

        whereClause.append(" and trim(payslipItem.referenceCode) in :cmpFields ");

        String orderBy = " ORDER BY pos.title.name ASC, entity.staff.staffCode DESC";

        String finalQuery = sql.toString() + whereClause.toString() + orderBy;

        TypedQuery<StaffWithTitleDto> query = entityManager.createQuery(finalQuery, StaffWithTitleDto.class);

        // Set parameters
        List<String> cmpFields = getAllFieldsInCommissionPayroll();
        query.setParameter("cmpFields", cmpFields);

        if (searchDto.getSalaryPeriod() != null || searchDto.getSalaryPeriodId() != null) {
            UUID salaryPeriodId = (searchDto.getSalaryPeriod() != null)
                    ? searchDto.getSalaryPeriod().getId()
                    : searchDto.getSalaryPeriodId();
            query.setParameter("salaryPeriodId", salaryPeriodId);
        }

        if (searchDto.getStaffId() != null) {
            query.setParameter("staffId", searchDto.getStaffId());
        }

        if (searchDto.getPositionTitleId() != null) {
            query.setParameter("positionTitleId", searchDto.getPositionTitleId());
        }

        if (searchDto.getDepartmentId() != null) {
            query.setParameter("departmentId", searchDto.getDepartmentId());
        }

        if (searchDto.getOrganizationId() != null) {
            query.setParameter("organizationId", searchDto.getOrganizationId());
        }

        List<StaffWithTitleDto> rawList = query.getResultList();

        // Loại trùng staff theo ID, giữ lại staff đầu tiên (theo thứ tự đã sort)
        Map<UUID, StaffWithTitleDto> staffMap = new LinkedHashMap<>();
        for (StaffWithTitleDto dto : rawList) {
            UUID id = dto.getStaff().getId();
            staffMap.putIfAbsent(id, dto);
        }

        // Trả ra danh sách đã loại trùng, vẫn giữ order theo title.name và staffCode DESC
        return staffMap.values().stream()
                .map(StaffWithTitleDto::getStaff)
                .collect(Collectors.toList());
    }


    private List<CommissionPayrollItem> getCMPIOfStaffsInDepartment(UUID departmentId, UUID salaryPeriodId) {
        List<CommissionPayrollItem> response = new ArrayList<>();

        if (departmentId == null || salaryPeriodId == null) {
            return response;
        }

        SearchSalaryResultStaffDto searchDto = new SearchSalaryResultStaffDto();

        searchDto.setSalaryPeriodId(salaryPeriodId);
        searchDto.setDepartmentId(departmentId);

        List<StaffDto> staffsInDepartment = this.getStaffsHaveRelatedPayslips(searchDto);
        if (staffsInDepartment == null || staffsInDepartment.isEmpty()) {
            return response;
        }

        for (StaffDto staff : staffsInDepartment) {
            CommissionPayrollItem cmpItem = this.buildCMPItem(staff, departmentId, salaryPeriodId);

            if (cmpItem == null) continue;

            response.add(cmpItem);
        }

        return response;
    }

    private CommissionPayrollItem buildCMPItem(StaffDto staffDto, UUID departmentId, UUID salaryPeriodId) {
        if (salaryPeriodId == null || departmentId == null || staffDto == null) return null;

        int maxPlusRow = 1;

        CommissionPayrollItem cmpItem = new CommissionPayrollItem();
        cmpItem.setMaximumPlus(maxPlusRow);

        // 1. Empl. Code
        cmpItem.setStaffCode(staffDto.getStaffCode());

        // 2. Vietnamese Name
        cmpItem.setDisplayName(staffDto.getDisplayName());

        // 4. Position
        String positionTitleName = "";
        if (staffDto.getPositionTitle() != null) {
            positionTitleName = staffDto.getPositionTitle().getName();
        }
        cmpItem.setPositionTitle(positionTitleName);

        // 5. BAN/PHÒNG/CƠ SỞ
        String departmentName = "";
        if (staffDto.getDepartment() != null) {
            departmentName = staffDto.getDepartment().getName();
        }
        cmpItem.setDepartment(departmentName);

// 6. TARGET NHẬN
        List<CommissionPayrollItemDetail> targetNhan = this.getTargetNhan(staffDto.getId(), salaryPeriodId);
        cmpItem.setTargetNhan(targetNhan);
        maxPlusRow = Math.max(maxPlusRow, targetNhan.size());

// 7. DOANH SỐ ĐẠT ĐƯỢC
        List<CommissionPayrollItemDetail> doanhSoDatDuoc = this.getDoanhSoDatDuoc(staffDto.getId(), salaryPeriodId);
        cmpItem.setDoanhSoDatDuoc(doanhSoDatDuoc);
        maxPlusRow = Math.max(maxPlusRow, doanhSoDatDuoc.size());

// 8. TG KHÁCH MỚI
        List<CommissionPayrollItemDetail> tgKhachMoi = this.getTgKhachMoi(staffDto.getId(), salaryPeriodId);
        cmpItem.setTgKhachMoi(tgKhachMoi);
        maxPlusRow = Math.max(maxPlusRow, tgKhachMoi.size());

// 9. DS KHÁCH MỚI
        List<CommissionPayrollItemDetail> dsKhachMoi = this.getDsKhachMoi(staffDto.getId(), salaryPeriodId);
        cmpItem.setDsKhachMoi(dsKhachMoi);
        maxPlusRow = Math.max(maxPlusRow, dsKhachMoi.size());

// 10. TG KHÁCH CŨ
        List<CommissionPayrollItemDetail> tgKhachCu = this.getTgKhachCu(staffDto.getId(), salaryPeriodId);
        cmpItem.setTgKhachCu(tgKhachCu);
        maxPlusRow = Math.max(maxPlusRow, tgKhachCu.size());

// 11. DS KHÁCH CŨ
        List<CommissionPayrollItemDetail> dsKhachCu = this.getDsKhachCu(staffDto.getId(), salaryPeriodId);
        cmpItem.setDsKhachCu(dsKhachCu);
        maxPlusRow = Math.max(maxPlusRow, dsKhachCu.size());

// 12. DS VƯỢT
        List<CommissionPayrollItemDetail> dsVuot = this.getDsVuot(staffDto.getId(), salaryPeriodId);
        cmpItem.setDsVuot(dsVuot);
        maxPlusRow = Math.max(maxPlusRow, dsVuot.size());

// 13. CÁCH TÍNH %
//        List<CommissionPayrollItemDetail> cachTinhPT = this.getCachTinhPT(staffDto.getId(), salaryPeriodId);
//        cmpItem.setCachTinhPT(cachTinhPT);
//        maxPlusRow = Math.max(maxPlusRow, cachTinhPT.size());

// 14. LƯƠNG KPI
        List<CommissionPayrollItemDetail> luongKPI = this.getLuongKPI(staffDto.getId(), salaryPeriodId);
        cmpItem.setLuongKPI(luongKPI);
        maxPlusRow = Math.max(maxPlusRow, luongKPI.size());

// 15. TIỀN TOUR
        List<CommissionPayrollItemDetail> tienTour = this.getTienTour(staffDto.getId(), salaryPeriodId);
        cmpItem.setTienTour(tienTour);
        maxPlusRow = Math.max(maxPlusRow, tienTour.size());

// 16. TRÁCH NHIỆM
        List<CommissionPayrollItemDetail> trachNhiem = this.getTrachNhiem(staffDto.getId(), salaryPeriodId);
        cmpItem.setTrachNhiem(trachNhiem);
        maxPlusRow = Math.max(maxPlusRow, trachNhiem.size());

// 17. THƯỞNG
        List<CommissionPayrollItemDetail> thuong = this.getThuong(staffDto.getId(), salaryPeriodId);
        cmpItem.setThuong(thuong);
        maxPlusRow = Math.max(maxPlusRow, thuong.size());

// 18. TRỪ KHÁC
        List<CommissionPayrollItemDetail> truKhac = this.getTruKhac(staffDto.getId(), salaryPeriodId);
        cmpItem.setTruKhac(truKhac);
        maxPlusRow = Math.max(maxPlusRow, truKhac.size());

// 19. LƯƠNG BỔ SUNG
        List<CommissionPayrollItemDetail> luongBoSung = this.getLuongBoSung(staffDto.getId(), salaryPeriodId);
        cmpItem.setLuongBoSung(luongBoSung);
        maxPlusRow = Math.max(maxPlusRow, luongBoSung.size());

        cmpItem.setMaximumPlus(maxPlusRow);

// 20. LƯƠNG THỰC LĨNH
        List<CommissionPayrollItemDetail> luongThucLinh = this.getLuongThucLinh(staffDto.getId(), salaryPeriodId, cmpItem);
        cmpItem.setLuongThucLinh(luongThucLinh);

//        if (cmpItem.getStaffCode().equals("LA2409_001858")) {
//            logger.info("catched");
//        }

//        // 6. TARGET NHẬN
//        double targetNhanSum = this.getSumTargetNhan(staffDto.getId(), salaryPeriodId);
//        cmpItem.setTargetNhanSum(targetNhanSum);
//
//        // 7. DOANH SỐ ĐẠT ĐƯỢC
//        double doanhSoDatDuocSum = this.getSumDoanhSoDatDuoc(staffDto.getId(), salaryPeriodId);
//        cmpItem.setDoanhSoDatDuocSum(doanhSoDatDuocSum);
//
//        // 8. TG KHÁCH MỚI
//        double tgKhachMoiSum = this.getSumTgKhachMoi(staffDto.getId(), salaryPeriodId);
//        cmpItem.setTgKhachMoiSum(tgKhachMoiSum);
//
//        // 9. DS KHÁCH MỚI
//        double dsKhachMoiSum = this.getSumDsKhachMoi(staffDto.getId(), salaryPeriodId);
//        cmpItem.setDsKhachMoiSum(dsKhachMoiSum);
//
//        // 10. TG KHÁCH CŨ
//        double tgKhachCuSum = this.getSumTgKhachCu(staffDto.getId(), salaryPeriodId);
//        cmpItem.setTgKhachCuSum(tgKhachCuSum);
//
//        // 11. DS KHÁCH CŨ
//        double dsKhachCuSum = this.getSumDsKhachCu(staffDto.getId(), salaryPeriodId);
//        cmpItem.setDsKhachCuSum(dsKhachCuSum);
//
//        // 12. DS VƯỢT
//        double dsVuotSum = this.getSumDsVuot(staffDto.getId(), salaryPeriodId);
//        cmpItem.setDsVuotSum(dsVuotSum);
//
//        // 14. LƯƠNG KPI
//        double luongKPISum = this.getSumLuongKPI(staffDto.getId(), salaryPeriodId);
//        cmpItem.setLuongKPISum(luongKPISum);
//
//        // 15. TIỀN TOUR
//        double tienTourSum = this.getSumTienTour(staffDto.getId(), salaryPeriodId);
//        cmpItem.setTienTourSum(tienTourSum);
//
//        // 16. TRÁCH NHIỆM
//        double trachNhiemSum = this.getSumTrachNhiem(staffDto.getId(), salaryPeriodId);
//        cmpItem.setTrachNhiemSum(trachNhiemSum);
//
//        // 17. THƯỞNG
//        double thuongSum = this.getSumThuong(staffDto.getId(), salaryPeriodId);
//        cmpItem.setThuongSum(thuongSum);
//
//        // 18. TRỪ KHÁC
//        double truKhacSum = this.getSumTruKhac(staffDto.getId(), salaryPeriodId);
//        cmpItem.setTruKhacSum(truKhacSum);
//
//        // 19. LƯƠNG BỔ SUNG
//        double luongBoSungSum = this.getSumLuongBoSung(staffDto.getId(), salaryPeriodId);
//        cmpItem.setLuongBoSungSum(luongBoSungSum);
//
//        // 20. LƯƠNG THỰC LĨNH
//        double luongThucLinhSum = this.getSumLuongThucLinh(staffDto.getId(), salaryPeriodId, cmpItem);
//        cmpItem.setLuongThucLinhSum(luongThucLinhSum);

        // 21. Ghi chú
        String note = "";
        cmpItem.setNote(note);

        // 22. EMAIL
        String email = "";
        if (staffDto.getEmail() != null) {
            email = staffDto.getEmail();
        }
        cmpItem.setEmail(email);

        // 23. PASS
        String password = "";
        cmpItem.setPassword(password);

        // 24. (Empty/Skipped index, possibly used for formatting)
        String blankCell = "";
        cmpItem.setBlankCell(blankCell);


        PersonBankAccount staffAccount = this.getStaffBankAccount(staffDto.getId());

        // 25. SỐ TÀI KHOẢN
        String bankAccount = "";
        if (staffAccount != null) {
            bankAccount = staffAccount.getBankAccountNumber();
        }
        cmpItem.setBank(bankAccount);

        // 26. NGÂN HÀNG
        String bank = "";
        if (staffAccount != null && staffAccount.getBank() != null) {
            bank = staffAccount.getBank().getCode() + " " + staffAccount.getBank().getName();
        }
        cmpItem.setBank(bank);


        return cmpItem;
    }


    public List<CommissionPayrollItemDetail> getCMPItemForProp(UUID staffId, UUID salaryPeriodId, List<String> codeList) {
        List<CommissionPayrollItemDetail> response = new ArrayList<>();

        List<SalaryResultStaffItem> values = salaryResultStaffItemRepository.findByPeriodIdStaffIdAndReferenceCodeList(salaryPeriodId, staffId, codeList);

        for (SalaryResultStaffItem itemValue : values) {
            if (itemValue != null && StringUtils.hasText(itemValue.getValue()) && itemValue.getValue().matches("^[0-9]+(\\.[0-9]+)?$")) {
                CommissionPayrollItemDetail itemDetail = new CommissionPayrollItemDetail(itemValue);

                response.add(itemDetail);
            }
        }

        return response;

    }

    public double getSumByReferenceCodes(UUID staffId, UUID salaryPeriodId, List<String> codeList) {
        List<String> values = salaryResultStaffItemRepository.findValidDecimalValues(salaryPeriodId, staffId, codeList);

        return values.stream()
                .filter(v -> v != null && v.matches("^[0-9]+(\\.[0-9]+)?$"))
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();
    }


    @Override
    public double getSumTargetNhan(UUID staffId, UUID salaryPeriodId) {
        return getSumByReferenceCodes(staffId, salaryPeriodId, TARGET_NHAN);
    }

    @Override
    public double getSumDoanhSoDatDuoc(UUID staffId, UUID salaryPeriodId) {
        return getSumByReferenceCodes(staffId, salaryPeriodId, DOANH_SO_DAT_DUOC);
    }

    @Override
    public double getSumTgKhachMoi(UUID staffId, UUID salaryPeriodId) {
        return getSumByReferenceCodes(staffId, salaryPeriodId, TG_KHACH_MOI);
    }

    @Override
    public double getSumDsKhachMoi(UUID staffId, UUID salaryPeriodId) {
        return getSumByReferenceCodes(staffId, salaryPeriodId, DS_KHACH_MOI);
    }

    @Override
    public double getSumTgKhachCu(UUID staffId, UUID salaryPeriodId) {
        return getSumByReferenceCodes(staffId, salaryPeriodId, TG_KHACH_CU);
    }

    @Override
    public double getSumDsKhachCu(UUID staffId, UUID salaryPeriodId) {
        return getSumByReferenceCodes(staffId, salaryPeriodId, DS_KHACH_CU);
    }

    @Override
    public double getSumDsVuot(UUID staffId, UUID salaryPeriodId) {
        return getSumByReferenceCodes(staffId, salaryPeriodId, DS_VUOT);
    }

    @Override
    public double getSumLuongKPI(UUID staffId, UUID salaryPeriodId) {
        return getSumByReferenceCodes(staffId, salaryPeriodId, LUONG_KPI);
    }

    @Override
    public double getSumTienTour(UUID staffId, UUID salaryPeriodId) {
        return getSumByReferenceCodes(staffId, salaryPeriodId, TIEN_TOUR);
    }

    @Override
    public double getSumTrachNhiem(UUID staffId, UUID salaryPeriodId) {
        return getSumByReferenceCodes(staffId, salaryPeriodId, TRACH_NHIEM);
    }

    @Override
    public double getSumThuong(UUID staffId, UUID salaryPeriodId) {
        return getSumByReferenceCodes(staffId, salaryPeriodId, THUONG);
    }

    @Override
    public double getSumTruKhac(UUID staffId, UUID salaryPeriodId) {
        return getSumByReferenceCodes(staffId, salaryPeriodId, TRU_KHAC);
    }

    @Override
    public double getSumLuongBoSung(UUID staffId, UUID salaryPeriodId) {
        return getSumByReferenceCodes(staffId, salaryPeriodId, LUONG_BO_SUNG);
    }


    @Override
    public double getSumLuongThucLinh(UUID staffId, UUID salaryPeriodId, CommissionPayrollItem cmpItem) {
        double result = 0D;

//        // 6. TARGET NHẬN
//        result += cmpItem.getTargetNhanSum();
//
//        // 7. DOANH SỐ ĐẠT ĐƯỢC
//        result += cmpItem.getDoanhSoDatDuocSum();
//
//        // 8. TG KHÁCH MỚI
//        result += cmpItem.getTgKhachMoiSum();
//
//        // 9. DS KHÁCH MỚI
//        result += cmpItem.getDsKhachMoiSum();
//
//        // 10. TG KHÁCH CŨ
//        result += cmpItem.getTgKhachCuSum();
//
//        // 11. DS KHÁCH CŨ
//        result += cmpItem.getDsKhachCuSum();
//
//        // 12. DS VƯỢT
//        result += cmpItem.getDsVuotSum();

        // 14. LƯƠNG KPI
        result += cmpItem.getLuongKPISum();

        // 15. TIỀN TOUR
        result += cmpItem.getTienTourSum();

        // 16. TRÁCH NHIỆM
        result += cmpItem.getTrachNhiemSum();

        // 17. THƯỞNG
        result += cmpItem.getThuongSum();

        // 18. TRỪ KHÁC
        result -= cmpItem.getTruKhacSum();

        // 19. LƯƠNG BỔ SUNG
        result += cmpItem.getLuongBoSungSum();

        return result;
    }

    public static double toDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : 0.0;
    }

    @Override
    public List<CommissionPayrollItemDetail> getTargetNhan(UUID staffId, UUID salaryPeriodId) {
        return this.getCMPItemForProp(staffId, salaryPeriodId, TARGET_NHAN);
    }

    @Override
    public List<CommissionPayrollItemDetail> getDoanhSoDatDuoc(UUID staffId, UUID salaryPeriodId) {
        return this.getCMPItemForProp(staffId, salaryPeriodId, DOANH_SO_DAT_DUOC);
    }

    @Override
    public List<CommissionPayrollItemDetail> getTgKhachMoi(UUID staffId, UUID salaryPeriodId) {
        return this.getCMPItemForProp(staffId, salaryPeriodId, TG_KHACH_MOI);
    }

    @Override
    public List<CommissionPayrollItemDetail> getDsKhachMoi(UUID staffId, UUID salaryPeriodId) {
        return this.getCMPItemForProp(staffId, salaryPeriodId, DS_KHACH_MOI);
    }

    @Override
    public List<CommissionPayrollItemDetail> getTgKhachCu(UUID staffId, UUID salaryPeriodId) {
        return this.getCMPItemForProp(staffId, salaryPeriodId, TG_KHACH_CU);
    }

    @Override
    public List<CommissionPayrollItemDetail> getDsKhachCu(UUID staffId, UUID salaryPeriodId) {
        return this.getCMPItemForProp(staffId, salaryPeriodId, DS_KHACH_CU);
    }

    @Override
    public List<CommissionPayrollItemDetail> getDsVuot(UUID staffId, UUID salaryPeriodId) {
        return this.getCMPItemForProp(staffId, salaryPeriodId, DS_VUOT);
    }


    @Override
    public List<CommissionPayrollItemDetail> getLuongKPI(UUID staffId, UUID salaryPeriodId) {
        return this.getCMPItemForProp(staffId, salaryPeriodId, LUONG_KPI);
    }

    @Override
    public List<CommissionPayrollItemDetail> getTienTour(UUID staffId, UUID salaryPeriodId) {
        return this.getCMPItemForProp(staffId, salaryPeriodId, TIEN_TOUR);
    }

    @Override
    public List<CommissionPayrollItemDetail> getTrachNhiem(UUID staffId, UUID salaryPeriodId) {
        return this.getCMPItemForProp(staffId, salaryPeriodId, TRACH_NHIEM);
    }

    @Override
    public List<CommissionPayrollItemDetail> getThuong(UUID staffId, UUID salaryPeriodId) {
        return this.getCMPItemForProp(staffId, salaryPeriodId, THUONG);
    }

    @Override
    public List<CommissionPayrollItemDetail> getTruKhac(UUID staffId, UUID salaryPeriodId) {
        return this.getCMPItemForProp(staffId, salaryPeriodId, TRU_KHAC);
    }

    @Override
    public List<CommissionPayrollItemDetail> getLuongBoSung(UUID staffId, UUID salaryPeriodId) {
        return this.getCMPItemForProp(staffId, salaryPeriodId, LUONG_BO_SUNG);
    }


    public static Double convertStringToDouble(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null; // or return 0.0 depending on your use case
        }
        try {
            return Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            System.err.println("Invalid input: " + input);
            return null; // or throw custom exception if needed
        }
    }


    @Override
    public List<CommissionPayrollItemDetail> getLuongThucLinh(UUID staffId, UUID salaryPeriodId, CommissionPayrollItem cmpItem) {
        List<CommissionPayrollItemDetail> response = new ArrayList<>();

        if (cmpItem == null || cmpItem.getMaximumPlus() == 0) return response;

        for (int i = 0; i < cmpItem.getMaximumPlus(); i++) {
            Double result = 0D;

            // 6. TARGET NHẬN
            List<CommissionPayrollItemDetail> targetNhan = cmpItem.getTargetNhan();
            if (targetNhan.size() > i) {
                result += convertStringToDouble(targetNhan.get(i).getValue());
            }

            // 7. DOANH SỐ ĐẠT ĐƯỢC
            List<CommissionPayrollItemDetail> doanhSoDatDuoc = cmpItem.getDoanhSoDatDuoc();
            if (doanhSoDatDuoc.size() > i) {
                result += convertStringToDouble(doanhSoDatDuoc.get(i).getValue());
            }

            // 8. TG KHÁCH MỚI
            List<CommissionPayrollItemDetail> tgKhachMoi = cmpItem.getTgKhachMoi();
            if (tgKhachMoi.size() > i) {
                result += convertStringToDouble(tgKhachMoi.get(i).getValue());
            }

            // 9. DS KHÁCH MỚI
            List<CommissionPayrollItemDetail> dsKhachMoi = cmpItem.getDsKhachMoi();
            if (dsKhachMoi.size() > i) {
                result += convertStringToDouble(dsKhachMoi.get(i).getValue());
            }

            // 10. TG KHÁCH CŨ
            List<CommissionPayrollItemDetail> tgKhachCu = cmpItem.getTgKhachCu();
            if (tgKhachCu.size() > i) {
                result += convertStringToDouble(tgKhachCu.get(i).getValue());
            }

            // 11. DS KHÁCH CŨ
            List<CommissionPayrollItemDetail> dsKhachCu = cmpItem.getDsKhachCu();
            if (dsKhachCu.size() > i) {
                result += convertStringToDouble(dsKhachCu.get(i).getValue());
            }

            // 12. DS VƯỢT
            List<CommissionPayrollItemDetail> dsVuot = cmpItem.getDsVuot();
            if (dsVuot.size() > i) {
                result += convertStringToDouble(dsVuot.get(i).getValue());
            }

            // 13. CÁCH TÍNH %


            // 14. LƯƠNG KPI
            List<CommissionPayrollItemDetail> luongKPI = cmpItem.getLuongKPI();
            if (luongKPI.size() > i) {
                result += convertStringToDouble(luongKPI.get(i).getValue());
            }

            // 15. TIỀN TOUR
            List<CommissionPayrollItemDetail> tienTour = cmpItem.getTienTour();
            if (tienTour.size() > i) {
                result += convertStringToDouble(tienTour.get(i).getValue());
            }

            // 16. TRÁCH NHIỆM
            List<CommissionPayrollItemDetail> trachNhiem = cmpItem.getTrachNhiem();
            if (trachNhiem.size() > i) {
                result += convertStringToDouble(trachNhiem.get(i).getValue());
            }

            // 17. THƯỞNG
            List<CommissionPayrollItemDetail> thuong = cmpItem.getThuong();
            if (thuong.size() > i) {
                result += convertStringToDouble(thuong.get(i).getValue());
            }

            // 18. TRỪ KHÁC
            List<CommissionPayrollItemDetail> truKhac = cmpItem.getTruKhac();
            if (truKhac.size() > i) {
                result += convertStringToDouble(truKhac.get(i).getValue());
            }

            // 19. LƯƠNG BỔ SUNG
            List<CommissionPayrollItemDetail> luongBoSung = cmpItem.getLuongBoSung();
            if (luongBoSung.size() > i) {
                result += convertStringToDouble(luongBoSung.get(i).getValue());
            }

            CommissionPayrollItemDetail itemDetail = new CommissionPayrollItemDetail();

            DecimalFormat df = new DecimalFormat("0.####");
            df.setMaximumFractionDigits(4);

            itemDetail.setValue(df.format(result));

            response.add(itemDetail);
        }

        return response;
    }


    private PersonBankAccount getStaffBankAccount(UUID staffId) {
        if (staffId == null) {
            return null;
        }

        List<PersonBankAccount> staffBankAccounts = personBankAccountRepository.findMainByPersonId(staffId);
        if (staffBankAccounts == null || staffBankAccounts.isEmpty()) {
            staffBankAccounts = personBankAccountRepository.findByPersonId(staffId);

            if (staffBankAccounts == null || staffBankAccounts.isEmpty()) return null;

            return staffBankAccounts.get(0);
        }

        return staffBankAccounts.get(0);
    }

    public static CellStyle createDepartmentCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();

        // Viền
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);

        // Căn chỉnh
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Màu nền xám
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Font chữ trắng
        Font font = workbook.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 12.6);
        font.setColor(IndexedColors.BLACK.getIndex());
        cellStyle.setFont(font);

        return cellStyle;
    }


    @Override
    public Workbook exportExcelCommissionPayroll(SearchSalaryResultStaffDto dto) {
        long startTime = System.nanoTime();

        if (dto == null || dto.getSalaryPeriodId() == null || dto.getSalaryPeriod() == null) {
            return null;
        }

        SalaryPeriod salaryPeriod = null;
        if (dto.getSalaryPeriod() != null) {
            salaryPeriod = salaryPeriodRepository.findById(dto.getSalaryPeriod().getId()).orElse(null);
        }
        if (salaryPeriod == null && dto.getSalaryPeriodId() != null) {
            salaryPeriod = salaryPeriodRepository.findById(dto.getSalaryPeriodId()).orElse(null);
        }
        if (salaryPeriod == null) return null;

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("Excel/BANG_LUONG_HOA_HONG.xlsx")) {
            if (fileInputStream == null) {
                throw new IOException("File BANG_LUONG_HOA_HONG không tìm thấy trong classpath");
            }

            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet importSheet = workbook.getSheetAt(0);

            CellStyle departmentCellStyle = this.createDepartmentCellStyle(workbook);
            CellStyle dataCellStyle = ExportExcelUtil.createDataCellStyle(workbook);

            // Dòng mẫu để clone style
            Row templateRow = importSheet.getRow(1); // Giả sử dòng 1 là dòng mẫu
            if (templateRow == null) {
                throw new IllegalStateException("Dòng template (row 1) không tồn tại trong file Excel.");
            }

            int rowIndex = 5; // Bắt đầu từ dòng sau dòng template
            int orderNumber = 1;

            List<HRDepartmentDto> cpDepartments = this.getDepartmentsHasCMP();
            for (HRDepartmentDto department : cpDepartments) {
                logger.info("Thống kê BLHH cho pb " + department.getName() + " - " + department.getCode());

                List<CommissionPayrollItem> cmpItems = this.getCMPIOfStaffsInDepartment(department.getId(), salaryPeriod.getId());

                if (cmpItems.isEmpty()) continue;

                Row departmentRow = importSheet.createRow(rowIndex);
                ExportExcelUtil.applyStyleToRow(departmentRow, departmentCellStyle, 27);
                ExportExcelUtil.createCell(departmentRow, 0, department.getName(), departmentCellStyle);
                // Gộp 3 ô: từ cellIndex đến cellIndex + 2 (tức là 3 ô)
                importSheet.addMergedRegion(new CellRangeAddress(
                        rowIndex, // from row
                        rowIndex, // to row
                        0, // from column
                        3 // to column
                ));

                rowIndex++;

                for (CommissionPayrollItem cmpItem : cmpItems) {
                    if (cmpItem == null)
                        continue;

//                    Row dataRow = importSheet.createRow(rowIndex);
//                    handlePrintItemToExcelV2(dataRow, cmpItem, orderNumber, dataCellStyle);

                    handlePrintItemToExcelV3(importSheet, rowIndex, cmpItem, orderNumber, dataCellStyle);

                    orderNumber++;

                    rowIndex++;
                    rowIndex += cmpItem.getMaximumPlus();
                }
            }

            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;

            logger.info("Xuất bảng lương hoa hồng cho kỳ lương {} - Xử lý mất {} ms ", salaryPeriod.getName(), elapsedTimeMs);
            return workbook;

        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }


    private void handlePrintItemToExcel(Sheet sheet, Row dataRow, CommissionPayrollItem cmpItem, int orderNumber, Integer rowIndex, CellStyle dataCellStyle) {
        // Hàm tiện ích tạo dòng nếu chưa có
        Function<Integer, Row> getOrCreateRow = index -> {
            Row row = sheet.getRow(index);
            return (row != null) ? row : sheet.createRow(index);
        };

        // 0. STT
        setCellValueKeepStyle(dataRow, 0, orderNumber);
        // 1. Staff Code
        setCellValueKeepStyle(dataRow, 1, cmpItem.getStaffCode());
        // 2. Vietnamese Name
        setCellValueKeepStyle(dataRow, 2, cmpItem.getDisplayName());
//        // 3. Empty
        // 4. Position Title
        setCellValueKeepStyle(dataRow, 4, cmpItem.getPositionTitle());
        // 5. Department
        setCellValueKeepStyle(dataRow, 5, cmpItem.getDepartment());

        // 6. Target Nhận
        printDetails(sheet, cmpItem.getTargetNhan(), 6, rowIndex, getOrCreateRow);
        // 7. Doanh số đạt được
        printDetails(sheet, cmpItem.getDoanhSoDatDuoc(), 7, rowIndex, getOrCreateRow);
        // 8. TG Khách mới
        printDetails(sheet, cmpItem.getTgKhachMoi(), 8, rowIndex, getOrCreateRow);
        // 9. DS Khách mới
        printDetails(sheet, cmpItem.getDsKhachMoi(), 9, rowIndex, getOrCreateRow);
        // 10. TG Khách cũ
        printDetails(sheet, cmpItem.getTgKhachCu(), 10, rowIndex, getOrCreateRow);
        // 11. DS Khách cũ
        printDetails(sheet, cmpItem.getDsKhachCu(), 11, rowIndex, getOrCreateRow);
        // 12. DS Vượt
        printDetails(sheet, cmpItem.getDsVuot(), 12, rowIndex, getOrCreateRow);

        // 13. Cách tính %
        String cachTinhPTStr = "";
        if (cmpItem.getCachTinhPT() != null && !cmpItem.getCachTinhPT().isEmpty()) {
            cachTinhPTStr = cmpItem.getCachTinhPT().get(0).getValue();
        }
        setCellValueKeepStyle(dataRow, 13, cachTinhPTStr);

        // 14. Lương KPI
        printDetails(sheet, cmpItem.getLuongKPI(), 14, rowIndex, getOrCreateRow);
        // 15. Tiền Tour
        printDetails(sheet, cmpItem.getTienTour(), 15, rowIndex, getOrCreateRow);
        // 16. Trách nhiệm
        printDetails(sheet, cmpItem.getTrachNhiem(), 16, rowIndex, getOrCreateRow);
        // 17. Thưởng
        printDetails(sheet, cmpItem.getThuong(), 17, rowIndex, getOrCreateRow);
        // 18. Trừ khác
        printDetails(sheet, cmpItem.getTruKhac(), 18, rowIndex, getOrCreateRow);
        // 19. Lương bổ sung
        printDetails(sheet, cmpItem.getLuongBoSung(), 19, rowIndex, getOrCreateRow);
        // 20. Lương thực lĩnh
        printDetails(sheet, cmpItem.getLuongThucLinh(), 20, rowIndex, getOrCreateRow);

        // 21. Ghi chú
        setCellValueKeepStyle(dataRow, 21, cmpItem.getNote());
        // 22. Email
        setCellValueKeepStyle(dataRow, 22, cmpItem.getEmail());
        // 23. Password
        setCellValueKeepStyle(dataRow, 23, cmpItem.getPassword());
        // 24. Blank Cell
        setCellValueKeepStyle(dataRow, 24, cmpItem.getBlankCell());
        // 25. Bank Account
        setCellValueKeepStyle(dataRow, 25, cmpItem.getBankAccount());
        // 26. Bank
        setCellValueKeepStyle(dataRow, 26, cmpItem.getBank());
    }


    public static String formatDecimalString(String input) {
        try {
            if (input == null || !input.matches("^[0-9]+(\\.[0-9]+)?$")) {
                return "0.00";
            }
            BigDecimal number = new BigDecimal(input);
            number = number.setScale(2, RoundingMode.HALF_UP);

            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            return formatter.format(number);
        } catch (Exception e) {
            return "0.00";
        }
    }

    public static String formatDecimalDouble(double input) {
        try {
            BigDecimal number = BigDecimal.valueOf(input).setScale(2, RoundingMode.HALF_UP);
            DecimalFormat df = new DecimalFormat("#,##0.00");
            return df.format(number);
        } catch (Exception e) {
            return "0.00";
        }
    }


    private void handlePrintItemToExcelV3(Sheet importSheet, int rowIndex, CommissionPayrollItem cmpItem, int orderNumber, CellStyle dataCellStyle) {

        if (importSheet == null) return;
        for (int i = 0; i < cmpItem.getMaximumPlus(); i++) {
            Row dataRow = importSheet.createRow(rowIndex + i);

            // 0. STT
            if (i == 0) {
                ExportExcelUtil.createCell(dataRow, 0, orderNumber, dataCellStyle);

            } else {
                ExportExcelUtil.createCell(dataRow, 0, "", dataCellStyle);
            }

            // 1. Staff Code
            if (i == 0) {
                ExportExcelUtil.createCell(dataRow, 1, cmpItem.getStaffCode(), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 1, "", dataCellStyle);
            }

            // 2. Vietnamese Name
            if (i == 0) {
                ExportExcelUtil.createCell(dataRow, 2, cmpItem.getDisplayName(), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 2, "", dataCellStyle);
            }

            // 3. Empty
            ExportExcelUtil.createCell(dataRow, 3, "", dataCellStyle);

            // 4. Position Title
            if (i == 0) {
                ExportExcelUtil.createCell(dataRow, 4, cmpItem.getPositionTitle(), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 4, "", dataCellStyle);
            }

            // 5. Department
            if (i == 0) {
                ExportExcelUtil.createCell(dataRow, 5, cmpItem.getDepartment(), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 5, "", dataCellStyle);
            }

            // 6. Target Nhận
            if (i < cmpItem.getTargetNhan().size()) {
                Double cellValue = convertStringToDouble(cmpItem.getTargetNhan().get(i).getValue());
                ExportExcelUtil.createCell(dataRow, 6, formatDecimalDouble(cellValue), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 6, "", dataCellStyle);
            }

            // 7. Doanh số đạt được
            if (i < cmpItem.getDoanhSoDatDuoc().size()) {
                Double cellValue = convertStringToDouble(cmpItem.getDoanhSoDatDuoc().get(i).getValue());
                ExportExcelUtil.createCell(dataRow, 7, formatDecimalDouble(cellValue), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 7, "", dataCellStyle);
            }

            // 8. TG Khách mới
            if (i < cmpItem.getTgKhachMoi().size()) {
                Double cellValue = convertStringToDouble(cmpItem.getTgKhachMoi().get(i).getValue());
                ExportExcelUtil.createCell(dataRow, 8, formatDecimalDouble(cellValue), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 8, "", dataCellStyle);
            }

            // 9. DS Khách mới
            if (i < cmpItem.getDsKhachMoi().size()) {
                Double cellValue = convertStringToDouble(cmpItem.getDsKhachMoi().get(i).getValue());
                ExportExcelUtil.createCell(dataRow, 9, formatDecimalDouble(cellValue), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 9, "", dataCellStyle);
            }

            // 10. TG Khách cũ
            if (i < cmpItem.getTgKhachCu().size()) {
                Double cellValue = convertStringToDouble(cmpItem.getTgKhachCu().get(i).getValue());
                ExportExcelUtil.createCell(dataRow, 10, formatDecimalDouble(cellValue), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 10, "", dataCellStyle);
            }

            // 11. DS Khách cũ
            if (i < cmpItem.getDsKhachCu().size()) {
                Double cellValue = convertStringToDouble(cmpItem.getDsKhachCu().get(i).getValue());
                ExportExcelUtil.createCell(dataRow, 11, formatDecimalDouble(cellValue), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 11, "", dataCellStyle);
            }

            // 12. DS Vượt
            if (i < cmpItem.getDsVuot().size()) {
                Double cellValue = convertStringToDouble(cmpItem.getDsVuot().get(i).getValue());
                ExportExcelUtil.createCell(dataRow, 12, formatDecimalDouble(cellValue), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 12, "", dataCellStyle);
            }

            // 13. Cách tính %
            ExportExcelUtil.createCell(dataRow, 13, "", dataCellStyle);

            // 14. Lương KPI
            if (i < cmpItem.getLuongKPI().size()) {
                Double cellValue = convertStringToDouble(cmpItem.getLuongKPI().get(i).getValue());
                ExportExcelUtil.createCell(dataRow, 14, formatDecimalDouble(cellValue), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 14, "", dataCellStyle);
            }

            // 15. Tiền Tour
            if (i < cmpItem.getTienTour().size()) {
                Double cellValue = convertStringToDouble(cmpItem.getTienTour().get(i).getValue());
                ExportExcelUtil.createCell(dataRow, 15, formatDecimalDouble(cellValue), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 15, "", dataCellStyle);
            }

            // 16. Trách nhiệm
            if (i < cmpItem.getTrachNhiem().size()) {
                Double cellValue = convertStringToDouble(cmpItem.getTrachNhiem().get(i).getValue());
                ExportExcelUtil.createCell(dataRow, 16, formatDecimalDouble(cellValue), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 16, "", dataCellStyle);
            }

            // 17. Thưởng
            if (i < cmpItem.getThuong().size()) {
                Double cellValue = convertStringToDouble(cmpItem.getThuong().get(i).getValue());
                ExportExcelUtil.createCell(dataRow, 17, formatDecimalDouble(cellValue), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 17, "", dataCellStyle);
            }

            // 18. Trừ khác
            if (i < cmpItem.getTruKhac().size()) {
                Double cellValue = convertStringToDouble(cmpItem.getTruKhac().get(i).getValue());
                ExportExcelUtil.createCell(dataRow, 18, formatDecimalDouble(cellValue), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 18, "", dataCellStyle);
            }

            // 19. Lương bổ sung
            if (i < cmpItem.getLuongBoSung().size()) {
                Double cellValue = convertStringToDouble(cmpItem.getLuongBoSung().get(i).getValue());
                ExportExcelUtil.createCell(dataRow, 19, formatDecimalDouble(cellValue), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 19, "", dataCellStyle);
            }

            // 20. Lương thực lĩnh
            if (i < cmpItem.getLuongThucLinh().size()) {
                Double cellValue = convertStringToDouble(cmpItem.getLuongThucLinh().get(i).getValue());
                ExportExcelUtil.createCell(dataRow, 20, formatDecimalDouble(cellValue), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 20, "", dataCellStyle);
            }


            // 21. Ghi chú
            if (i == 0) {
                ExportExcelUtil.createCell(dataRow, 21, cmpItem.getNote(), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 21, "", dataCellStyle);
            }

            // 22. Email
            if (i == 0) {
                ExportExcelUtil.createCell(dataRow, 22, cmpItem.getEmail(), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 22, "", dataCellStyle);
            }

            // 23. Password
            if (i == 0) {
                ExportExcelUtil.createCell(dataRow, 23, cmpItem.getPassword(), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 23, "", dataCellStyle);
            }

            // 24. Blank Cell
            ExportExcelUtil.createCell(dataRow, 24, cmpItem.getBlankCell(), dataCellStyle);

            // 25. Bank Account
            if (i == 0) {
                ExportExcelUtil.createCell(dataRow, 25, cmpItem.getBankAccount(), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 25, "", dataCellStyle);
            }

            // 26. Bank
            if (i == 0) {
                ExportExcelUtil.createCell(dataRow, 26, cmpItem.getBank(), dataCellStyle);
            } else {
                ExportExcelUtil.createCell(dataRow, 26, "", dataCellStyle);
            }
        }
    }


    private void handlePrintItemToExcelV2(Row dataRow, CommissionPayrollItem cmpItem, int orderNumber, CellStyle dataCellStyle) {

        // 0. STT
        ExportExcelUtil.createCell(dataRow, 0, orderNumber, dataCellStyle);

        // 1. Staff Code
        ExportExcelUtil.createCell(dataRow, 1, cmpItem.getStaffCode(), dataCellStyle);

        // 2. Vietnamese Name
        ExportExcelUtil.createCell(dataRow, 2, cmpItem.getDisplayName(), dataCellStyle);

//        // 3. Empty
        ExportExcelUtil.createCell(dataRow, 3, "", dataCellStyle);

        // 4. Position Title
        ExportExcelUtil.createCell(dataRow, 4, cmpItem.getPositionTitle(), dataCellStyle);

        // 5. Department
        ExportExcelUtil.createCell(dataRow, 5, cmpItem.getDepartment(), dataCellStyle);

        // 6. Target Nhận
        ExportExcelUtil.createCell(dataRow, 6, formatDecimalDouble(cmpItem.getTargetNhanSum()), dataCellStyle);
        // 7. Doanh số đạt được
        ExportExcelUtil.createCell(dataRow, 7, formatDecimalDouble(cmpItem.getDoanhSoDatDuocSum()), dataCellStyle);
        // 8. TG Khách mới
        ExportExcelUtil.createCell(dataRow, 8, formatDecimalDouble(cmpItem.getTgKhachMoiSum()), dataCellStyle);
        // 9. DS Khách mới
        ExportExcelUtil.createCell(dataRow, 9, formatDecimalDouble(cmpItem.getDsKhachMoiSum()), dataCellStyle);
        // 10. TG Khách cũ
        ExportExcelUtil.createCell(dataRow, 10, formatDecimalDouble(cmpItem.getTgKhachCuSum()), dataCellStyle);
        // 11. DS Khách cũ
        ExportExcelUtil.createCell(dataRow, 11, formatDecimalDouble(cmpItem.getDsKhachCuSum()), dataCellStyle);
        // 12. DS Vượt
        ExportExcelUtil.createCell(dataRow, 12, formatDecimalDouble(cmpItem.getDsVuotSum()), dataCellStyle);

        // 13. Cách tính %
        ExportExcelUtil.createCell(dataRow, 13, "", dataCellStyle);

        // 14. Lương KPI
        ExportExcelUtil.createCell(dataRow, 14, formatDecimalDouble(cmpItem.getLuongKPISum()), dataCellStyle);
        // 15. Tiền Tour
        ExportExcelUtil.createCell(dataRow, 15, formatDecimalDouble(cmpItem.getTienTourSum()), dataCellStyle);
        // 16. Trách nhiệm
        ExportExcelUtil.createCell(dataRow, 16, formatDecimalDouble(cmpItem.getTrachNhiemSum()), dataCellStyle);
        // 17. Thưởng
        ExportExcelUtil.createCell(dataRow, 17, formatDecimalDouble(cmpItem.getThuongSum()), dataCellStyle);
        // 18. Trừ khác
        ExportExcelUtil.createCell(dataRow, 18, formatDecimalDouble(cmpItem.getTruKhacSum()), dataCellStyle);
        // 19. Lương bổ sung
        ExportExcelUtil.createCell(dataRow, 19, formatDecimalDouble(cmpItem.getLuongBoSungSum()), dataCellStyle);
        // 20. Lương thực lĩnh
        ExportExcelUtil.createCell(dataRow, 20, formatDecimalDouble(cmpItem.getLuongThucLinhSum()), dataCellStyle);

        // 21. Ghi chú
        ExportExcelUtil.createCell(dataRow, 21, cmpItem.getNote(), dataCellStyle);

        // 22. Email
        ExportExcelUtil.createCell(dataRow, 22, cmpItem.getEmail(), dataCellStyle);

        // 23. Password
        ExportExcelUtil.createCell(dataRow, 23, cmpItem.getPassword(), dataCellStyle);

        // 24. Blank Cell
        ExportExcelUtil.createCell(dataRow, 24, cmpItem.getBlankCell(), dataCellStyle);

        // 25. Bank Account
        ExportExcelUtil.createCell(dataRow, 25, cmpItem.getBankAccount(), dataCellStyle);

        // 26. Bank
        ExportExcelUtil.createCell(dataRow, 26, cmpItem.getBank(), dataCellStyle);

    }


    // Hàm tiện ích để in danh sách chi tiết vào một cột, với kiểm tra row null
    private void printDetails(Sheet sheet, List<CommissionPayrollItemDetail> details, int columnIndex, int baseRowIndex, Function<Integer, Row> getOrCreateRow) {
        if (details != null && !details.isEmpty()) {
            for (int i = 0; i < details.size(); i++) {
                Row row = getOrCreateRow.apply(baseRowIndex + i);
                setCellValueKeepStyle(row, columnIndex, details.get(i).getValue());
            }
        }
    }


    private void cloneRowStyle(Row sourceRow, Row targetRow) {
        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
            Cell sourceCell = sourceRow.getCell(i);
            if (sourceCell == null) continue;

            Cell targetCell = targetRow.createCell(i);
            targetCell.setCellStyle(sourceCell.getCellStyle());
            targetCell.setCellType(sourceCell.getCellType());
        }
    }

    private void setCellValueKeepStyle(Row row, int cellIndex, Object value) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            cell = row.createCell(cellIndex);
        }

        if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value != null) {
            cell.setCellValue(value.toString());
        } else {
            cell.setCellValue("");
        }
    }


    /**
     * Hàm hỗ trợ lấy style của cell mẫu theo index
     */
    private CellStyle getTemplateCellStyle(Row templateRow, int cellIndex) {
        if (templateRow == null) return null;
        Cell templateCell = templateRow.getCell(cellIndex);
        return (templateCell != null) ? templateCell.getCellStyle() : null;
    }


    private SearchStaffSalaryTemplateDto convertToSearchStaffSalaryTemplateDto(SearchSalaryResultDto dto) {
        SearchStaffSalaryTemplateDto response = new SearchStaffSalaryTemplateDto();

        response.setStaffId(dto.getStaffId());
        response.setDepartmentId(dto.getDepartmentId());
        response.setOrganizationId(dto.getOrganizationId());
        response.setPositionTitleId(dto.getPositionTitleId());
        response.setPositionId(dto.getPositionId());

        return response;
    }


}
