package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.Allowance;
import com.globits.hr.domain.Position;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffAllowance;
import com.globits.hr.dto.HRDepartmentDto;
import com.globits.hr.dto.HrOrganizationDto;
import com.globits.hr.dto.PositionTitleDto;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.dto.staff.StaffSocialInsuranceDto;
import com.globits.hr.repository.StaffAllowanceRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.service.UserExtService;
import com.globits.salary.domain.*;
import com.globits.salary.dto.*;
import com.globits.salary.dto.excel.ImportSalaryResultStaffDto;
import com.globits.salary.dto.excel.ImportSalaryStaffItemValueDto;
import com.globits.salary.dto.excel.SalaryResultStaffItemImportDto;
import com.globits.salary.dto.search.SalaryCalculatePayslipDto;
import com.globits.salary.dto.search.SearchSalaryResultStaffDto;
import com.globits.salary.dto.search.SearchStaffAdvancePaymentDto;
import com.globits.salary.repository.*;
import com.globits.salary.service.*;
import com.globits.security.dto.RoleDto;
import com.globits.security.dto.UserDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.*;

@Service
public class SalaryPayslipServiceImpl extends GenericServiceImpl<SalaryResultStaff, UUID>
        implements SalaryPayslipService {

    private static final Logger logger = LoggerFactory.getLogger(SalaryResultStaffDto.class);

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
    private SalaryResultItemRepository salaryResultItemRepository;

    @Autowired
    private SalaryItemService salaryItemService;

    @Autowired
    private ExpressionEvaluatorService expressionEvaluatorService;

    @Autowired
    private StaffAllowanceRepository staffAllowanceRepository;

    @Autowired
    private StaffSalaryItemValueRepository staffSalaryItemValueRepository;

    @Autowired
    private SalaryResultStaffItemService salaryResultStaffItemService;

    @Autowired
    private SalaryResultStaffService salaryResultStaffService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SalaryPeriodService salaryPeriodService;

    @Autowired
    private UserExtService userExtService;

//    private void getObserversOfCell(SalaryResultStaffItemDto cell, Map<UUID, List<String>> varsToCalCellValueMap, Map<UUID, List<UUID>> mapObservers) {
//        if (cell == null || cell.getId() == null || mapObservers.containsKey(cell.getId()))
//            return;
//
//        mapObservers.put(cell.getId(), new ArrayList<UUID>());
//
//        SalaryResultStaff calculatingRow = salaryResultStaffRepository.findById(cell.getSalaryResultStaffId()).orElse(null);
//        if (calculatingRow == null || calculatingRow.getSalaryResultStaffItems() == null || calculatingRow.getSalaryResultStaffItems().size() == 0) {
//            return;
//        }
//
//        List<UUID> observerIds = new ArrayList<>();
//        for (SalaryResultStaffItem cellInRow : calculatingRow.getSalaryResultStaffItems()) {
//            if (cellInRow.getId() == null || cellInRow.getId().equals(cell.getId())) continue;
//            List<String> varToCalCellValue = varsToCalCellValueMap.get(cellInRow.getId());
//
//            if (varToCalCellValue == null || varToCalCellValue.size() == 0) continue;
//            if (varToCalCellValue.contains(cell.getReferenceCode())) observerIds.add(cellInRow.getId());
//
//            if (mapObservers.containsKey(cellInRow.getId())) continue;
//            getObserversOfCell(new SalaryResultStaffItemDto(cellInRow), varsToCalCellValueMap, mapObservers);
//        }
//
//        mapObservers.put(cell.getId(), observerIds);
//    }
//
//    private Map<UUID, List<String>> getVarsToCalCellValueMapInRow(UUID salaryResultStaffId) {
//        if (salaryResultStaffId == null) return null;
//
//        Map<UUID, List<String>> varsToCalCellValueMap = new HashMap<>();
//
//        SalaryResultStaff calculatingRow = salaryResultStaffRepository.findById(salaryResultStaffId).orElse(null);
//        if (calculatingRow == null || calculatingRow.getSalaryResultStaffItems() == null || calculatingRow.getSalaryResultStaffItems().size() == 0)
//            return varsToCalCellValueMap;
//
//        for (SalaryResultStaffItem cellInRow : calculatingRow.getSalaryResultStaffItems()) {
//            if (cellInRow.getSalaryResultItem() == null || cellInRow.getSalaryResultItem().getUsingFormula() == null) {
//                varsToCalCellValueMap.put(cellInRow.getId(), new ArrayList<>());
//                continue;
//            }
//
//            List<String> varsInFormula = salaryItemService.extractVariables(cellInRow.getSalaryResultItem().getUsingFormula());
//            varsToCalCellValueMap.put(cellInRow.getId(), varsInFormula);
//        }
//
//        return varsToCalCellValueMap;
//    }
//
//    private void calculateFromCell(SalaryResultStaffItemDto calCell, String newValue,
//                                   List<SalaryResultStaffItemDto> othersInRow,
//                                   Map<UUID, List<String>> varsToCalCellValueMap,
//                                   Map<UUID, List<UUID>> mapObservers) {
//        calCell.setValue(newValue);
//
//        if (!mapObservers.containsKey(calCell.getId()) || mapObservers.get(calCell.getId()) == null || mapObservers.get(calCell.getId()).size() == 0)
//            return;
//        List<SalaryResultStaffItemDto> needToRecalculateCells = new ArrayList<>();
//        for (SalaryResultStaffItemDto otherCell : othersInRow) {
//            if (otherCell.getId().equals(calCell.getId())) continue;
//
//            if (!mapObservers.get(calCell.getId()).contains(otherCell.getId())) continue;
//            needToRecalculateCells.add(otherCell);
//        }
//
//        for (SalaryResultStaffItemDto recalculateCell : needToRecalculateCells) {
//
//        }
//
//    }
//
//    @Override
//    public SalaryResultStaffDto recalculateByChangingCellValue(SalaryResultStaffItemDto cell) {
//        if (cell == null || cell.getId() == null || cell.getSalaryResultStaffId() == null) return null;
//        SalaryResultStaff calculatingRowEntity = salaryResultStaffRepository.findById(cell.getSalaryResultStaffId()).orElse(null);
//        if (calculatingRowEntity == null || calculatingRowEntity.getSalaryResultStaffItems() == null || calculatingRowEntity.getSalaryResultStaffItems().size() == 0)
//            return null;
//        SalaryResultStaffDto calculatingRow = new SalaryResultStaffDto(calculatingRowEntity);
//        List<SalaryResultStaffItemDto> cellsInRow = calculatingRow.getSalaryResultStaffItems();
//
//        SalaryResultStaffItemDto calCellInRow = null;
//        for (SalaryResultStaffItemDto cellInRow : cellsInRow) {
//            if (cellInRow.getId().equals(cell.getId())) {
//                calCellInRow = cellInRow;
//                break;
//            }
//        }
//        if (calCellInRow == null) return calculatingRow;
//
//        // cell value is not changed => do not need to recalculate
//        if (cell.getValue() != null && cell.getValue().equals(calCellInRow.getValue())) return calculatingRow;
//
//        // map that a cell has which variables
//        Map<UUID, List<String>> varsToCalCellValueMap = getVarsToCalCellValueMapInRow(cell.getSalaryResultStaffId());
//        // map that when a cell changes, which other cells have to recalculate afterward
//        Map<UUID, List<UUID>> mapObservers = new HashMap<UUID, List<UUID>>();
//        // generate values for mapObservers
//        getObserversOfCell(cell, varsToCalCellValueMap, mapObservers);
//
//        // recursively recalculate cells' value
//        calculateFromCell(calCellInRow, cell.getValue(), cellsInRow, varsToCalCellValueMap, mapObservers);
//
//        calculatingRow.setSalaryResultStaffItems(cellsInRow);
//        return calculatingRow;
//    }

    @Override
    public SalaryResultStaffDto reCalculateRowByChangingCellValue(SalaryCalculatePayslipDto dto) {
        if (dto == null || dto.getChangedCellId() == null || dto.getReCalculatingRow() == null) {
            return null;
        }

        DecimalFormat df = new DecimalFormat("0.##");
        Map<String, Object> currentValueMap = new HashMap<>();

        // map contains current value of each variables in row
        // initial data for map
        for (SalaryResultStaffItemDto cell : dto.getReCalculatingRow().getSalaryResultStaffItems()) {
            SalaryResultItem resultItemOfCell = salaryResultItemRepository.findById(cell.getSalaryResultItemId())
                    .orElse(null);
            if (resultItemOfCell == null)
                continue;

            // skip set value for TEXT value type
            if (resultItemOfCell.getValueType() != null && (resultItemOfCell.getValueType()
                    .equals(HrConstants.SalaryItemValueType.TEXT.getValue())
                    || resultItemOfCell.getValueType().equals(HrConstants.SalaryItemValueType.OTHERS.getValue())))
                continue;

            if (cell.getValue() != null) {
                currentValueMap.put(cell.getReferenceCode(), cell.getValue());
            } else {
                currentValueMap.put(cell.getReferenceCode(), "0.00");
            }
        }

        // from the previous valueMap, generate others cells which are calculated by
        // formula
        boolean needRecalculate = true;
        while (needRecalculate) {
            needRecalculate = false;

            for (SalaryResultStaffItemDto cell : dto.getReCalculatingRow().getSalaryResultStaffItems()) {
                // do not recalculate cell changed value manually
                if (cell.getId().equals(dto.getChangedCellId()))
                    continue;

                SalaryResultItem resultItemOfCell = salaryResultItemRepository.findById(cell.getSalaryResultItemId())
                        .orElse(null);
                if (resultItemOfCell == null)
                    continue;

                String cellReferenceCode = cell.getReferenceCode();
                String cellUsingFormula = resultItemOfCell.getUsingFormula();
                Object currentValue = cell.getValue();

                // hard code handling for ThueTNCN
                if (cell.getReferenceCode()
                        .equals(HrConstants.SalaryItemCodeSystemDefault.THUE_TNCN_SYSTEM.getValue())) {
                    Object thuNhapTinhThueVal = null;
                    if (currentValueMap.containsKey(
                            HrConstants.SalaryItemCodeSystemDefault.THU_NHAP_TINH_THUE_SYSTEM.getValue())) {
                        thuNhapTinhThueVal = currentValueMap
                                .get(HrConstants.SalaryItemCodeSystemDefault.THU_NHAP_TINH_THUE_SYSTEM.getValue());
                    }
                    if (thuNhapTinhThueVal == null)
                        continue;

                    Double calculatedValue = calculatePersonalIncomeTax(thuNhapTinhThueVal);
                    if (currentValue != null && areObjectsEqualAsDoubles(currentValue, calculatedValue))
                        continue;

                    needRecalculate = true;
                    currentValueMap.put(cell.getReferenceCode(), calculatedValue);

                    String stringFormat = df.format(calculatedValue);
                    cell.setValue(stringFormat);

                    // skip all the remaining code below
                    continue;
                }

                if (resultItemOfCell.getAllowance() == null && resultItemOfCell.getUsingFormula() == null)
                    continue;
                    // nếu là phụ cấp thì lấy công thức tính phụ cấp theo từng nhân viên
                else if (resultItemOfCell.getAllowance() != null) {
                    this.setAllowanceFormulaInCell(cell.getId(), resultItemOfCell);
                }

                Object calculatedValue = expressionEvaluatorService
                        .evaluateExpression(resultItemOfCell.getUsingFormula(), currentValueMap);

                // Xử lý trường hợp thành phần lương có calculationType là THRESHOLD
                SalaryResultStaffItem cellEntity = salaryResultStaffItemRepository.findById(cell.getId()).orElse(null);
                if (cellEntity != null && cellEntity.getSalaryResultItem().getCalculationType() != null
                        && cellEntity.getSalaryResultItem().getCalculationType()
                        .equals(HrConstants.SalaryItemCalculationType.THRESHOLD.getValue())) {
                    calculatedValue = calculateThresholdValueWithEntity(cellEntity, currentValueMap);
                }

                // Xử lý trường hợp thành phần lương có calculationType là FIX
                if (cellEntity != null && cellEntity.getSalaryResultItem().getCalculationType() != null
                        && cellEntity.getSalaryResultItem().getCalculationType()
                        .equals(HrConstants.SalaryItemCalculationType.FIX.getValue())) {
                    calculatedValue = calculateFixValueWithEntity(cellEntity);
                }

                // hard code handling for Thu nhap tinh thue
                if (cell.getReferenceCode()
                        .equals(HrConstants.SalaryItemCodeSystemDefault.THU_NHAP_TINH_THUE_SYSTEM.getValue())
                        && calculatedValue != null) {
                    // Kiểm tra xem calculatedValue có phải là một đối tượng Double
                    if (calculatedValue instanceof Double) {
                        // So sánh và gán lại giá trị nếu cần
                        if ((Double) calculatedValue < 0.0) {
                            calculatedValue = 0.0;
                        }
                    } else if (calculatedValue instanceof Number) {
                        // Nếu là một đối tượng Number khác (int, float, v.v.), chuyển sang Double
                        if (((Number) calculatedValue).doubleValue() < 0.0) {
                            calculatedValue = 0.0;
                        }
                    }
                }

                if (currentValue == null && calculatedValue == null)
                    continue;
                if (currentValue != null && calculatedValue != null
                        && areObjectsEqualAsDoubles(currentValue, calculatedValue))
                    continue;

                needRecalculate = true;
                currentValueMap.put(cell.getReferenceCode(), calculatedValue);

                String stringFormat = df.format(calculatedValue);
                cell.setValue(stringFormat);
            }
        }

        // fill all remain numeric cells which do not contain value is '0'
        for (SalaryResultStaffItemDto cell : dto.getReCalculatingRow().getSalaryResultStaffItems()) {
            SalaryResultItem resultItemOfCell = salaryResultItemRepository.findById(cell.getSalaryResultItemId())
                    .orElse(null);
            if (resultItemOfCell == null)
                continue;

            if (cell.getValue() != null || resultItemOfCell.getValueType() == null)
                continue;
            if (resultItemOfCell.getValueType().equals(HrConstants.SalaryItemValueType.TEXT.getValue())
                    || resultItemOfCell.getValueType().equals(HrConstants.SalaryItemValueType.OTHERS.getValue()))
                continue;
            cell.setValue("0.00");
        }

        SalaryResultStaffDto response = dto.getReCalculatingRow();

        return response;
    }

    @Override
    public void autoCalculateCellValueInEachRow(SalaryResult entity) {
        if (entity == null)
            return;

        DecimalFormat df = new DecimalFormat("0.##");
        for (SalaryResultStaff row : entity.getSalaryResultStaffs()) {
            Map<String, Object> currentValueMap = new HashMap<>();

            // initial data for map
            for (SalaryResultStaffItem cell : row.getSalaryResultStaffItems()) {
                // skip set value for TEXT value type
                if (cell.getSalaryResultItem().getValueType() != null && (cell.getSalaryResultItem().getValueType()
                        .equals(HrConstants.SalaryItemValueType.TEXT.getValue())
                        || cell.getSalaryResultItem().getValueType()
                        .equals(HrConstants.SalaryItemValueType.OTHERS.getValue())))
                    continue;

                if (cell.getValue() != null) {
                    currentValueMap.put(cell.getReferenceCode(), cell.getValue());
                } else {
                    currentValueMap.put(cell.getReferenceCode(), "0.00");
                }
            }

            // from the previous valueMap, generate others cells which are calculated by
            // formula
            boolean needRecalculate = true;
            while (needRecalculate) {
                needRecalculate = false;

                for (SalaryResultStaffItem cell : row.getSalaryResultStaffItems()) {
//                    String cellReferenceCode = cell.getReferenceCode();
//                    String cellUsingFormula = cell.getSalaryResultItem().getUsingFormula();
                    Object currentValue = cell.getValue();

                    // hard code handling for ThueTNCN
                    if (cell.getReferenceCode()
                            .equals(HrConstants.SalaryItemCodeSystemDefault.THUE_TNCN_SYSTEM.getValue())) {
                        Object thuNhapTinhThueVal = null;
                        if (currentValueMap.containsKey(
                                HrConstants.SalaryItemCodeSystemDefault.THU_NHAP_TINH_THUE_SYSTEM.getValue())) {
                            thuNhapTinhThueVal = currentValueMap
                                    .get(HrConstants.SalaryItemCodeSystemDefault.THU_NHAP_TINH_THUE_SYSTEM.getValue());
                        }
                        if (thuNhapTinhThueVal == null)
                            continue;

                        Double calculatedValue = calculatePersonalIncomeTax(thuNhapTinhThueVal);
                        if (currentValue != null && areObjectsEqualAsDoubles(currentValue, calculatedValue))
                            continue;

                        needRecalculate = true;
                        currentValueMap.put(cell.getReferenceCode(), calculatedValue);

                        String stringFormat = df.format(calculatedValue);
                        cell.setValue(stringFormat);

                        // skip all the remaining code below
                        continue;
                    }

                    if (cell.getSalaryResultItem().getAllowance() == null
                            && cell.getSalaryResultItem().getUsingFormula() == null)
                        continue;
                        // nếu là phụ cấp thì lấy công thức tính phụ cấp theo từng nhân viên
                    else if (cell.getSalaryResultItem().getAllowance() != null) {
                        this.setAllowanceFormulaInCell(cell.getId(), cell.getSalaryResultItem());
                    }

                    Object calculatedValue = expressionEvaluatorService
                            .evaluateExpression(cell.getSalaryResultItem().getUsingFormula(), currentValueMap);

                    // Xử lý trường hợp thành phần lương có calculationType là THRESHOLD
                    if (cell.getSalaryResultItem().getCalculationType() != null && cell.getSalaryResultItem()
                            .getCalculationType().equals(HrConstants.SalaryItemCalculationType.THRESHOLD.getValue())) {
                        calculatedValue = calculateThresholdValueWithEntity(cell, currentValueMap);
                    }

                    // Xử lý trường hợp thành phần lương có calculationType là FIX
                    if (cell.getSalaryResultItem().getCalculationType() != null && cell.getSalaryResultItem()
                            .getCalculationType().equals(HrConstants.SalaryItemCalculationType.FIX.getValue())) {
                        calculatedValue = calculateFixValueWithEntity(cell);
                    }

                    // hard code handling for Thu nhap tinh thue
                    if (cell.getReferenceCode()
                            .equals(HrConstants.SalaryItemCodeSystemDefault.THU_NHAP_TINH_THUE_SYSTEM.getValue())
                            && calculatedValue != null) {
                        // Kiểm tra xem calculatedValue có phải là một đối tượng Double
                        if (calculatedValue instanceof Double) {
                            // So sánh và gán lại giá trị nếu cần
                            if ((Double) calculatedValue < 0.0) {
                                calculatedValue = 0.0;
                            }
                        } else if (calculatedValue instanceof Number) {
                            // Nếu là một đối tượng Number khác (int, float, v.v.), chuyển sang Double
                            if (((Number) calculatedValue).doubleValue() < 0.0) {
                                calculatedValue = 0.0;
                            }
                        }
                    }

                    if (currentValue == null && calculatedValue == null)
                        continue;
                    if (currentValue != null && calculatedValue != null
                            && areObjectsEqualAsDoubles(currentValue, calculatedValue))
                        continue;

                    needRecalculate = true;
                    currentValueMap.put(cell.getReferenceCode(), calculatedValue);

                    String stringFormat = df.format(calculatedValue);
                    cell.setValue(stringFormat);
                }
            }

            // fill all remain numeric cells which do not contain value is '0'
            for (SalaryResultStaffItem cell : row.getSalaryResultStaffItems()) {
                if (cell.getValue() != null || cell.getSalaryResultItem().getValueType() == null)
                    continue;
                if (cell.getSalaryResultItem().getValueType().equals(HrConstants.SalaryItemValueType.TEXT.getValue())
                        || cell.getSalaryResultItem().getValueType()
                        .equals(HrConstants.SalaryItemValueType.OTHERS.getValue()))
                    continue;
                cell.setValue("0.00");
            }
        }

    }

    private void setAllowanceFormulaInCell(UUID cellId, SalaryResultItem resultItemOfCell) {
        if (cellId == null || resultItemOfCell == null) {
            return;
        }

        SalaryResultStaffItem salaryResultStaffItem = salaryResultStaffItemRepository.findById(cellId).orElse(null);
        if (salaryResultStaffItem == null) {
            return;
        }

        SalaryResultItem salaryResultItem = salaryResultStaffItem.getSalaryResultItem();
        SalaryPeriod salaryPeriod = (salaryResultItem != null && salaryResultItem.getSalaryResult() != null)
                ? salaryResultItem.getSalaryResult().getSalaryPeriod()
                : null;
        Allowance allowance = (salaryResultItem != null && salaryResultItem.getSalaryItem() != null)
                ? salaryResultItem.getSalaryItem().getAllowance()
                : null;
        Staff staff = (salaryResultStaffItem.getSalaryResultStaff() != null)
                ? salaryResultStaffItem.getSalaryResultStaff().getStaff()
                : null;

        if (allowance == null || staff == null || salaryPeriod == null) {
            return;
        }

        Date fromDateSalaryPeriod = salaryPeriod.getFromDate();
        Date toDateSalaryPeriod = salaryPeriod.getToDate();
        if (fromDateSalaryPeriod == null || toDateSalaryPeriod == null) {
            return;
        }

        List<StaffAllowance> staffAllowances = staffAllowanceRepository.findByStaffIdAndAllowanceId(staff.getId(),
                allowance.getId());
        if (staffAllowances == null || staffAllowances.isEmpty()) {
            return;
        }

        String allowanceFormula = staffAllowances.stream().filter(sa -> sa.getUsingFormula() != null).filter(sa -> {
            Date start = sa.getStartDate();
            Date end = sa.getEndDate();
            if (start == null && end == null) {
                return false;
            }
            // Nếu có cả start và end: salaryPeriod phải nằm hoàn toàn trong khoảng [start,
            // end]
            if (start != null && end != null) {
                return fromDateSalaryPeriod.compareTo(start) >= 0 && toDateSalaryPeriod.compareTo(end) <= 0;
            }
            // Nếu chỉ có start: chỉ kiểm tra salaryPeriod bắt đầu sau start
            if (start != null) {
                return fromDateSalaryPeriod.compareTo(start) >= 0;
            }
            // Nếu chỉ có end: chỉ kiểm tra salaryPeriod kết thúc trước end
            return toDateSalaryPeriod.compareTo(end) <= 0;
        }).map(StaffAllowance::getUsingFormula).findFirst().orElse("");

        resultItemOfCell.setUsingFormula(allowanceFormula);
    }

    public Double calculateFixValueWithEntity(SalaryResultStaffItem cell) {
        try {
            if (cell.getSalaryResultItem() == null || cell.getSalaryResultItem().getSalaryItem() == null
                    || cell.getSalaryResultStaff() == null || cell.getSalaryResultStaff().getStaff() == null)
                return this.convertToDouble(cell.getValue());

            SalaryTemplateItem salaryTemplateItem = cell.getSalaryResultItem().getCopiedTemplateItem();
            Staff staff = cell.getSalaryResultStaff().getStaff();

            List<StaffSalaryItemValue> availableResults = staffSalaryItemValueRepository.findByStaffIdAndSalaryItemId(staff.getId(), salaryTemplateItem.getSalaryItem().getId());

            if (availableResults == null || availableResults.isEmpty()) return this.convertToDouble(cell.getValue());

            StaffSalaryItemValue staffSalaryItemValue = availableResults.get(0);
            return staffSalaryItemValue.getValue();

        } catch (Exception exception) {
            exception.printStackTrace();
            return 0.00;
        }
    }

    public Double calculateThresholdValueWithEntity(SalaryResultStaffItem cell, Map<String, Object> currentValueMap) {
        try {
//            if (cell.getSalaryResultItem() == null || cell.getSalaryResultItem().getSalaryItem() == null)
//                return this.convertToDouble(cell.getValue());
//            SalaryItem salaryItem = cell.getSalaryResultItem().getSalaryItem();
//            if (salaryItem.getThresholds() == null || salaryItem.getThresholds().isEmpty())
//                return this.convertToDouble(cell.getValue());
//
//            List<SalaryItemThreshold> thresholds = new ArrayList<>(salaryItem.getThresholds());
//
//            Collections.sort(thresholds, new Comparator<SalaryItemThreshold>() {
//                @Override
//                public int compare(SalaryItemThreshold o1, SalaryItemThreshold o2) {
//                    if (o1.getDisplayOrder() == null && o2.getDisplayOrder() == null)
//                        return 0;
//                    if (o1.getDisplayOrder() == null)
//                        return 1; // Null values come last
//                    if (o2.getDisplayOrder() == null)
//                        return -1; // Null values come last
//
//                    return o2.getDisplayOrder().compareTo(o1.getDisplayOrder()); // Sort in descending order
//                }
//            });
//
//            // usingFormula in THRESHOLD calculationType is compareValue
//            double compareValue = expressionEvaluatorService
//                    .evaluateExpression(cell.getSalaryResultItem().getUsingFormula(), currentValueMap);
//
//            for (SalaryItemThreshold thresholdItem : thresholds) {
//                double thresholdValue = expressionEvaluatorService.evaluateExpression(thresholdItem.getThresholdValue(),
//                        currentValueMap);
//                if (compareValue >= thresholdValue) {
//                    double valueInUse = expressionEvaluatorService.evaluateExpression(thresholdItem.getInUseValue(),
//                            currentValueMap);
//
//                    return valueInUse;
//                }
//            }
//
//            return this.convertToDouble(cell.getValue());

            return 0.00;

        } catch (Exception exception) {
            exception.printStackTrace();
            return 0.00;
        }
    }

    public double calculatePersonalIncomeTax(Object input) {
        // Kiểm tra và chuyển đổi input thành double
        double taxableIncome = convertToDouble(input);

        // Biểu thuế lũy tiến từng phần
        double[] incomeBrackets = {5000000, 10000000, 18000000, 32000000, 52000000, 80000000}; // Ngưỡng thu nhập
        double[] taxRates = {0.05, 0.10, 0.15, 0.20, 0.25, 0.30, 0.35}; // Thuế suất tương ứng

        double tax = 0.0;

        // Tính thuế dựa trên biểu thuế lũy tiến
        for (int i = incomeBrackets.length; i >= 0; i--) {
            if (taxableIncome > (i > 0 ? incomeBrackets[i - 1] : 0)) {
                double taxableAtThisRate = taxableIncome - (i > 0 ? incomeBrackets[i - 1] : 0);
                tax += taxableAtThisRate * taxRates[i];
                taxableIncome -= taxableAtThisRate;
            }
        }

        // Làm tròn kết quả thuế đến 2 chữ số thập phân
        return roundToTwoDecimalPlaces(tax);
    }

    private double convertToDouble(Object input) {
        double result;
        if (input == null)
            return 0.00;

        if (input instanceof Number) {
            result = ((Number) input).doubleValue();
        } else if (input instanceof String) {
            try {
                result = Double.parseDouble((String) input);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert input to double: " + input);
            }
        } else {
            throw new IllegalArgumentException("Unsupported input type: " + input.getClass().getName());
        }

        // Làm tròn kết quả đầu vào đến 2 chữ số thập phân
        return roundToTwoDecimalPlaces(result);
    }

    private double roundToTwoDecimalPlaces(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private boolean areObjectsEqualAsDoubles(Object obj1, Object obj2) {
        // Validate input: Ensure objects are not null
        if (obj1 == null || obj2 == null) {
            System.out.println("One or both inputs are null.");
            return false;
        }

        double val1, val2;

        try {
            // Convert obj1 to double
            if (obj1 instanceof String) {
                val1 = Double.parseDouble((String) obj1);
            } else if (obj1 instanceof Double) {
                val1 = (Double) obj1;
            } else {
                System.out.println("obj1 is not a valid type (must be String or Double).");
                return false;
            }

            // Convert obj2 to double
            if (obj2 instanceof String) {
                val2 = Double.parseDouble((String) obj2);
            } else if (obj2 instanceof Double) {
                val2 = (Double) obj2;
            } else {
                System.out.println("obj2 is not a valid type (must be String or Double).");
                return false;
            }

            // Use exact comparison for doubles
            return Double.compare(val1, val2) == 0;

        } catch (NumberFormatException e) {
            // Handle invalid string input
            System.out.println("Invalid input: " + e.getMessage());
            return false;
        }
    }


    @Override
    public void importItemValueForPayslips(List<ImportSalaryResultStaffDto> staffAndImportValues, SearchSalaryResultStaffDto searchDto) {
        if (staffAndImportValues == null || staffAndImportValues.isEmpty()) {
            logger.warn("Danh sách staffAndImportValues null hoặc rỗng");
            return;
        }

        SalaryResult salaryResult = null;
        if (searchDto.getSalaryResultId() != null) {
            salaryResult = salaryResultRepository.findById(searchDto.getSalaryResultId()).orElse(null);
        }
        // Không tìm thấy Bảng lương tổng hợp
        if (salaryResult == null) {
            logger.warn("Bảng lương không xác định");
            return;
        }


        SalaryTemplate salaryTemplate = null;
        if (salaryResult.getSalaryTemplate() != null && salaryResult.getSalaryTemplate().getId() != null) {
            salaryTemplate = salaryTemplateRepository.findById(salaryResult.getSalaryTemplate().getId()).orElse(null);
        } else {
            logger.warn("Mẫu bảng lương không xác định");
            return;
        }

        if (salaryTemplate == null || (salaryTemplate.getVoided() != null && salaryTemplate.getVoided())) {
            logger.warn("Mẫu bảng lương đã bị hủy");
            return;
        }


        for (ImportSalaryResultStaffDto importStaff : staffAndImportValues) {
            // looping on staff

            // check staff
            if (importStaff.getSalaryItemValues() == null || importStaff.getSalaryItemValues().isEmpty()) {
                logger.warn("Thành phần lương của nhân viên mã " + importStaff.getStaffCode() + "không có giá trị nào");
                continue;
            }

            for (ImportSalaryStaffItemValueDto importItem : importStaff.getSalaryItemValues()) {
                SalaryResultStaffItemImportDto cellItem = new SalaryResultStaffItemImportDto();

                cellItem.setStaffCode(importStaff.getStaffCode());
                cellItem.setSalaryItemValue(importItem.getSalaryItemValue());
                cellItem.setSalaryItemCode(importItem.getSalaryItemCode());
                cellItem.setSalaryPeriodCode(importStaff.getSalaryPeriodCode());
                cellItem.setRecalculateStaffPayslipAfterProcess(false);

                // Nhập dữ liệu trong phiếu lương của nhân viên
                salaryResultStaffItemService.importSalaryResultStaffItemValue(cellItem);
            }

            // Tính lại phiếu lương sau khi import dữ liệu
            try {
                recalculatePayslipSafely(importStaff.getStaffCode(), importStaff.getSalaryPeriodCode(), salaryTemplate.getCode());
            } catch (Exception e) {
                logger.warn("Lỗi tính toán: " + e.getMessage());
                throw e;
            }
        }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recalculatePayslipSafely(String staffCode, String periodCode, String templateCode) {
        SalaryResultStaff payslip = this.getUniquePayslipFromStaffCodePeriodCodeTemplateCode(staffCode, periodCode, templateCode);
        if (payslip == null) return;
        SalaryResultStaffDto recalculatedPayslip = salaryResultStaffService.recalculateSalaryStaff(new SalaryResultStaffDto(payslip, true));
    }

    private SalaryResultStaff getUniquePayslipFromStaffCodePeriodCodeTemplateCode(String staffCode, String salaryPeriodCode, String salaryTemplateCode) {
        if (staffCode == null || !StringUtils.hasText(staffCode) || salaryPeriodCode == null || !StringUtils.hasText(salaryPeriodCode) || salaryTemplateCode == null || !StringUtils.hasText(salaryTemplateCode)) {
            return null;
        }

        staffCode = staffCode.trim();
        salaryPeriodCode = salaryPeriodCode.trim();
        salaryTemplateCode = salaryTemplateCode.trim();

        List<SalaryResultStaff> payslips = salaryResultStaffRepository.findByStaffCodePeriodCodeAndTemplateCode(staffCode, salaryPeriodCode, salaryTemplateCode);

        if (payslips == null || payslips.isEmpty()) return null;

        return payslips.get(0);
    }

    // Số lượng phiếu lương có thể thuộc bảng lương nhưng chưa được tổng hợp
    @Override
    public Integer hasAnyOrphanedPayslips(UUID salaryResultId) {
        if (salaryResultId == null) return 0;

        SalaryResult salaryResult = salaryResultRepository.findById(salaryResultId).orElse(null);
        if (salaryResult == null) return 0;

        List<SalaryResultStaff> orphanedPayslips = salaryResultStaffRepository.findOrphanedPayslipsByPeriodIdAndTemplateId(salaryResult.getSalaryPeriod().getId(), salaryResult.getSalaryTemplate().getId());
        if (orphanedPayslips == null || orphanedPayslips.isEmpty()) return 0;

        return orphanedPayslips.size();
    }

    // Lấy danh sách phiếu lương có thể tổng hợp vào bảng lương
    @Override
    public List<SalaryResultStaffDto> getAllOrphanedPayslips(UUID salaryResultId) {
        List<SalaryResultStaffDto> response = new ArrayList<>();

        if (salaryResultId == null) return response;

        SalaryResult salaryResult = salaryResultRepository.findById(salaryResultId).orElse(null);
        if (salaryResult == null) return response;

        List<SalaryResultStaff> orphanedPayslips = salaryResultStaffRepository.findOrphanedPayslipsByPeriodIdAndTemplateId(salaryResult.getSalaryPeriod().getId(), salaryResult.getSalaryTemplate().getId());
        if (orphanedPayslips == null || orphanedPayslips.isEmpty()) return response;

        for (SalaryResultStaff salaryResultStaff : orphanedPayslips) {
            SalaryResultStaffDto payslip = new SalaryResultStaffDto(salaryResultStaff, true);

            response.add(payslip);
        }

        return response;
    }


    // Tổng hợp các phiếu lương được chọn vào bảng lương
    @Override
    public Boolean mergeOrphanedPayslips(SearchSalaryResultStaffDto dto) {
        if (dto == null || dto.getSalaryResultId() == null || dto.getChosenPayslipIds() == null) return false;

        if (dto.getChosenPayslipIds().isEmpty()) return true;

        SalaryResult salaryResult = salaryResultRepository.findById(dto.getSalaryResultId()).orElse(null);
        if (salaryResult == null) return false;

        if (salaryResult.getSalaryResultStaffs() == null) {
            salaryResult.setSalaryResultStaffs(new HashSet<>());
        }

        for (UUID payslipId : dto.getChosenPayslipIds()) {
            if (payslipId == null) continue;

            SalaryResultStaff payslip = salaryResultStaffRepository.findById(payslipId).orElse(null);
            if (payslip == null) continue;

            payslip.setSalaryResult(salaryResult);

            salaryResult.getSalaryResultStaffs().add(payslip);
        }

        salaryResult = salaryResultRepository.saveAndFlush(salaryResult);

        return true;
    }


    // CRUD
    @Override
    @Modifying
    @Transactional
    public SalaryResultStaffPaySlipDto saveOrUpdate(SalaryResultStaffPaySlipDto dto) {
        if (dto == null) {
            return null;
        }

        SalaryResultStaff entity = null;
        if (dto.getId() != null) {
            entity = salaryResultStaffRepository.findById(dto.getId()).orElse(null);
        }

        if (entity == null) return null;

        entity.setNote(dto.getNote());
//        entity.setApprovalStatus(dto.getApprovalStatus());
        entity.setPaidStatus(dto.getPaidStatus());

        entity = salaryResultStaffRepository.saveAndFlush(entity);

        // toggle generate/remove record social insurance of staff when updating payslip's approval status
//        this.handleSocialInsuranceByChangingStatus(entity, dto.getApprovalStatus());

        SalaryResultStaffPaySlipDto response = new SalaryResultStaffPaySlipDto(entity, true);

        return response;
    }

    @Override
    public SalaryResultStaffPaySlipDto getById(UUID id) {
        SalaryResultStaff entity = salaryResultStaffRepository.findById(id).orElse(null);
        if (entity == null) return null;

        return new SalaryResultStaffPaySlipDto(entity, true);
    }

    @Override
    public SearchSalaryResultStaffDto getInitialFilter() {
        SearchSalaryResultStaffDto response = new SearchSalaryResultStaffDto();

        response.setPaidStatus(0);
        response.setPaidStatus(0);
        response.setPageIndex(1);
        response.setPageSize(10);

        List<SalaryPeriodDto> activePeriods = salaryPeriodService.getActivePeriodsByDate(new Date());
        if (activePeriods != null && !activePeriods.isEmpty()) {
            SalaryPeriodDto period = activePeriods.get(0);
            response.setSalaryPeriod(period);
            response.setSalaryPeriodId(period.getId());
//            response.setFromDate(period.getFromDate());
//            response.setToDate(period.getToDate());
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
    public Page<SalaryResultStaffPaySlipDto> pagingSalaryPayslip(SearchSalaryResultStaffDto searchDto) {
        if (searchDto == null) {
            return null;
        }

        int pageIndex = searchDto.getPageIndex();
        int pageSize = searchDto.getPageSize();

        if (pageIndex > 0) {
            pageIndex--;
        } else {
            pageIndex = 0;
        }

        if (searchDto.getStaff() != null) {
            searchDto.setStaffId(searchDto.getStaff().getId());
        }

        boolean isRoleManager = false;
        UserDto user = userExtService.getCurrentUser();
        if (user != null && user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (RoleDto item : user.getRoles()) {
                if (item.getName() != null && ("ROLE_ADMIN".equals(item.getName()) || "HR_MANAGER".equals(item.getName()))) {
                    isRoleManager = true;
                }
            }
        }
        if (!isRoleManager) {
            StaffDto currentStaff = userExtService.getCurrentStaff();
            searchDto.setStaffId(currentStaff.getId());
        }

        String whereClause = " where (1=1) ";
        if (searchDto.getIsPayslip() != null && searchDto.getIsPayslip().equals(true)) {
            whereClause += " and entity.salaryTemplate.isCreatePayslip = true ";
        }

        String orderBy = " ORDER BY entity.modifyDate desc ";

        String sqlCount = "select count(distinct entity.id) from SalaryResultStaff as entity ";
        String sql = "select distinct new com.globits.salary.dto.SalaryResultStaffPaySlipDto(entity) from SalaryResultStaff as entity ";

        String joinPositionStaff = "";
        boolean hasJoinMainPosition = false;
        if (searchDto.getPositionTitleId() != null || searchDto.getDepartmentId() != null || searchDto.getOrganizationId() != null) {
            joinPositionStaff = " JOIN Position pos ON pos.isMain = true AND pos.staff.id = entity.staff.id ";
            hasJoinMainPosition = true;
        }

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            whereClause += " AND (entity.staff.staffCode LIKE :text OR entity.staff.displayName like :text) ";
        }
        if (searchDto.getSalaryResult() != null || searchDto.getSalaryResultId() != null) {
            whereClause += " and (entity.salaryResult.id = :salaryResultId) ";
        }
        if (searchDto.getSalaryPeriod() != null || searchDto.getSalaryPeriodId() != null) {
            whereClause += " and (entity.salaryPeriod.id = :salaryPeriodId) ";
        }
        if (searchDto.getSalaryTemplateId() != null) {
            whereClause += " and (entity.salaryTemplate.id = :salaryTemplateId) ";
        }
        if (searchDto.getStaffId() != null) {
            whereClause += " and (entity.staff.id = :staffId) ";
        }
        if (searchDto.getApprovalStatus() != null) {
            whereClause += " and (entity.approvalStatus = :approvalStatus) ";
        }
        if (searchDto.getPaidStatus() != null) {
            whereClause += " and (entity.paidStatus = :paidStatus) ";
        }


        if (hasJoinMainPosition) {
            if (searchDto.getOrganizationId() != null && StringUtils.hasText(searchDto.getOrganizationId().toString())) {
                whereClause += " AND ( pos.department.organization.id  =: organizationId ) ";
            }
            if (searchDto.getDepartmentId() != null && StringUtils.hasText(searchDto.getDepartmentId().toString())) {
                whereClause += " AND ( pos.department.id  =: departmentId ) ";
            }
            if (searchDto.getPositionTitleId() != null && StringUtils.hasText(searchDto.getPositionTitleId().toString())) {
                whereClause += " AND ( pos.title.id  =: positionTitleId ) ";
            }
        }

        sql += joinPositionStaff + whereClause + orderBy;
        sqlCount += joinPositionStaff + whereClause;

        jakarta.persistence.Query query = manager.createQuery(sql, SalaryResultStaffPaySlipDto.class);
        Query qCount = manager.createQuery(sqlCount);

        if (searchDto.getKeyword() != null && StringUtils.hasText(searchDto.getKeyword())) {
            query.setParameter("text", '%' + searchDto.getKeyword() + '%');
            qCount.setParameter("text", '%' + searchDto.getKeyword() + '%');
        }
        if (searchDto.getSalaryResult() != null || searchDto.getSalaryResultId() != null) {
            UUID salaryResultId = null;
            if (searchDto.getSalaryResult() != null) {
                salaryResultId = searchDto.getSalaryResult().getId();
            } else {
                salaryResultId = searchDto.getSalaryResultId();
            }

            query.setParameter("salaryResultId", salaryResultId);
            qCount.setParameter("salaryResultId", salaryResultId);
        }

        if (searchDto.getSalaryPeriod() != null || searchDto.getSalaryPeriodId() != null) {
            UUID salaryPeriodId = null;
            if (searchDto.getSalaryResult() != null) {
                salaryPeriodId = searchDto.getSalaryPeriod().getId();
            } else {
                salaryPeriodId = searchDto.getSalaryPeriodId();
            }

            query.setParameter("salaryPeriodId", salaryPeriodId);
            qCount.setParameter("salaryPeriodId", salaryPeriodId);
        }

        if (searchDto.getSalaryTemplateId() != null) {
            query.setParameter("salaryTemplateId", searchDto.getSalaryTemplateId());
            qCount.setParameter("salaryTemplateId", searchDto.getSalaryTemplateId());
        }
        if (searchDto.getStaffId() != null) {
            query.setParameter("staffId", searchDto.getStaffId());
            qCount.setParameter("staffId", searchDto.getStaffId());
        }
        if (searchDto.getApprovalStatus() != null) {
            query.setParameter("approvalStatus", searchDto.getApprovalStatus());
            qCount.setParameter("approvalStatus", searchDto.getApprovalStatus());
        }
        if (searchDto.getPaidStatus() != null) {
            query.setParameter("paidStatus", searchDto.getPaidStatus());
            qCount.setParameter("paidStatus", searchDto.getPaidStatus());
        }

        if (hasJoinMainPosition) {
            if (searchDto.getOrganizationId() != null && StringUtils.hasText(searchDto.getOrganizationId().toString())) {
                query.setParameter("organizationId", searchDto.getOrganizationId());
                qCount.setParameter("organizationId", searchDto.getOrganizationId());
            }
            if (searchDto.getDepartmentId() != null && StringUtils.hasText(searchDto.getDepartmentId().toString())) {
                query.setParameter("departmentId", searchDto.getDepartmentId());
                qCount.setParameter("departmentId", searchDto.getDepartmentId());
            }
            if (searchDto.getPositionTitleId() != null && StringUtils.hasText(searchDto.getPositionTitleId().toString())) {
                query.setParameter("positionTitleId", searchDto.getPositionTitleId());
                qCount.setParameter("positionTitleId", searchDto.getPositionTitleId());
            }
        }


        long count = (long) qCount.getSingleResult();
        int startPosition = pageIndex * pageSize;

        query.setFirstResult(startPosition);
        query.setMaxResults(pageSize);

        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        List<SalaryResultStaffPaySlipDto> entities = query.getResultList();
        Page<SalaryResultStaffPaySlipDto> result = new PageImpl<>(entities, pageable, count);

        return result;
    }


    @Override
    public SalaryResultStaffDto renewPayslip(UUID payslipId) {
        SalaryResultStaff entity = salaryResultStaffRepository.findById(payslipId).orElse(null);

        if (entity == null) return null;

        SalaryResultStaffDto payslip = new SalaryResultStaffDto(entity, true);

        salaryResultStaffService.deleteSalaryResultStaff(payslipId);

//        salaryResultStaffRepository.flush();
        entityManager.flush();

        SalaryResultStaffDto response = salaryResultStaffService.calculateSalaryStaff(payslip);

        return response;
    }
}


