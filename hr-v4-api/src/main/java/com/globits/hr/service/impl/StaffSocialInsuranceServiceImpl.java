package com.globits.hr.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffSocialInsurance;
import com.globits.hr.dto.*;
import com.globits.hr.dto.search.SearchStaffDto;
import com.globits.hr.dto.search.SearchStaffSocialInsuranceDto;
import com.globits.hr.dto.staff.StaffSocialInsuranceDto;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.StaffSocialInsuranceRepository;
import com.globits.hr.service.StaffService;
import com.globits.hr.service.StaffSocialInsuranceService;
import com.globits.hr.service.SystemConfigService;
import com.globits.hr.service.UserExtService;
import com.globits.hr.utils.ExcelUtils;
import com.globits.salary.domain.*;
import com.globits.salary.dto.SalaryItemDto;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.dto.SalaryResultStaffDto;
import com.globits.salary.dto.SalaryTemplateDto;
import com.globits.salary.dto.search.SearchSalaryResultStaffDto;
import com.globits.salary.repository.*;
import com.globits.salary.service.ExpressionEvaluatorService;
import com.globits.salary.service.SalaryItemService;
import com.globits.salary.service.SalaryPeriodService;
import com.globits.salary.service.StaffSalaryItemValueService;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional
@Service
public class StaffSocialInsuranceServiceImpl extends GenericServiceImpl<StaffSocialInsurance, UUID>
        implements StaffSocialInsuranceService {

    private static final Logger logger = LoggerFactory.getLogger(StaffSocialInsurance.class);

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
    private StaffService staffService;

    @Autowired
    private StaffSocialInsuranceRepository staffSocialInsuranceRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private SalaryResultRepository salaryResultRepository;

    @Autowired
    private SalaryPeriodRepository salaryPeriodRepository;

    @Autowired
    private SalaryItemService salaryItemService;

    @Autowired
    private SalaryResultStaffRepository salaryResultStaffRepository;

    @Autowired
    private ExpressionEvaluatorService expressionEvaluatorService;

    @Autowired
    private SalaryResultStaffItemRepository salaryResultStaffItemRepository;

    @Autowired
    private SalaryPeriodService salaryPeriodService;

    @Autowired
    private UserExtService userExtService;

    @Autowired
    private StaffSalaryItemValueService staffSalaryItemValueService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private EntityManager entityManager;

    @Override
    public Boolean updateStaffSocialInsurancePaidStatus(SearchStaffSocialInsuranceDto dto) throws Exception {
        if (dto == null)
            return false;
        if (dto.getChosenRecordIds() != null && dto.getChosenRecordIds().isEmpty())
            return true;

        for (UUID recordId : dto.getChosenRecordIds()) {
            StaffSocialInsurance entity = staffSocialInsuranceRepository.findById(recordId).orElse(null);
            if (entity == null)
                throw new Exception("Record is not existed!");

            entity.setPaidStatus(dto.getPaidStatus());

            staffSocialInsuranceRepository.save(entity);


        }

        return true;
    }


    private void handleSetTotalInsuranceAmount(StaffSocialInsurance entity) {
        // tinh tong tien bao hiem = tien ca nhan dong + tien don vi dong
        Double totalInsuranceAmount = 0.0;
        totalInsuranceAmount += (entity.getStaffTotalInsuranceAmount() != null ? entity.getStaffTotalInsuranceAmount() : 0.0);
        totalInsuranceAmount += (entity.getOrgTotalInsuranceAmount() != null ? entity.getOrgTotalInsuranceAmount() : 0.0);
        entity.setTotalInsuranceAmount(totalInsuranceAmount);
    }

    // common CRUD
    @Override
    public StaffSocialInsuranceDto saveStaffSocialInsurance(StaffSocialInsuranceDto dto) {
        if (dto == null)
            return null;

        StaffSocialInsurance entity = null;
        if (dto.getId() != null) {
            entity = staffSocialInsuranceRepository.findById(dto.getId()).orElse(null);
            if (entity == null)
                return null;
        }
        if (entity == null) {
            entity = new StaffSocialInsurance();
        }
        if (dto.getStaff() != null && dto.getStaff().getId() != null) {
            Staff staff = staffRepository.findById(dto.getStaff().getId()).orElse(null);
            if (staff == null)
                return null;
            entity.setStaff(staff);
        } else {
            entity.setStaff(null);
        }
        if (dto.getSalaryPeriod() != null && dto.getSalaryPeriod().getId() != null) {
            SalaryPeriod salaryPeriod = salaryPeriodRepository.findById(dto.getSalaryPeriod().getId()).orElse(null);
            if (salaryPeriod == null)
                return null;
            entity.setSalaryPeriod(salaryPeriod);
        } else {
            entity.setSalaryPeriod(null);
        }
        if (dto.getSalaryResult() != null && dto.getSalaryResult().getId() != null) {
            SalaryResult salaryResult = salaryResultRepository.findById(dto.getSalaryResult().getId()).orElse(null);
            if (salaryResult == null)
                return null;
            entity.setSalaryResult(salaryResult);
        } else {
            entity.setSalaryResult(null);
        }
        entity.setInsuranceSalary(dto.getInsuranceSalary());

        entity.setStaffSocialInsurancePercentage(dto.getStaffSocialInsurancePercentage());
        entity.setStaffHealthInsurancePercentage(dto.getStaffHealthInsurancePercentage());
        entity.setStaffUnemploymentInsurancePercentage(dto.getStaffUnemploymentInsurancePercentage());

        entity.setOrgSocialInsurancePercentage(dto.getOrgSocialInsurancePercentage());
        entity.setOrgHealthInsurancePercentage(dto.getOrgHealthInsurancePercentage());
        entity.setOrgUnemploymentInsurancePercentage(dto.getOrgUnemploymentInsurancePercentage());

        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());

        if (dto.getInsuranceSalary() != null) {
            Double insuranceSalary = dto.getInsuranceSalary();
            // ===== Nhân viên đóng =====
            Double employeeSocialInsuranceAmount = dto.getStaffSocialInsurancePercentage() != null
                    ? insuranceSalary * dto.getStaffSocialInsurancePercentage() : 0.0;
            Double employeeHealthInsuranceAmount = dto.getStaffHealthInsurancePercentage() != null
                    ? insuranceSalary * dto.getStaffHealthInsurancePercentage() : 0.0;
            Double employeeUnemploymentInsuranceAmount = dto.getStaffUnemploymentInsurancePercentage() != null
                    ? insuranceSalary * dto.getStaffUnemploymentInsurancePercentage() : 0.0;
            Double employeeTotalInsuranceAmount = employeeSocialInsuranceAmount + employeeHealthInsuranceAmount + employeeUnemploymentInsuranceAmount;

            entity.setStaffTotalInsuranceAmount(employeeTotalInsuranceAmount);

            // ===== Công ty đóng =====
            Double orgSocialInsuranceAmount = dto.getOrgSocialInsurancePercentage() != null
                    ? insuranceSalary * dto.getOrgSocialInsurancePercentage() / 100 : 0.0;
            Double orgHealthInsuranceAmount = dto.getOrgHealthInsurancePercentage() != null
                    ? insuranceSalary * dto.getOrgHealthInsurancePercentage() / 100 : 0.0;
            Double orgUnemploymentInsuranceAmount = dto.getOrgUnemploymentInsurancePercentage() != null
                    ? insuranceSalary * dto.getOrgUnemploymentInsurancePercentage() / 100 : 0.0;
            Double orgTotalInsuranceAmount = orgSocialInsuranceAmount + orgHealthInsuranceAmount + orgUnemploymentInsuranceAmount;

            entity.setOrgTotalInsuranceAmount(orgTotalInsuranceAmount);

            entity.setTotalInsuranceAmount(orgTotalInsuranceAmount + employeeTotalInsuranceAmount);

        }

        this.handleSetTotalInsuranceAmount(entity);

        if (dto.getPaidStatus() != null) {
            entity.setPaidStatus(dto.getPaidStatus());
        } else {
            entity.setPaidStatus(HrConstants.StaffSocialInsurancePaidStatus.PAID.getValue());
        }
        entity.setNote(dto.getNote());

        StaffSocialInsurance response = staffSocialInsuranceRepository.save(entity);
        return new StaffSocialInsuranceDto(response);
    }

    @Override
    public Boolean deleteStaffSocialInsurance(UUID id) {
        if (id == null) {
            return false;
        }
        StaffSocialInsurance entity = staffSocialInsuranceRepository.findById(id).orElse(null);
        if (entity == null)
            return false;
        staffSocialInsuranceRepository.delete(entity);
        return true;
    }

    @Override
    public StaffSocialInsuranceDto getStaffSocialInsurance(UUID id) {
        if (id == null)
            return null;

        StaffSocialInsurance entity = staffSocialInsuranceRepository.findById(id).orElse(null);

        if (entity == null)
            return null;

        return new StaffSocialInsuranceDto(entity);
    }

    @Override
    public Object calculateInsAmmount(SearchStaffSocialInsuranceDto dto) {
        if (dto == null) {
            return null;
        }
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        if (dto.getStaff() != null) {
            dto.setStaffId(dto.getStaff().getId());
        }

        boolean isRoleManager = false;
        UserDto user = userExtService.getCurrentUser();
        if (user != null && user.getRoles() != null && user.getRoles().size() > 0) {
            for (RoleDto item : user.getRoles()) {
                if (item.getName() != null &&
                        ("ROLE_ADMIN".equals(item.getName()) || "HR_MANAGER".equals(item.getName())
                        )) {
                    isRoleManager = true;
                }
            }
        }
        if (!isRoleManager) {
            StaffDto currentStaff = userExtService.getCurrentStaff();
            dto.setStaffId(currentStaff.getId());
        }
        String whereClause = " where (1=1)";
        String sqlCount = "select sum(entity.insuranceSalary), sum(entity.staffTotalInsuranceAmount) from StaffSocialInsurance as entity ";

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += " AND (entity.staff.staffCode LIKE :text OR entity.staff.firstName LIKE :text  OR entity.staff.lastName LIKE :text OR entity.salaryResult.name LIKE :text OR entity.salaryPeriod.name LIKE :text) ";
        }
        if (dto.getSalaryPeriod() != null) {
            whereClause += " and (entity.salaryPeriod.id = :salaryPeriodId) ";
        }
        if (dto.getSalaryResult() != null) {
            whereClause += " and (entity.salaryResult.id = :salaryResultId) ";
        }
        if (dto.getStaffId() != null) {
            whereClause += " and (entity.staff.id = :staffId) ";
        }
        if (dto.getPaidStatus() != null) {
            whereClause += " and (entity.paidStatus = :paidStatus) ";
        }

        sqlCount += whereClause;

        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            qCount.setParameter("text", '%' + dto.getKeyword() + '%');
        }
        if (dto.getSalaryPeriod() != null) {
            qCount.setParameter("salaryPeriodId", dto.getSalaryPeriod().getId());
        }
        if (dto.getSalaryResult() != null) {
            qCount.setParameter("salaryResultId", dto.getSalaryResult().getId());
        }
        if (dto.getStaffId() != null) {
            qCount.setParameter("staffId", dto.getStaffId());
        }
        if (dto.getPaidStatus() != null) {
            qCount.setParameter("paidStatus", dto.getPaidStatus());
        }
        Object result = (long) qCount.getSingleResult();
        return result;
    }

    @Override
    public Workbook exportStaffSocialInsurance(SearchStaffSocialInsuranceDto dto) {
        if (dto == null) {
            return null;
        }

        try (InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream("Excel/MAU_BHXH.xlsx")) {
            if (fileInputStream == null) {
                throw new IOException("File '" + "Excel/MAU_BHXH.xlsx" + "' không tìm thấy trong classpath");
            }

            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet staffSheet = workbook.getSheetAt(0);
            CellStyle dataCellStyle = ExcelUtils.createDataCellStyle(workbook);

            // Lấy tất cả dữ liệu
            dto.setPageIndex(1);
            dto.setPageSize(Integer.MAX_VALUE);
            Page<StaffSocialInsuranceDto> pageBHXH = this.searchByPage(dto);

            if (pageBHXH == null || pageBHXH.isEmpty()) {
                return workbook;
            }

            int[] rowIndex = {1};
            int[] orderNumber = {1};
            long startTime = System.nanoTime();

            List<StaffSocialInsuranceDto> listBHXH = pageBHXH.getContent();

            // Ghi dữ liệu vào file Excel
            for (StaffSocialInsuranceDto item : listBHXH) {
                writeBHXHToExcel(item, staffSheet, dataCellStyle, rowIndex, orderNumber);
            }

            // Sau khi ghi dữ liệu
            writeTotalRowToExcel(listBHXH, staffSheet, dataCellStyle, rowIndex);

            long endTime = System.nanoTime();
            long elapsedTimeMs = (endTime - startTime) / 1_000_000;
            logger.info("Xuất tất cả BHXH - Xử lý mất {} ms", elapsedTimeMs);

            return workbook;

        } catch (IOException e) {
            System.err.println("Error reading Excel template: " + e.getMessage());
            return null;
        }
    }

    private void writeBHXHToExcel(
            StaffSocialInsuranceDto item,
            Sheet sheet,
            CellStyle dataCellStyle,
            int[] rowIndex,
            int[] orderNumber) {

        if (item == null) return;

        Row dataRow = sheet.createRow(rowIndex[0]);
        int cellIndex = 0;

        // STT
        ExcelUtils.createCell(dataRow, cellIndex++, orderNumber[0]++, dataCellStyle);

        // Thông tin nhân viên
        createCell(dataRow, cellIndex++, item.getStaff() != null ? item.getStaff().getStaffCode() : "", dataCellStyle);
        createCell(dataRow, cellIndex++, item.getStaff() != null ? item.getStaff().getDisplayName() : "", dataCellStyle);

        // Kỳ lương
        String salaryPeriodInfo = "";
        if (item.getSalaryPeriod() != null) {
            salaryPeriodInfo = item.getSalaryPeriod().getName() + "\n" +
                    "(" + ExcelUtils.formatDate(item.getSalaryPeriod().getFromDate()) + " - " +
                    ExcelUtils.formatDate(item.getSalaryPeriod().getToDate()) + ")";
        }
        createCell(dataRow, cellIndex++, salaryPeriodInfo, dataCellStyle);

//        // Dữ liệu từ
//        createCell(dataRow, cellIndex++,
//                item.getSalaryResult() != null ? item.getSalaryResult().getName() : "",
//                dataCellStyle);

        // Mức lương đóng BHXH
        createCell(dataRow, cellIndex++, formatMoney(item.getInsuranceSalary()), dataCellStyle);

        // BHXH nhân viên đóng (số tiền + tỷ lệ)
        createInsuranceCell(dataRow, cellIndex++,
                item.getStaffSocialInsuranceAmount(),
                item.getStaffSocialInsurancePercentage(),
                dataCellStyle);

        // BHYT nhân viên đóng
        createInsuranceCell(dataRow, cellIndex++,
                item.getStaffHealthInsuranceAmount(),
                item.getStaffHealthInsurancePercentage(),
                dataCellStyle);

        // BHTN nhân viên đóng
        createInsuranceCell(dataRow, cellIndex++,
                item.getStaffUnemploymentInsuranceAmount(),
                item.getStaffUnemploymentInsurancePercentage(),
                dataCellStyle);

        // Tổng nhân viên đóng
        createCell(dataRow, cellIndex++, formatMoney(item.getStaffTotalInsuranceAmount()), dataCellStyle);

        // BHXH công ty đóng
        createInsuranceCell(dataRow, cellIndex++,
                item.getOrgSocialInsuranceAmount(),
                item.getOrgSocialInsurancePercentage(),
                dataCellStyle);

        // BHYT công ty đóng
        createInsuranceCell(dataRow, cellIndex++,
                item.getOrgHealthInsuranceAmount(),
                item.getOrgHealthInsurancePercentage(),
                dataCellStyle);

        // BHTN công ty đóng
        createInsuranceCell(dataRow, cellIndex++,
                item.getOrgUnemploymentInsuranceAmount(),
                item.getOrgUnemploymentInsurancePercentage(),
                dataCellStyle);

        // Tổng công ty đóng
        createCell(dataRow, cellIndex++, formatMoney(item.getOrgTotalInsuranceAmount()), dataCellStyle);

        // Tổng tiền
        createCell(dataRow, cellIndex++, formatMoney(item.getTotalInsuranceAmount()), dataCellStyle);

        // Trạng thái
        String paidStatus = getPaidStatusName(item.getPaidStatus());
        createCell(dataRow, cellIndex++, paidStatus, dataCellStyle);

        rowIndex[0]++;
    }

    private void writeTotalRowToExcel(
            List<StaffSocialInsuranceDto> list,
            Sheet sheet,
            CellStyle dataCellStyle,
            int[] rowIndex) {

        Row totalRow = sheet.createRow(rowIndex[0]);
        int mergeToIndex = 3; // Gộp từ cột 0 đến 3 cho nhãn "Tổng"

        // Gộp cell từ cột 0 đến mergeToIndex
        sheet.addMergedRegion(new CellRangeAddress(rowIndex[0], rowIndex[0], 0, mergeToIndex));
        Cell mergedCell = totalRow.createCell(0);
        mergedCell.setCellValue("Tổng");
        mergedCell.setCellStyle(dataCellStyle);

        // Tạo các ô trống cho vùng được gộp (tránh lỗi khi ghi đè)
        for (int i = 1; i <= mergeToIndex; i++) {
            Cell blank = totalRow.createCell(i);
            blank.setCellStyle(dataCellStyle);
        }

        int cellIndex = mergeToIndex + 1;

        // Các cột tiền tệ cần tính tổng
        double totalInsuranceSalary = 0.0;
        double totalStaffSocial = 0.0;
        double totalStaffHealth = 0.0;
        double totalStaffUnemp = 0.0;
        double totalStaffTotal = 0.0;

        double totalOrgSocial = 0.0;
        double totalOrgHealth = 0.0;
        double totalOrgUnemp = 0.0;
        double totalOrgTotal = 0.0;

        double totalAll = 0.0;

        for (StaffSocialInsuranceDto item : list) {
            totalInsuranceSalary += safe(item.getInsuranceSalary());
            totalStaffSocial += safe(item.getStaffSocialInsuranceAmount());
            totalStaffHealth += safe(item.getStaffHealthInsuranceAmount());
            totalStaffUnemp += safe(item.getStaffUnemploymentInsuranceAmount());
            totalStaffTotal += safe(item.getStaffTotalInsuranceAmount());

            totalOrgSocial += safe(item.getOrgSocialInsuranceAmount());
            totalOrgHealth += safe(item.getOrgHealthInsuranceAmount());
            totalOrgUnemp += safe(item.getOrgUnemploymentInsuranceAmount());
            totalOrgTotal += safe(item.getOrgTotalInsuranceAmount());

            totalAll += safe(item.getTotalInsuranceAmount());
        }

        // Ghi từng ô tổng (không cần tỷ lệ phần trăm)
        createCell(totalRow, cellIndex++, formatMoney(totalInsuranceSalary), dataCellStyle);
        createCell(totalRow, cellIndex++, formatMoney(totalStaffSocial), dataCellStyle);
        createCell(totalRow, cellIndex++, formatMoney(totalStaffHealth), dataCellStyle);
        createCell(totalRow, cellIndex++, formatMoney(totalStaffUnemp), dataCellStyle);
        createCell(totalRow, cellIndex++, formatMoney(totalStaffTotal), dataCellStyle);
        createCell(totalRow, cellIndex++, formatMoney(totalOrgSocial), dataCellStyle);
        createCell(totalRow, cellIndex++, formatMoney(totalOrgHealth), dataCellStyle);
        createCell(totalRow, cellIndex++, formatMoney(totalOrgUnemp), dataCellStyle);
        createCell(totalRow, cellIndex++, formatMoney(totalOrgTotal), dataCellStyle);
        createCell(totalRow, cellIndex++, formatMoney(totalAll), dataCellStyle);

        // Ô trạng thái cuối cùng (trống)
        createCell(totalRow, cellIndex, "", dataCellStyle);

        rowIndex[0]++;
    }

    private double safe(Double value) {
        return value != null ? value : 0.0;
    }


    private void createInsuranceCell(Row row, int cellIndex, Double amount, Double percentage, CellStyle style) {
        String value = "";
        if (amount != null || percentage != null) {
            value = (amount != null ? formatMoney(amount) : "0");
//                    +  (percentage != null ? " (" + percentage + "%)" : "");
        }
        createCell(row, cellIndex, value, style);
    }

    private String formatMoney(Double amount) {
        if (amount == null) return "0";
        DecimalFormat formatter = new DecimalFormat("#,##0.00"); // Thêm .00 để luôn hiển thị 2 số thập phân
        return formatter.format(amount);
    }

    private String getPaidStatusName(Integer paidStatus) {
        if (paidStatus == null) return "";
        return paidStatus == 1 ? "Đã thanh toán" : "Chưa thanh toán";
    }

    private void createCell(Row row, int cellIndex, Object value, CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());

            // Thêm định dạng số với 2 chữ số thập phân
            CellStyle numberStyle = row.getSheet().getWorkbook().createCellStyle();
            numberStyle.cloneStyleFrom(style);
            numberStyle.setDataFormat(row.getSheet().getWorkbook()
                    .createDataFormat()
                    .getFormat("0.00"));
            cell.setCellStyle(numberStyle);
            return;
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
            CellStyle dateStyle = row.getSheet().getWorkbook().createCellStyle();
            dateStyle.cloneStyleFrom(style);
            CreationHelper createHelper = row.getSheet().getWorkbook().getCreationHelper();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
            cell.setCellStyle(dateStyle);
            return;
        } else {
            cell.setCellValue(value.toString());
        }
        cell.setCellStyle(style);
    }

    @Override
    public SearchStaffSocialInsuranceDto getInitialFilter() {
        SearchStaffSocialInsuranceDto response = new SearchStaffSocialInsuranceDto();

        response.setPaidStatus(0);
        response.setPageIndex(1);
        response.setPageSize(10);

        List<SalaryPeriodDto> activePeriods = salaryPeriodService.getActivePeriodsByDate(new Date());
        if (activePeriods != null && !activePeriods.isEmpty()) {
            SalaryPeriodDto period = activePeriods.get(0);
            response.setSalaryPeriod(period);
            response.setSalaryPeriodId(period.getId());
            response.setFromDate(period.getFromDate());
            response.setToDate(period.getToDate());
        }

        Staff staff = userExtService.getCurrentStaffEntity();
        if (staff == null) {
            return response;
        }

        response.setStaff(new StaffDto());
        response.getStaff().setId(staff.getId());
        response.getStaff().setStaffCode(staff.getStaffCode());
        response.getStaff().setDisplayName(staff.getDisplayName());
        response.setStaffId(response.getStaff().getId());

        if (staff.getCurrentPositions() != null && !staff.getCurrentPositions().isEmpty()) {
            Position mainPosition = null;

            for (Position position : staff.getCurrentPositions()) {
                if (position != null && position.getIsMain() != null && position.getIsMain().equals(true)) {
                    mainPosition = position;
                    break;
                }
            }

            if (mainPosition != null) {

                if (mainPosition.getTitle() != null) {
                    PositionTitleDto positionTitle = new PositionTitleDto();

                    positionTitle.setId(mainPosition.getTitle().getId());
                    positionTitle.setCode(mainPosition.getTitle().getCode());
                    positionTitle.setName(mainPosition.getTitle().getName());

                    response.setPositionTitle(positionTitle);
                    response.setPositionTitleId(positionTitle.getId());
                }

                if (mainPosition.getDepartment() != null) {
                    HRDepartmentDto department = new HRDepartmentDto();

                    department.setId(mainPosition.getDepartment().getId());
                    department.setCode(mainPosition.getDepartment().getCode());
                    department.setName(mainPosition.getDepartment().getName());

                    response.setDepartment(department);
                    response.setDepartmentId(department.getId());
                }

                if (mainPosition.getDepartment() != null && mainPosition.getDepartment().getOrganization() != null) {
                    HrOrganizationDto organization = new HrOrganizationDto();

                    organization.setId(mainPosition.getDepartment().getOrganization().getId());
                    organization.setCode(mainPosition.getDepartment().getOrganization().getCode());
                    organization.setName(mainPosition.getDepartment().getOrganization().getName());

                    response.setOrganization(organization);
                    response.setOrganizationId(organization.getId());
                }
            }
        }

        return response;


    }

    @Override
    public Page<StaffSocialInsuranceDto> searchByPage(SearchStaffSocialInsuranceDto dto) {
        if (dto == null) {
            return null;
        }
        int pageIndex = dto.getPageIndex();
        int pageSize = dto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }
        if (dto.getStaff() != null) {
            dto.setStaffId(dto.getStaff().getId());
        }

        boolean isRoleManager = false;
        UserDto user = userExtService.getCurrentUser();
        if (user != null && user.getRoles() != null && user.getRoles().size() > 0) {
            for (RoleDto item : user.getRoles()) {
                if (item.getName() != null &&
                        ("ROLE_ADMIN".equals(item.getName()) || "HR_MANAGER".equals(item.getName())
                        )) {
                    isRoleManager = true;
                }
            }
        }
        if (!isRoleManager) {
            StaffDto currentStaff = userExtService.getCurrentStaff();
            dto.setStaffId(currentStaff.getId());
        }
        String whereClause = " where (1=1) ";
        String orderBy = " ORDER BY entity.createDate desc ";

        String sqlCount = "select count(distinct entity.id) from StaffSocialInsurance as entity ";
        String sql = "select new com.globits.hr.dto.staff.StaffSocialInsuranceDto(entity) from StaffSocialInsurance as entity ";
        String leftJoin = " Left join entity.staff staff ";

        leftJoin += " Left join entity.salaryResult salaryResult ";
        leftJoin += " Left join entity.salaryPeriod salaryPeriod ";
        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            whereClause += "AND (LOWER(staff.staffCode) LIKE LOWER(:text) " +
                    "  OR LOWER(staff.firstName) LIKE LOWER(:text) " +
                    "  OR LOWER(staff.lastName) LIKE LOWER(:text) " +
                    "  OR LOWER(staff.displayName) LIKE LOWER(:text) " +
                    "  OR LOWER(salaryResult.name) LIKE LOWER(:text) " +
                    "  OR LOWER(salaryPeriod.name) LIKE LOWER(:text)) ";
        }

        String joinPositionStaff = "";
        boolean hasJoinMainPosition = false;
        if (dto.getPositionTitleId() != null || dto.getDepartmentId() != null || dto.getOrganizationId() != null) {
            joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.staff.id ";
            hasJoinMainPosition = true;
        }

        if (dto.getSalaryPeriod() != null) {
            whereClause += " and (entity.salaryPeriod.id = :salaryPeriodId) ";
        }
        if (dto.getSalaryResult() != null) {
            whereClause += " and (entity.salaryResult.id = :salaryResultId) ";
        }
        if (dto.getStaffId() != null) {
            whereClause += " and (entity.staff.id = :staffId) ";
        }
        if (dto.getPaidStatus() != null) {
            whereClause += " and (entity.paidStatus = :paidStatus) ";
        }
        // ContractOrganization - WorkOrganization theo hop dong gan nhat
        // Đơn vị ký hợp đồng
        if (dto.getContractOrganizationId() != null) {
            whereClause += " and (entity.staff.id in ( SELECT sa.staff.id FROM StaffLabourAgreement sa"
                    + " where sa.contractOrganization.id =: contractOrganizationId "
                    + " and sa.startDate = (SELECT MAX(sa2.startDate) FROM StaffLabourAgreement sa2 WHERE sa2.staff.id = sa.staff.id ) )) ";
        }

        if (hasJoinMainPosition) {
            if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
                whereClause += " AND ( pos.department.organization.id  =: organizationId ) ";
            }
            if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
                whereClause += " AND ( pos.department.id  =: departmentId ) ";
            }
            if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
                whereClause += " AND ( pos.title.id  =: positionTitleId ) ";
            }
        }

        sql += joinPositionStaff + leftJoin + whereClause + orderBy;
        sqlCount += joinPositionStaff + leftJoin + whereClause;

        Query query = manager.createQuery(sql, StaffSocialInsuranceDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (dto.getKeyword() != null && StringUtils.hasText(dto.getKeyword())) {
            String keyword = "%" + dto.getKeyword().strip() + "%"; // Chuyển keyword thành chữ thường
            query.setParameter("text", keyword);
            qCount.setParameter("text", keyword);
        }
        if (dto.getSalaryPeriod() != null) {
            query.setParameter("salaryPeriodId", dto.getSalaryPeriod().getId());
            qCount.setParameter("salaryPeriodId", dto.getSalaryPeriod().getId());
        }
        if (dto.getSalaryResult() != null) {
            query.setParameter("salaryResultId", dto.getSalaryResult().getId());
            qCount.setParameter("salaryResultId", dto.getSalaryResult().getId());
        }
        if (dto.getStaffId() != null) {
            query.setParameter("staffId", dto.getStaffId());
            qCount.setParameter("staffId", dto.getStaffId());
        }
        if (dto.getPaidStatus() != null) {
            query.setParameter("paidStatus", dto.getPaidStatus());
            qCount.setParameter("paidStatus", dto.getPaidStatus());
        }

        if (hasJoinMainPosition) {
            if (dto.getOrganizationId() != null && StringUtils.hasText(dto.getOrganizationId().toString())) {
                query.setParameter("organizationId", dto.getOrganizationId());
                qCount.setParameter("organizationId", dto.getOrganizationId());
            }
            if (dto.getDepartmentId() != null && StringUtils.hasText(dto.getDepartmentId().toString())) {
                query.setParameter("departmentId", dto.getDepartmentId());
                qCount.setParameter("departmentId", dto.getDepartmentId());
            }
            if (dto.getPositionTitleId() != null && StringUtils.hasText(dto.getPositionTitleId().toString())) {
                query.setParameter("positionTitleId", dto.getPositionTitleId());
                qCount.setParameter("positionTitleId", dto.getPositionTitleId());
            }
        }
        if (dto.getContractOrganizationId() != null) {
            query.setParameter("contractOrganizationId", dto.getContractOrganizationId());
            qCount.setParameter("contractOrganizationId", dto.getContractOrganizationId());
        }
        List<StaffSocialInsuranceDto> entities = new ArrayList<>();
        long count = (long) qCount.getSingleResult();
        Page<StaffSocialInsuranceDto> result;

        int startPosition = pageIndex * pageSize;
        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        entities = query.getResultList();
        result = new PageImpl<>(entities, pageable, count);

        return result;
    }

    @Override
    @Modifying
    @Transactional
    public Boolean deleteMultiple(List<UUID> ids) {
        if (ids == null)
            return false;
        boolean isValid = true;
        for (UUID itemId : ids) {
            boolean deleteRes = this.deleteStaffSocialInsurance(itemId);
            if (!deleteRes)
                isValid = false;
        }
        return isValid;
    }

    @Override
    public void handleSocialInsuranceByChangingStatus(SalaryResultStaff entity, Integer newApprovalStatus) {
        if (newApprovalStatus == null || entity.getSalaryResult() == null
                || !entity.getSalaryResult().getSalaryTemplate().getCode().equals(HrConstants.MAU_BANG_LUONG_THUC_TE))
            return;

        // Nếu phiếu lương được chốt thì sẽ tạo ra phiếu chi trả BHXH
        if (newApprovalStatus.equals(HrConstants.SalaryResulStaffApprovalStatus.APPROVED.getValue())
                && entity.getStaff() != null && entity.getStaff().getHasSocialIns() != null
                && entity.getStaff().getHasSocialIns().equals(true)) {
            List<StaffSocialInsurance> availableOldRecords = staffSocialInsuranceRepository.findByStaffIdAndSalaryResultId(entity.getStaff().getId(), entity.getSalaryResult().getId());

            if (availableOldRecords == null || availableOldRecords.isEmpty()) {
                this.generateFromResultStaff(entity);
            }
        }
        // Bỏ phiếu chi trả BHXH khi thay đổi trạng thái phiếu lương khác đã chốt
        else {
            if (entity.getStaff() != null && entity.getSalaryResult() != null) {
                List<StaffSocialInsurance> availableOldRecords = staffSocialInsuranceRepository.findByStaffIdAndSalaryResultId(entity.getStaff().getId(), entity.getSalaryResult().getId());

                if (availableOldRecords != null && !availableOldRecords.isEmpty()) {
                    staffSocialInsuranceRepository.deleteAll(availableOldRecords);
                }
            }
        }

        entityManager.flush();
        entityManager.clear();
    }


    @Override
    public StaffSocialInsuranceDto generateFromResultStaff(SalaryResultStaff resultStaff) {
        if (resultStaff == null || resultStaff.getStaff() == null || resultStaff.getSalaryResult() == null)
            return null;
//        if (resultStaff.getStaff().getHasSocialIns() == null || resultStaff.getStaff().getHasSocialIns().equals(false)) {
//            return null;
//        }

        StaffSocialInsurance entity = null;

        List<StaffSocialInsurance> availableRecords = staffSocialInsuranceRepository
                .findByStaffIdAndSalaryResultId(resultStaff.getStaff().getId(), resultStaff.getSalaryResult().getId());
        if (availableRecords != null && !availableRecords.isEmpty()) {
            entity = availableRecords.get(0);
        }
        if (entity == null) {
            entity = new StaffSocialInsurance();
            entity.setStaff(resultStaff.getStaff());
            entity.setSalaryResult(resultStaff.getSalaryResult());
            if (resultStaff.getSalaryResult().getSalaryPeriod() != null) {
                entity.setSalaryPeriod(resultStaff.getSalaryResult().getSalaryPeriod());
                entity.setStartDate(resultStaff.getSalaryResult().getSalaryPeriod().getFromDate());
                entity.setEndDate(resultStaff.getSalaryResult().getSalaryPeriod().getToDate());
            }
        }

        entity.setPaidStatus(HrConstants.StaffSocialInsurancePaidStatus.UNPAID.getValue());

        // Mức lương tham gia bảo hiểm xã hội
        this.handleSetInsuranceSalary(resultStaff, entity);

        // Tỷ lệ đóng BHXH của nhân viên
        this.handleSetStaffSocialInsurancePercentage(resultStaff, entity);

        // Tỷ lệ đóng BHYT của nhân viên
        this.handleSetStaffHealthInsurancePercentage(resultStaff, entity);

        // Tỷ lệ đóng BHTN của nhân viên
        this.handleSetStaffUnEmployedInsurancePercentage(resultStaff, entity);

        // Tổng tiền bảo hiểm mà nhân viên đóng
        Double staffTotalInsurance = 0D;
        if (entity.getInsuranceSalary() != null && entity.getInsuranceSalary() > 0) {
            if (entity.getStaffSocialInsurancePercentage() != null && entity.getStaffSocialInsurancePercentage() > 0) {
                staffTotalInsurance += entity.getInsuranceSalary() * entity.getStaffSocialInsurancePercentage();
            }

            if (entity.getStaffHealthInsurancePercentage() != null && entity.getStaffHealthInsurancePercentage() > 0) {
                staffTotalInsurance += entity.getInsuranceSalary() * entity.getStaffHealthInsurancePercentage();
            }

            if (entity.getStaffUnemploymentInsurancePercentage() != null && entity.getStaffUnemploymentInsurancePercentage() > 0) {
                staffTotalInsurance += entity.getInsuranceSalary() * entity.getStaffUnemploymentInsurancePercentage();
            }
        }
        entity.setStaffTotalInsuranceAmount(staffTotalInsurance);

        // Tỷ lệ đóng BHXH của công ty
        this.handleSetOrgSocialInsurancePercentage(resultStaff, entity);

        // Tỷ lệ đóng BHYT của công ty
        this.handleSetOrgHealthInsurancePercentage(resultStaff, entity);

        // Tỷ lệ đóng BHTN của công ty
        this.handleSetOrgUnemploymentInsurancePercentage(resultStaff, entity);

        // Tổng tiền bảo hiểm mà công ty đóng
        Double orgTotalInsurance = 0D;
        if (entity.getInsuranceSalary() != null && entity.getInsuranceSalary() > 0) {
            if (entity.getOrgSocialInsurancePercentage() != null && entity.getOrgSocialInsurancePercentage() > 0) {
                orgTotalInsurance += entity.getInsuranceSalary() * entity.getOrgSocialInsurancePercentage();
            }

            if (entity.getOrgHealthInsurancePercentage() != null && entity.getOrgHealthInsurancePercentage() > 0) {
                orgTotalInsurance += entity.getInsuranceSalary() * entity.getOrgHealthInsurancePercentage();
            }

            if (entity.getOrgUnemploymentInsurancePercentage() != null && entity.getOrgUnemploymentInsurancePercentage() > 0) {
                orgTotalInsurance += entity.getInsuranceSalary() * entity.getOrgUnemploymentInsurancePercentage();
            }
        }
        entity.setOrgTotalInsuranceAmount(orgTotalInsurance);

        // Tổng tiền bảo hiểm (cả nhân viên + công ty)
        entity.setTotalInsuranceAmount(staffTotalInsurance + orgTotalInsurance);

        if (entity.getTotalInsuranceAmount() == null || entity.getTotalInsuranceAmount() == 0D) return null;


        entity = staffSocialInsuranceRepository.saveAndFlush(entity);
        StaffSocialInsuranceDto response = new StaffSocialInsuranceDto(entity);

        handleSetOrgStructureInfo(entity, response);

        entityManager.flush();
        entityManager.clear();


        return response;
    }

    private void handleSetOrgStructureInfo(StaffSocialInsurance entity, StaffSocialInsuranceDto response) {
        Staff staff = entity.getStaff();
        if (staff != null && staff.getCurrentPositions() != null && !staff.getCurrentPositions().isEmpty()) {
            Position mainPosition = null;

            for (Position position : staff.getCurrentPositions()) {
                if (position != null && position.getIsMain() != null && position.getIsMain().equals(true)) {
                    mainPosition = position;
                    break;
                }
            }

            if (mainPosition != null) {
                response.setMainPosition(mainPosition.getName());

                if (mainPosition.getTitle() != null) {
                    response.setMainPositionTitle(mainPosition.getTitle().getName());
                }

                if (mainPosition.getDepartment() != null) {
                    response.setMainDepartment(mainPosition.getDepartment().getName());
                }

                if (mainPosition.getDepartment() != null && mainPosition.getDepartment().getOrganization() != null) {
                    response.setMainOrganization(mainPosition.getDepartment().getOrganization().getName());
                }
            }
        }
    }

    // Làm tròn 2 chữ số sau dấu phẩy
    private double roundToTwoDecimalPlaces(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    // Mức lương tham gia bảo hiểm xã hội
    private void handleSetInsuranceSalary(SalaryResultStaff resultStaff, StaffSocialInsurance entity) {
        List<SalaryResultStaffItem> availableResults = salaryResultStaffItemRepository
                .findBySalaryResultStaffIdAndReferenceCode(resultStaff.getId(),
                        HrConstants.SalaryItemAutoConnectCode.MUC_LUONG_DONG_BAO_HIEM_XA_HOI.getValue());

        if (availableResults == null || availableResults.isEmpty()) {
            entity.setInsuranceSalary(0.00);
            return;
        }

        String insuranceValue = availableResults.get(0).getValue();

        try {
            if (insuranceValue == null) {
                entity.setInsuranceSalary(0.00);
                return;
            }

            double doubleNumber = Double.parseDouble(insuranceValue);
            entity.setInsuranceSalary(roundToTwoDecimalPlaces(doubleNumber));
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format: " + insuranceValue);
            entity.setInsuranceSalary(0.00);
        }
    }

    // Tỷ lệ đóng BHXH của nhân viên
    private void handleSetStaffSocialInsurancePercentage(SalaryResultStaff resultStaff, StaffSocialInsurance entity) {
        List<SalaryResultStaffItem> availableResults = salaryResultStaffItemRepository
                .findBySalaryResultStaffIdAndReferenceCode(resultStaff.getId(),
                        HrConstants.SalaryItemAutoConnectCode.BAO_HIEM_XA_HOI_NHAN_VIEN_DONG.getValue());

        if (availableResults == null || availableResults.isEmpty()) {
            entity.setStaffSocialInsurancePercentage(0.00);
            return;
        }

        String insuranceValue = availableResults.get(0).getValue();

        try {
            if (insuranceValue == null) {
                entity.setStaffSocialInsurancePercentage(0.00);
                return;
            }

            if (entity.getInsuranceSalary() == null || entity.getInsuranceSalary() == 0D) {
                entity.setStaffSocialInsurancePercentage(0D);
            } else {
                double doubleNumber = Double.parseDouble(insuranceValue);
                Double percentage = (doubleNumber / entity.getInsuranceSalary()) * 100;
                entity.setStaffSocialInsurancePercentage(roundToTwoDecimalPlaces(percentage));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format: " + insuranceValue);
            entity.setStaffSocialInsurancePercentage(0.00);
        }
    }

    // Tỷ lệ đóng BHYT của nhân viên
    private void handleSetStaffHealthInsurancePercentage(SalaryResultStaff resultStaff, StaffSocialInsurance entity) {
        List<SalaryResultStaffItem> availableResults = salaryResultStaffItemRepository
                .findBySalaryResultStaffIdAndReferenceCode(resultStaff.getId(),
                        HrConstants.SalaryItemAutoConnectCode.BAO_HIEM_Y_TE_NHAN_VIEN_DONG.getValue());

        if (availableResults == null || availableResults.isEmpty()) {
            entity.setStaffHealthInsurancePercentage(0.00);
            return;
        }

        String insuranceValue = availableResults.get(0).getValue();

        try {
            if (insuranceValue == null) {
                entity.setStaffHealthInsurancePercentage(0.00);
                return;
            }

            if (entity.getInsuranceSalary() == null || entity.getInsuranceSalary() == 0D) {
                entity.setStaffHealthInsurancePercentage(0D);
            } else {
                double doubleNumber = Double.parseDouble(insuranceValue);
                Double percentage = (doubleNumber / entity.getInsuranceSalary()) * 100;
                entity.setStaffHealthInsurancePercentage(roundToTwoDecimalPlaces(percentage));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format: " + insuranceValue);
            entity.setStaffHealthInsurancePercentage(0.00);
        }
    }

    // Tỷ lệ đóng BHTN của nhân viên
    private void handleSetStaffUnEmployedInsurancePercentage(SalaryResultStaff resultStaff, StaffSocialInsurance entity) {
        List<SalaryResultStaffItem> availableResults = salaryResultStaffItemRepository
                .findBySalaryResultStaffIdAndReferenceCode(resultStaff.getId(),
                        HrConstants.SalaryItemAutoConnectCode.BAO_HIEM_THAT_NGHIEP_NHAN_VIEN_DONG.getValue());

        if (availableResults == null || availableResults.isEmpty()) {
            entity.setStaffUnemploymentInsurancePercentage(0.00);
            return;
        }

        String insuranceValue = availableResults.get(0).getValue();

        try {
            if (insuranceValue == null) {
                entity.setStaffUnemploymentInsurancePercentage(0.00);
                return;
            }

            if (entity.getInsuranceSalary() == null || entity.getInsuranceSalary() == 0D) {
                entity.setStaffUnemploymentInsurancePercentage(0D);
            } else {
                double doubleNumber = Double.parseDouble(insuranceValue);
                Double percentage = (doubleNumber / entity.getInsuranceSalary()) * 100;
                entity.setStaffUnemploymentInsurancePercentage(roundToTwoDecimalPlaces(percentage));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format: " + insuranceValue);
            entity.setStaffUnemploymentInsurancePercentage(0.00);
        }
    }

    // Tỷ lệ đóng BHXH của công ty
    private void handleSetOrgSocialInsurancePercentage(SalaryResultStaff resultStaff, StaffSocialInsurance entity) {
        List<SalaryResultStaffItem> results = salaryResultStaffItemRepository
                .findBySalaryResultStaffIdAndReferenceCode(resultStaff.getId(),
                        HrConstants.SalaryItemAutoConnectCode.BAO_HIEM_XA_HOI_CONG_TY_DONG.getValue());

        parseAndSetOrgPercentage(results, entity::setOrgSocialInsurancePercentage, entity);
    }

    // Tỷ lệ đóng BHYT của công ty
    private void handleSetOrgHealthInsurancePercentage(SalaryResultStaff resultStaff, StaffSocialInsurance entity) {
        List<SalaryResultStaffItem> results = salaryResultStaffItemRepository
                .findBySalaryResultStaffIdAndReferenceCode(resultStaff.getId(),
                        HrConstants.SalaryItemAutoConnectCode.BAO_HIEM_Y_TE_CONG_TY_DONG.getValue());

        parseAndSetOrgPercentage(results, entity::setOrgHealthInsurancePercentage, entity);
    }

    // Tỷ lệ đóng BHTN của công ty
    private void handleSetOrgUnemploymentInsurancePercentage(SalaryResultStaff resultStaff, StaffSocialInsurance entity) {
        List<SalaryResultStaffItem> results = salaryResultStaffItemRepository
                .findBySalaryResultStaffIdAndReferenceCode(resultStaff.getId(),
                        HrConstants.SalaryItemAutoConnectCode.BAO_HIEM_THAT_NGHIEP_CONG_TY_DONG.getValue());

        parseAndSetOrgPercentage(results, entity::setOrgUnemploymentInsurancePercentage, entity);
    }

    // Hàm dùng chung cho các tỷ lệ công ty
    private void parseAndSetOrgPercentage(List<SalaryResultStaffItem> results,
                                          Consumer<Double> setter,
                                          StaffSocialInsurance entity) {
        if (results == null || results.isEmpty()) {
            setter.accept(0.00);
            return;
        }

        String value = results.get(0).getValue();
        try {
            if (value == null) {
                setter.accept(0.00);
                return;
            }

            if (entity.getInsuranceSalary() == null || entity.getInsuranceSalary() == 0D) {
                setter.accept(0D);
            } else {
                double number = Double.parseDouble(value);
                double percentage = (number / entity.getInsuranceSalary()) * 100;
                setter.accept(roundToTwoDecimalPlaces(percentage));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format (org percentage): " + value);
            setter.accept(0.00);
        }
    }


    @Override
    public List<UUID> generateSocialInsuranceTicketsForStaffsBySalaryPeriod(SearchStaffSocialInsuranceDto dto) {
        LocalDateTime time = logTime("Bắt đầu tạo bảng lương");

        if (dto == null || dto.getSalaryPeriod() == null) {
            return null;
        }

        List<UUID> response = new ArrayList<>();

        // Lấy danh sách tất cả nhân viên được đóng BHXH
        SearchStaffDto searchStaffDto = new SearchStaffDto();

        searchStaffDto.setHasSocialIns(true);

        int pageIndex = 1;
        int pageSize = 50;
        searchStaffDto.setPageSize(pageSize);
        searchStaffDto.setPageIndex(pageIndex);

        boolean hasNextPage = true;

        while (hasNextPage) {
            Page<StaffDto> needGenerateStaffs = staffService.searchByPage(searchStaffDto);
            time = logTime("Lấy danh sách nhân viên để tạo, trang " + pageIndex, time);

//            logger.info("New page: " + needGenerateStaffs.getContent().size() + " , pageIndex: " + pageIndex);

            hasNextPage = needGenerateStaffs.hasNext();
            pageIndex++;
            searchStaffDto.setPageIndex(pageIndex);

            for (StaffDto calStaff : needGenerateStaffs) {
                time = logTime("Tạo phiếu BHXH: " + calStaff.getDisplayName(), time);

                logger.info("Generating Insurance ticket for staff: " + calStaff.getDisplayName());
                SalaryResultStaffDto payslipRequest = new SalaryResultStaffDto();

                dto.setSalaryPeriodId(dto.getSalaryPeriod().getId());
                dto.setStaff(calStaff);
                dto.setStaffId(calStaff.getId());

                StaffSocialInsuranceDto createdTicket = this.generateSingleSocialInsuranceTicket(dto);
                if (createdTicket != null) {
                    response.add(createdTicket.getId());

                    time = logTime("Tạo thành công phiếu BHXH: " + calStaff.getDisplayName(), time);
                }
            }

            entityManager.flush();
            entityManager.clear();
        }

        time = logTime("Tạo thành công phiếu BHXH cho số lượng NV: " + response.size(), time);

        return response;
    }


    @Override
    public StaffSocialInsuranceDto generateSingleSocialInsuranceTicket(SearchStaffSocialInsuranceDto dto) {
        if (dto == null || (dto.getStaff() == null && dto.getStaffId() == null) || (dto.getSalaryPeriod() == null && dto.getSalaryPeriodId() == null)) {
            return null;
        }

        UUID staffId = dto.getStaffId();
        if (staffId == null) {
            staffId = dto.getStaff().getId();
        }

        UUID salaryPeriodId = dto.getSalaryPeriodId();
        if (salaryPeriodId == null) {
            salaryPeriodId = dto.getSalaryPeriod().getId();
        }

        if (staffId == null || salaryPeriodId == null) return null;


        StaffSocialInsurance entity = null;

        List<StaffSocialInsurance> availableRecords = staffSocialInsuranceRepository.findByStaffIdAndSalaryPeriodId(staffId, salaryPeriodId);
        if (availableRecords != null && !availableRecords.isEmpty()) {
            entity = availableRecords.get(0);
        }
        if (entity == null) {
            entity = new StaffSocialInsurance();
        }

        Staff staff = staffRepository.findById(staffId).orElse(null);
        if (staff == null) return null;
        entity.setStaff(staff);

        SalaryPeriod salaryPeriod = salaryPeriodRepository.findById(salaryPeriodId).orElse(null);
        if (salaryPeriod == null) return null;

        entity.setSalaryPeriod(salaryPeriod);

        entity.setStartDate(salaryPeriod.getFromDate());
        entity.setEndDate(salaryPeriod.getToDate());

        entity.setPaidStatus(HrConstants.StaffSocialInsurancePaidStatus.UNPAID.getValue());


        // Mức lương tham gia bảo hiểm xã hội
        double insuranceSalary = this.getSocialInsuranceAmount(staffId);
        entity.setInsuranceSalary(roundToTwoDecimalPlaces(insuranceSalary));

        // Tỷ lệ đóng BHXH của nhân viên
        entity.setStaffSocialInsurancePercentage(roundToTwoDecimalPlaces(8));

        // Tỷ lệ đóng BHYT của nhân viên
        entity.setStaffHealthInsurancePercentage(roundToTwoDecimalPlaces(1.5));

        // Tỷ lệ đóng BHTN của nhân viên
        entity.setStaffUnemploymentInsurancePercentage(roundToTwoDecimalPlaces(1));

        // Tổng tiền bảo hiểm mà nhân viên đóng
        Double staffTotalInsurance = 0D;
        if (entity.getInsuranceSalary() != null && entity.getInsuranceSalary() > 0) {
            if (entity.getStaffSocialInsurancePercentage() != null && entity.getStaffSocialInsurancePercentage() > 0) {
                staffTotalInsurance += entity.getInsuranceSalary() * entity.getStaffSocialInsurancePercentage() / 100;
            }

            if (entity.getStaffHealthInsurancePercentage() != null && entity.getStaffHealthInsurancePercentage() > 0) {
                staffTotalInsurance += entity.getInsuranceSalary() * entity.getStaffHealthInsurancePercentage() / 100;
            }

            if (entity.getStaffUnemploymentInsurancePercentage() != null && entity.getStaffUnemploymentInsurancePercentage() > 0) {
                staffTotalInsurance += entity.getInsuranceSalary() * entity.getStaffUnemploymentInsurancePercentage() / 100;
            }
        }
        entity.setStaffTotalInsuranceAmount(roundToTwoDecimalPlaces(staffTotalInsurance));

        // Tỷ lệ đóng BHXH của công ty
        entity.setOrgSocialInsurancePercentage(roundToTwoDecimalPlaces(17.5));

        // Tỷ lệ đóng BHYT của công ty
        entity.setOrgHealthInsurancePercentage(roundToTwoDecimalPlaces(3));

        // Tỷ lệ đóng BHTN của công ty
        entity.setOrgUnemploymentInsurancePercentage(roundToTwoDecimalPlaces(1));

        // Tổng tiền bảo hiểm mà công ty đóng
        Double orgTotalInsurance = 0D;
        if (entity.getInsuranceSalary() != null && entity.getInsuranceSalary() > 0) {
            if (entity.getOrgSocialInsurancePercentage() != null && entity.getOrgSocialInsurancePercentage() > 0) {
                orgTotalInsurance += entity.getInsuranceSalary() * entity.getOrgSocialInsurancePercentage() / 100;
            }

            if (entity.getOrgHealthInsurancePercentage() != null && entity.getOrgHealthInsurancePercentage() > 0) {
                orgTotalInsurance += entity.getInsuranceSalary() * entity.getOrgHealthInsurancePercentage() / 100;
            }

            if (entity.getOrgUnemploymentInsurancePercentage() != null && entity.getOrgUnemploymentInsurancePercentage() > 0) {
                orgTotalInsurance += entity.getInsuranceSalary() * entity.getOrgUnemploymentInsurancePercentage() / 100;
            }
        }
        entity.setOrgTotalInsuranceAmount(orgTotalInsurance);

        // Tổng tiền bảo hiểm (cả nhân viên + công ty)
        entity.setTotalInsuranceAmount(staffTotalInsurance + orgTotalInsurance);

        if (entity.getTotalInsuranceAmount() == null || entity.getTotalInsuranceAmount() == 0D) return null;


        entity = staffSocialInsuranceRepository.saveAndFlush(entity);
        StaffSocialInsuranceDto response = new StaffSocialInsuranceDto(entity);

        handleSetOrgStructureInfo(entity, response);

        entityManager.flush();
        entityManager.clear();


        return response;
    }

    private double getSocialInsuranceAmount(UUID staffId) {
        SystemConfigDto siaCodeConfig = systemConfigService.getByKeyCode(HrConstants.SystemConfigCode.INSURANCE_AMOUNT_SALARY_ITEM_CODE.getCode());
        if (siaCodeConfig == null || !StringUtils.hasText(siaCodeConfig.getConfigValue())) return 0.0;

        SalaryItemDto siaSalaryItem = salaryItemService.findByCode(siaCodeConfig.getConfigValue());
        if (siaSalaryItem == null) return 0.0;

        StaffSalaryItemValue insuranceValue = staffSalaryItemValueService.findCurrentByStaffIdAndSalaryItemId(staffId, siaSalaryItem.getId());
        if (insuranceValue == null || insuranceValue.getValue() == null) return 0.0;

        return insuranceValue.getValue();
    }
}
