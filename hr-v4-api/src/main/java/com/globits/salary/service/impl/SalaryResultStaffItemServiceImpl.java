package com.globits.salary.service.impl;

import com.globits.core.service.impl.GenericServiceImpl;
import com.globits.hr.HrConstants;
import com.globits.hr.domain.PositionStaff;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffFamilyRelationship;
import com.globits.hr.domain.StaffLabourAgreement;
import com.globits.hr.dto.StaffDto;
import com.globits.hr.repository.StaffFamilyRelationshipRepository;
import com.globits.hr.repository.StaffLabourAgreementRepository;
import com.globits.hr.repository.StaffRepository;
import com.globits.hr.repository.StaffWorkScheduleRepository;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.hr.utils.ExpressionUtil;
import com.globits.salary.domain.*;
import com.globits.salary.dto.SalaryPeriodDto;
import com.globits.salary.dto.SalaryResultStaffDto;
import com.globits.salary.dto.SalaryResultStaffItemDto;
import com.globits.salary.dto.SalaryTemplateDto;
import com.globits.salary.dto.excel.ImportSalaryResultStaffDto;
import com.globits.salary.dto.excel.ImportSalaryStaffItemValueDto;
import com.globits.salary.dto.excel.SalaryResultStaffItemImportDto;
import com.globits.salary.dto.search.CalculateSalaryRequest;
import com.globits.salary.repository.*;
import com.globits.salary.service.*;
import com.globits.timesheet.domain.TimeSheetDetail;
import com.globits.timesheet.domain.TimeSheetShiftWorkPeriod;
import com.globits.timesheet.repository.TimeSheetDetailRepository;
import com.globits.timesheet.repository.TimeSheetShiftWorkPeriodRepository;
import com.globits.timesheet.service.TimeSheetShiftWorkPeriodService;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.DecimalFormat;
import java.util.*;

@Service
public class SalaryResultStaffItemServiceImpl extends GenericServiceImpl<SalaryResultStaffItem, UUID>
        implements SalaryResultStaffItemService {
    private static final Logger logger = LoggerFactory.getLogger(SalaryResultStaffItemServiceImpl.class);

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
    private StaffLabourAgreementRepository staffLabourAgreementRepository;

    @Autowired
    private TimeSheetShiftWorkPeriodRepository timeSheetShiftWorkPeriodRepository;

    @Autowired
    private StaffAdvancePaymentRepository staffAdvancePaymentRepository;

    @Autowired
    private StaffFamilyRelationshipRepository staffFamilyRelationshipRepository;

    @Autowired
    private SalaryResultItemRepository salaryResultItemRepository;

    @Autowired
    private TimeSheetShiftWorkPeriodService timeSheetShiftWorkPeriodService;

    @Autowired
    private SalaryItemRepository salaryItemRepository;

    @Autowired
    private SalaryResultStaffService salaryResultStaffService;

    @Autowired
    private StaffWorkScheduleRepository staffWorkScheduleRepository;

    @Autowired
    private SalaryAutoMapService salaryAutoMapService;

    @Autowired
    private StaffSalaryTemplateService salaryTemplateService;

    @Autowired
    private SalaryAutoCalculationService salaryAutoCalculationService;

    @Autowired
    private EntityManager entityManager;


    @Override
    public void generateResultStaffItems(SalaryResultStaff row, SalaryResult entity) {
        if (row == null)
            return;
        if (row.getSalaryResultStaffItems() == null) {
            row.setSalaryResultStaffItems(new HashSet<>());
        }

        if (entity == null)
            return;
        List<SalaryResultItem> resultItems = salaryResultItemRepository.getAllResultItemsOfSalaryResult(entity.getId());
        if (resultItems == null || resultItems.isEmpty())
            return;

        Set<SalaryResultStaffItem> rowCells = new HashSet<>();
        for (SalaryResultItem item : resultItems) {
            SalaryResultStaffItem cell = null;

            List<SalaryResultStaffItem> allValidCells = salaryResultStaffItemRepository
                    .findBySalaryResultStaffIdAndSalaryResultItemId(row.getId(), item.getId());
            if (allValidCells != null && !allValidCells.isEmpty()) {
                cell = allValidCells.get(0);
            }
            if (cell == null) {
                cell = new SalaryResultStaffItem();

                cell.setSalaryResultItem(item);
                cell.setSalaryResultStaff(row);
            }

            cell.setReferenceName(item.getDisplayName());
            cell.setReferenceCode(item.getSalaryItem().getCode());
            cell.setReferenceDisplayOrder(item.getDisplayOrder());

            cell = salaryResultStaffItemRepository.save(cell);

            // generate cell value
            this.detectAndAutoGenerateValue(cell);

            rowCells.add(cell);
        }

        row.getSalaryResultStaffItems().clear();
        row.getSalaryResultStaffItems().addAll(rowCells);
    }

    @Override
    @Modifying
    @Transactional
    public void detectAndAutoGenerateValue(SalaryResultStaffItem cell) {
        if (cell == null || cell.getId() == null)
            return;
//        if (cell.getValue() != null &&
//                (
//                        !cell.getValue().equals("0") || !cell.getValue().equals("0.00") || !Objects.equals(cell.getValue(), "0.0")
//                )
//        ) {
//            return;
//        }

        SalaryResultItem usingItem = null;
        if (cell.getSalaryResultItem() != null && cell.getSalaryResultItem().getSalaryItem() != null) {
            usingItem = cell.getSalaryResultItem();
        }

        if (usingItem == null || usingItem.getCode() == null)
            return;

        // Assuming this is the dynamic code that will be checked
        String code = cell.getSalaryResultItem().getCode();

        if (HrConstants.SalaryItemCodeSystemDefault.STT_SYSTEM.getValue().equals(code)) {
            handleGenerateSTT(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.HO_VA_TEN_NV_SYSTEM.getValue().equals(code)) {
            handleGenerateHoVaTenNV(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.HO_NV_SYSTEM.getValue().equals(code)) {
            handleGenerateHoNV(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.TEN_NV_SYSTEM.getValue().equals(code)) {
            handleGenerateTenNV(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.CHUC_VU_NV_SYSTEM.getValue().equals(code)) {
            handleGenerateChucVuNV(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.PHONG_BAN_NV_SYSTEM.getValue().equals(code)) {
            handleGeneratePhongBanNV(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.DON_VI_NV_SYSTEM.getValue().equals(code)) {
            handleGenerateDonViNV(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.MA_NV_SYSTEM.getValue().equals(code)) {
            handleGenerateMaNV(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.EMAIL_NV_SYSTEM.getValue().equals(code)) {
            handleGenerateEmailNV(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.SDT_NV_SYSTEM.getValue().equals(code)) {
            handleGenerateSDTNV(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.GIOI_TINH_NV_SYSTEM.getValue().equals(code)) {
            handleGenerateGioiTinhNV(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.QUOC_TICH_NV_SYSTEM.getValue().equals(code)) {
            handleGenerateQuocTichNV(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.NGUYEN_QUAN_NV_SYSTEM.getValue().equals(code)) {
            handleGenerateNguyenQuanNV(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.LUONG_CO_BAN_SYSTEM.getValue().equals(code)) {
            handleGenerateLuongCoBan(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.LUONG_DONG_BHXH_SYSTEM.getValue().equals(code)) {
            handleGenerateLuongDongBHXH(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.SO_NGAY_CONG_SYSTEM.getValue().equals(code)) {
            handleGenerateSoNgayCong(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.SO_GIO_CONG_SYSTEM.getValue().equals(code)) {
            handleGenerateSoGioCong(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.SO_GIO_CONG_OT_SYSTEM.getValue().equals(code)) {
            handleGenerateSoGioCongOT(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.TAM_UNG_SYSTEM.getValue().equals(code)) {
            handleGenerateTamUng(cell);
        } else if (HrConstants.SalaryItemCodeSystemDefault.SO_NGUOI_PHU_THUOC_SYSTEM.getValue().equals(code)) {
            handleGenerateSoNguoiPhuThuoc(cell);
        }
        // 20-11-2024 - default set value of the cell with default value
        else if (cell.getSalaryResultItem().getDefaultValue() != null && !cell.getSalaryResultItem().getDefaultValue().isEmpty()) {
            if (cell.getSalaryResultItem().getValueType().equals(HrConstants.SalaryItemValueType.TEXT.getValue())
                    || cell.getSalaryResultItem().getValueType().equals(HrConstants.SalaryItemValueType.OTHERS.getValue())) {
                DecimalFormat df = new DecimalFormat("0.##"); // Keeps all significant digits
                String stringFormat = df.format(cell.getValue());
                cell.setValue(stringFormat);
            } else {
                cell.setValue(cell.getSalaryResultItem().getDefaultValue());
            }
        } else {
            //	System.out.println("Unknown salary item code: " + code);
        }

    }

    private void handleGenerateSoNguoiPhuThuoc(SalaryResultStaffItem cell) {
        Staff staff = null;
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null) {
            staff = cell.getSalaryResultStaff().getStaff();
        }

        if (staff == null) {
            cell.setValue("0.00");
            return;
        }

        List<StaffFamilyRelationship> availableResults = staffFamilyRelationshipRepository.findDependentRelationshipOfStaff(staff.getId());
        if (availableResults == null || availableResults.isEmpty()) {
            cell.setValue("0.00");
            return;
        }

        DecimalFormat df = new DecimalFormat("0.##"); // Keeps all significant digits
        String stringFormat = df.format(availableResults.size());
        cell.setValue(stringFormat);
    }


    private void handleGenerateTamUng(SalaryResultStaffItem cell) {
        Staff staff = null;
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null) {
            staff = cell.getSalaryResultStaff().getStaff();
        }

        if (staff == null) {
            cell.setValue("0.00");
            return;
        }

        SalaryPeriod salaryPeriod = null;
        if (cell.getSalaryResultItem() != null && cell.getSalaryResultItem().getSalaryResult() != null
                && cell.getSalaryResultItem().getSalaryResult().getSalaryPeriod() != null) {
            salaryPeriod = cell.getSalaryResultItem().getSalaryResult().getSalaryPeriod();
        }

        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null) {
            cell.setValue("0.00");
            return;
        }

        List<StaffAdvancePayment> availableResults = staffAdvancePaymentRepository.findByStaffIdAndSalaryPeriodIdAndApprovalStatus(staff.getId(), salaryPeriod.getId(), HrConstants.StaffAdvancePaymentApprovalStatus.APPROVED.getValue());
        if (availableResults == null || availableResults.isEmpty()) {
            cell.setValue("0.00");
            return;
        }

        Double totalAdvanceAmount = 0.0;
        for (StaffAdvancePayment advancePayment : availableResults) {
            totalAdvanceAmount += advancePayment.getAdvancedAmount();
        }

        DecimalFormat df = new DecimalFormat("0.##"); // Keeps all significant digits
        String stringFormat = df.format(totalAdvanceAmount);
        cell.setValue(stringFormat);
    }

    private void handleGenerateSoGioCong(SalaryResultStaffItem cell) {
        Staff staff = null;
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null) {
            staff = cell.getSalaryResultStaff().getStaff();
        }

        if (staff == null) {
            cell.setValue("0.00");
            return;
        }

        SalaryPeriod salaryPeriod = null;
        if (cell.getSalaryResultItem() != null && cell.getSalaryResultItem().getSalaryResult() != null
                && cell.getSalaryResultItem().getSalaryResult().getSalaryPeriod() != null) {
            salaryPeriod = cell.getSalaryResultItem().getSalaryResult().getSalaryPeriod();
        }

        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null) {
            cell.setValue("0.00");
            return;
        }

        Date fromDate = DateTimeUtil.getStartOfDay(salaryPeriod.getFromDate());
        Date toDate = DateTimeUtil.getEndOfDay(salaryPeriod.getToDate());
        List<TimeSheetShiftWorkPeriod> tsdsOfStaff = timeSheetShiftWorkPeriodRepository.findTimeSheetShiftWorkPeriodInRangeTimeOfStaffNOTByShiftWorkTimePeriod(fromDate,
                toDate, staff.getId(), HrConstants.ShiftWorkTimePeriodEnum.EVENING_SHIFT.getCodeValue());

        double totalWorkingHours = timeSheetShiftWorkPeriodService.calculateTotalWorkingTime(tsdsOfStaff);

        DecimalFormat df = new DecimalFormat("0.##"); // Keeps all significant digits
        String stringFormat = df.format(totalWorkingHours);

        cell.setValue(stringFormat);
    }

    private void handleGenerateSoGioCongOT(SalaryResultStaffItem cell) {
        Staff staff = null;
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null) {
            staff = cell.getSalaryResultStaff().getStaff();
        }

        if (staff == null) {
            cell.setValue("0.00");
            return;
        }

        SalaryPeriod salaryPeriod = null;
        if (cell.getSalaryResultItem() != null && cell.getSalaryResultItem().getSalaryResult() != null
                && cell.getSalaryResultItem().getSalaryResult().getSalaryPeriod() != null) {
            salaryPeriod = cell.getSalaryResultItem().getSalaryResult().getSalaryPeriod();
        }

        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null) {
            cell.setValue("0.00");
            return;
        }

        Date fromDate = DateTimeUtil.getStartOfDay(salaryPeriod.getFromDate());
        Date toDate = DateTimeUtil.getEndOfDay(salaryPeriod.getToDate());
        List<TimeSheetShiftWorkPeriod> tsdsOfStaff = timeSheetShiftWorkPeriodRepository.findTimeSheetShiftWorkPeriodInRangeTimeOfStaffByShiftWorkTimePeriod(fromDate,
                toDate, staff.getId(), HrConstants.ShiftWorkTimePeriodEnum.EVENING_SHIFT.getCodeValue());

        double totalWorkingHours = timeSheetShiftWorkPeriodService.calculateTotalWorkingTime(tsdsOfStaff);

        DecimalFormat df = new DecimalFormat("0.##"); // Keeps all significant digits
        String stringFormat = df.format(totalWorkingHours);

        cell.setValue(stringFormat);
    }

    private void handleGenerateSoNgayCong(SalaryResultStaffItem cell) {
        Staff staff = null;
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null) {
            staff = cell.getSalaryResultStaff().getStaff();
        }

        if (staff == null) {
            cell.setValue("0.00");
            return;
        }

        SalaryPeriod salaryPeriod = null;
        if (cell.getSalaryResultItem() != null && cell.getSalaryResultItem().getSalaryResult() != null
                && cell.getSalaryResultItem().getSalaryResult().getSalaryPeriod() != null) {
            salaryPeriod = cell.getSalaryResultItem().getSalaryResult().getSalaryPeriod();
        }

        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null) {
            cell.setValue("0.00");
            return;
        }

        Date fromDate = DateTimeUtil.getStartOfDay(salaryPeriod.getFromDate());
        Date toDate = DateTimeUtil.getEndOfDay(salaryPeriod.getToDate());
        List<TimeSheetShiftWorkPeriod> tsdsOfStaff = timeSheetShiftWorkPeriodRepository.findTimeSheetShiftWorkPeriodInRangeTimeOfStaffNOTByShiftWorkTimePeriod(fromDate,
                toDate, staff.getId(), HrConstants.ShiftWorkTimePeriodEnum.EVENING_SHIFT.getCodeValue());

        Double countWorkingDay = 0.0;
        if (tsdsOfStaff != null && !tsdsOfStaff.isEmpty()) {
            countWorkingDay = tsdsOfStaff.size() * 0.5;
        }

        cell.setValue(countWorkingDay.toString());
    }


    private void handleGenerateLuongDongBHXH(SalaryResultStaffItem cell) {
        Staff staff = null;
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null) {
            staff = cell.getSalaryResultStaff().getStaff();
        }

        if (staff == null) {
            cell.setValue("0.00");
            return;
        }

        SalaryPeriod salaryPeriod = null;
        if (cell.getSalaryResultItem() != null && cell.getSalaryResultItem().getSalaryResult() != null
                && cell.getSalaryResultItem().getSalaryResult().getSalaryPeriod() != null) {
            salaryPeriod = cell.getSalaryResultItem().getSalaryResult().getSalaryPeriod();
        }

        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null) {
            cell.setValue("0.00");
            return;
        }

        StaffLabourAgreement labourAgreement = null;
        List<StaffLabourAgreement> currentActiveLabourAgreements = staffLabourAgreementRepository
                .getLabourAgreementOfStaffCoverRangeTime(staff.getId(), salaryPeriod.getFromDate(),
                        salaryPeriod.getToDate());
        if (currentActiveLabourAgreements == null || currentActiveLabourAgreements.isEmpty()) {
            cell.setValue("0.00");
            return;
        }

        labourAgreement = currentActiveLabourAgreements.get(0);

        if (labourAgreement == null || labourAgreement.getHasSocialIns() == null || labourAgreement.getHasSocialIns().equals(false)) {
            cell.setValue("0.00");
            return;
        }

        if (labourAgreement.getInsuranceSalary() != null) {
            DecimalFormat df = new DecimalFormat("0.##"); // Keeps all significant digits
            String stringFormat = df.format(labourAgreement.getInsuranceSalary());
            cell.setValue(stringFormat);
            return;
        }

        cell.setValue("0.00");
    }

    private void handleGenerateLuongCoBan(SalaryResultStaffItem cell) {
        Staff staff = null;
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null) {
            staff = cell.getSalaryResultStaff().getStaff();
        }

        if (staff == null) {
            cell.setValue("0.00");
            return;
        }

        SalaryPeriod salaryPeriod = null;
        if (cell.getSalaryResultItem() != null && cell.getSalaryResultItem().getSalaryResult() != null
                && cell.getSalaryResultItem().getSalaryResult().getSalaryPeriod() != null) {
            salaryPeriod = cell.getSalaryResultItem().getSalaryResult().getSalaryPeriod();
        }

        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null) {
            cell.setValue("0.00");
            return;
        }

        StaffLabourAgreement labourAgreement = null;
        List<StaffLabourAgreement> currentActiveLabourAgreements = staffLabourAgreementRepository
                .getLabourAgreementOfStaffCoverRangeTime(staff.getId(), salaryPeriod.getFromDate(),
                        salaryPeriod.getToDate());
        if (currentActiveLabourAgreements == null || currentActiveLabourAgreements.isEmpty()) {
            cell.setValue("0.00");
            return;
        }

        labourAgreement = currentActiveLabourAgreements.get(0);

        if (labourAgreement.getSalary() != null) {
            DecimalFormat df = new DecimalFormat("0.##"); // Keeps all significant digits
            String stringFormat = df.format(labourAgreement.getSalary());
            cell.setValue(stringFormat);
            return;
        }

        cell.setValue("0.00");
    }

    // Example of specific handler implementations
    private void handleGenerateSTT(SalaryResultStaffItem cell) {
        // Generate STT logic here
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getDisplayOrder() != null) {
            Integer order = cell.getSalaryResultStaff().getDisplayOrder();
            order++;
            cell.setValue(order.toString());
        }
    }

    private void handleGenerateHoVaTenNV(SalaryResultStaffItem cell) {
        // Generate full name logic here
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null
                && cell.getSalaryResultStaff().getStaff().getDisplayName() != null) {
            cell.setValue(cell.getSalaryResultStaff().getStaff().getDisplayName());
        }
    }

    private void handleGenerateHoNV(SalaryResultStaffItem cell) {
        // Generate last name logic here
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null
                && cell.getSalaryResultStaff().getStaff().getLastName() != null) {
            cell.setValue(cell.getSalaryResultStaff().getStaff().getLastName());
        }

    }

    private void handleGenerateTenNV(SalaryResultStaffItem cell) {
        // Generate first name logic here
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null
                && cell.getSalaryResultStaff().getStaff().getFirstName() != null) {
            cell.setValue(cell.getSalaryResultStaff().getStaff().getFirstName());
        }
    }

    private void handleGenerateChucVuNV(SalaryResultStaffItem cell) {
        // Generate position logic here
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null && cell.getSalaryResultStaff().getStaff().getCurrentPosition() != null) {
            cell.setValue(cell.getSalaryResultStaff().getStaff().getCurrentPosition().getName());
        } else {
            cell.setValue("");
        }
    }

    private void handleGeneratePhongBanNV(SalaryResultStaffItem cell) {
        // Generate department logic here
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null
                && cell.getSalaryResultStaff().getStaff().getDepartment() != null) {
            cell.setValue(cell.getSalaryResultStaff().getStaff().getDepartment().getName());
        }
    }

    private void handleGenerateDonViNV(SalaryResultStaffItem cell) {
        // Generate unit logic here
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null
                && cell.getSalaryResultStaff().getStaff().getUser() != null
                && cell.getSalaryResultStaff().getStaff().getUser().getOrg() != null) {
            cell.setValue(cell.getSalaryResultStaff().getStaff().getUser().getOrg().getName());
        }
    }

    private void handleGenerateMaNV(SalaryResultStaffItem cell) {
        // Generate employee code logic here
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null
                && cell.getSalaryResultStaff().getStaff().getStaffCode() != null) {
            cell.setValue(cell.getSalaryResultStaff().getStaff().getStaffCode());
        }
    }

    private void handleGenerateEmailNV(SalaryResultStaffItem cell) {
        // Generate email logic here
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null
                && cell.getSalaryResultStaff().getStaff().getEmail() != null) {
            cell.setValue(cell.getSalaryResultStaff().getStaff().getEmail());
        }
    }

    private void handleGenerateSDTNV(SalaryResultStaffItem cell) {
        // Generate phone number logic here
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null
                && cell.getSalaryResultStaff().getStaff().getPhoneNumber() != null) {
            cell.setValue(cell.getSalaryResultStaff().getStaff().getPhoneNumber());
        }
    }

    private void handleGenerateGioiTinhNV(SalaryResultStaffItem cell) {
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null
                && cell.getSalaryResultStaff().getStaff().getGender() != null) {
            String cellValue = "";
            if (cell.getSalaryResultStaff().getStaff().getGender().equals("M"))
                cellValue = "Nam";
            else if (cell.getSalaryResultStaff().getStaff().getGender().equals("F"))
                cellValue = "Nữ";

            cell.setValue(cellValue);
        }
    }

    private void handleGenerateQuocTichNV(SalaryResultStaffItem cell) {
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null
                && cell.getSalaryResultStaff().getStaff().getNationality() != null) {
            cell.setValue(cell.getSalaryResultStaff().getStaff().getNationality().getName());
        }
    }

    private void handleGenerateNguyenQuanNV(SalaryResultStaffItem cell) {
        if (cell.getSalaryResultStaff() != null && cell.getSalaryResultStaff().getStaff() != null
                && cell.getSalaryResultStaff().getStaff().getNativeVillage() != null) {
            cell.setValue(cell.getSalaryResultStaff().getStaff().getNativeVillage().getName());
        }
    }

    // Fallback method for unhandled cases
    private void handleDefaultCase(SalaryResultStaffItem cell, SalaryItem usingItem) {
        // Handle unhandled cases, if necessary, or log for debugging
        System.out.println("No handler found for code: " + usingItem.getCode());
    }

    @Override
    public void importItemValueAndRecalculateStaffPayslip(List<ImportSalaryResultStaffDto> staffAndImportValues) {
        if (staffAndImportValues == null || staffAndImportValues.isEmpty()) return;

        for (ImportSalaryResultStaffDto importStaff : staffAndImportValues) {
            // looping on staff

            if (importStaff.getSalaryItemValues() == null || importStaff.getSalaryItemValues().isEmpty()) {
                continue;
            }

            for (ImportSalaryStaffItemValueDto importItem : importStaff.getSalaryItemValues()) {
                SalaryResultStaffItemImportDto cellItem = new SalaryResultStaffItemImportDto();

                cellItem.setStaffCode(importStaff.getStaffCode());
                cellItem.setSalaryItemValue(importItem.getSalaryItemValue());
                cellItem.setSalaryItemCode(importItem.getSalaryItemCode());
                cellItem.setSalaryPeriodCode(importStaff.getSalaryPeriodCode());
                cellItem.setRecalculateStaffPayslipAfterProcess(true);

                // Nhập dữ liệu trong phiếu lương của nhân viên
                this.importSalaryResultStaffItemValue(cellItem);
            }

        }
    }

    // update giá trị thành phần lương cho nhân viên
    @Override
    public List<SalaryResultStaffItemDto> importSalaryResultStaffItemValue(SalaryResultStaffItemImportDto dto) {
        if (dto == null) return null;

        List<Staff> availableStaffs = staffRepository.findByCode(dto.getStaffCode());
        if (availableStaffs == null || availableStaffs.isEmpty()) return null;
        Staff staff = availableStaffs.get(0);

        List<SalaryPeriod> availableSalaryPeriods = salaryPeriodRepository.findByCode(dto.getSalaryPeriodCode());
        if (availableSalaryPeriods == null || availableSalaryPeriods.isEmpty()) return null;
        SalaryPeriod salaryPeriod = availableSalaryPeriods.get(0);

        CalculateSalaryRequest calculateSalaryRequest = new CalculateSalaryRequest(staff.getId(), salaryPeriod.getId(), null);

        if (dto.getSalaryTemplateCode() != null) {
            List<SalaryTemplate> availableTemplates = salaryTemplateRepository.findByCode(dto.getSalaryTemplateCode());
            if (availableTemplates == null || availableTemplates.isEmpty()) {
                return null;
            }
            SalaryTemplate salaryTemplate = availableTemplates.get(0);
            calculateSalaryRequest.setSalaryTemplateId(salaryTemplate.getId());
        }

        List<SalaryTemplate> salaryTemplates = salaryTemplateService.getListValidSalaryTemplatesOfStaffInPeriod(calculateSalaryRequest);

        // Nhân viên không được chọn tính lương theo mẫu bảng lương nào => Không import được dữ liệu
        if (salaryTemplates == null || salaryTemplates.isEmpty()) return null;

        // Kết quả import vào các giá trị của phiếu lương trong các mẫu bảng lương hợp lệ của nhân viên
        List<SalaryResultStaffItemDto> response = new ArrayList<>();

        for (SalaryTemplate salaryTemplate : salaryTemplates) {
            List<SalaryTemplateItem> availableSalaryTemplateItems = salaryTemplateItemRepository.findSalaryTemplateItemByTemplateIdAndTemplateItemCode(salaryTemplate.getId(), dto.getSalaryItemCode());
            // Không tồn tại thành phần lương cần import trong mẫu bảng lương này
            if (availableSalaryTemplateItems == null || availableSalaryTemplateItems.isEmpty()) {
                continue;
            }

            SalaryTemplateItem salaryTemplateItem = availableSalaryTemplateItems.get(0);

            // Nếu thành phần lương không phải là tự nhập => bỏ qua, không import vào phiếu lương
            if (salaryTemplateItem.getCalculationType() != null && !(salaryTemplateItem.getCalculationType().equals(HrConstants.SalaryItemCalculationType.USER_FILL.getValue()) || salaryTemplateItem.getCalculationType().equals(HrConstants.SalaryItemCalculationType.AUTO_SYSTEM.getValue()))) {
                continue;
            }

            // Các ô trong phiếu lương
            List<SalaryResultStaffItem> availableResultStaffItems = salaryResultStaffItemRepository.findByStaffIdSalaryPeriodIdAndSalaryTemplateItemId(staff.getId(), salaryPeriod.getId(), salaryTemplateItem.getId());

            SalaryResultStaffItem cell = null;

            // nếu chưa có phiếu lương nào VỚI MẪU BẢNG LƯƠNG NÀY thì tạo phiếu lương cho nhân viên
            if (availableResultStaffItems == null || availableResultStaffItems.isEmpty()) {
                SalaryResultStaffDto salaryCalculationRequest = new SalaryResultStaffDto();

                salaryCalculationRequest.setSalaryPeriod(new SalaryPeriodDto(salaryPeriod));
                salaryCalculationRequest.setStaff(new StaffDto(staff));
                salaryCalculationRequest.setSalaryTemplate(new SalaryTemplateDto(salaryTemplate));

                // Phiếu lương được tạo theo MẪU BẢNG LƯƠNG hiện tại
                SalaryResultStaffDto createdPayslip = salaryResultStaffService.calculateSalaryStaff(salaryCalculationRequest);

                availableResultStaffItems = salaryResultStaffItemRepository.findByStaffIdSalaryPeriodIdAndSalaryTemplateItemId(staff.getId(), salaryPeriod.getId(), salaryTemplateItem.getId());

                if (!CollectionUtils.isEmpty(availableResultStaffItems)) {
                    cell = availableResultStaffItems.get(0);

                    if (canUpdatePayslipFromItem(cell)) {
                        cell = this.updateSalaryResultStaffItemValue(cell, dto.getSalaryItemValue());
                    } else {
                        logger.error("Phiếu lương đã bị khóa, không được cập nhật ô trong phiếu lương có id: " + cell.getId());
                    }
                } else {
                    logger.error("Không tồn tại thành phần lương " + salaryTemplateItem.getCode() + " trong phiếu lương của nhân viên " + staff.getDisplayName());
                }
            }
            // đã có phiếu lương
            else {
                if (!CollectionUtils.isEmpty(availableResultStaffItems)) {
                    cell = availableResultStaffItems.get(0);

                    if (canUpdatePayslipFromItem(cell)) {
                        cell = this.updateSalaryResultStaffItemValue(cell, dto.getSalaryItemValue());
                    } else {
                        logger.error("Phiếu lương đã bị khóa, không được cập nhật ô trong phiếu lương có id: " + cell.getId());
                    }
                } else {
                    logger.error("Không tồn tại thành phần lương " + salaryTemplateItem.getCode() + " trong phiếu lương của nhân viên " + staff.getDisplayName());
                }
            }

            // Có tính lại phiếu lương của nhân viên (salaryResultStaff) sau khi import dữ liệu vào salaryResultStaffItem hay không
            if (dto.getRecalculateStaffPayslipAfterProcess() != null && dto.getRecalculateStaffPayslipAfterProcess().equals(true) && cell != null && cell.getSalaryResultStaff() != null) {
                SalaryResultStaff payslip = salaryResultStaffRepository.findById(cell.getSalaryResultStaff().getId()).orElse(null);

                SalaryResultStaffDto recalculatedPayslip = salaryResultStaffService.recalculateSalaryStaff(new SalaryResultStaffDto(payslip, true));
            }

            SalaryResultStaffItemDto cellDto = new SalaryResultStaffItemDto(cell);
            response.add(cellDto);
        }

        entityManager.flush();
        entityManager.clear();

        return response;
    }

    private Boolean canUpdatePayslipFromItem(SalaryResultStaffItem cell) {
        if (cell == null) return false;

        if (cell.getSalaryResultStaff() == null) return true;

        if (cell.getSalaryResultStaff().getApprovalStatus() != null && cell.getSalaryResultStaff().getApprovalStatus().equals(HrConstants.SalaryResulStaffApprovalStatus.LOCKED.getValue()))
            return false;

        return true;
    }

    private SalaryResultStaffItem updateSalaryResultStaffItemValue(SalaryResultStaffItem cell, String newCellValue) {
        cell.setValue(newCellValue);
        cell = salaryResultStaffItemRepository.save(cell);
        return cell;
    }


    @Override
    public void updateTimekeepingDataForPayslips(UUID staffId, Date requestDate) {
        if (staffId == null || requestDate == null) {
            return;
        }

        Staff staff = staffRepository.findById(staffId).orElse(null);
        if (staff == null) return;

        this.updateTimekeepingDataForPayslips(staff, requestDate);

        entityManager.flush();
        entityManager.clear();
    }

    /**
     * tạo/update thành phần lương của nhân viên khi chấm công: số giờ làm việc, số công làm việc, số lần đi làm muộn
     */
    @Override
    public void updateTimekeepingDataForPayslips(Staff staff, Date requestDate) {
        List<SalaryPeriod> salaryPeriods = salaryPeriodRepository.getActivePeriodsByDate(requestDate);

        if (salaryPeriods == null || CollectionUtils.isEmpty(salaryPeriods) || staff == null) return;

        SalaryPeriod salaryPeriod = salaryPeriods.get(0);


        // Số giờ công tiêu chuẩn
        Double soGioCongTieuChuan = salaryAutoCalculationService.getSoGioCongTieuChuan(staff.getId(), salaryPeriod);
        if (soGioCongTieuChuan != null) {
            this.createAndImportSalaryResultItem(staff, salaryPeriod, soGioCongTieuChuan,
                    HrConstants.SalaryItemAutoConnectCode.SO_GIO_CONG_TIEU_CHUAN.getValue());
        }

        // Số giờ làm việc thực tế
        Double soGioLamViecHopLe = salaryAutoCalculationService.getSoGioLamViecHopLe(staff.getId(), salaryPeriod);
        if (soGioLamViecHopLe != null) {
            this.createAndImportSalaryResultItem(staff, salaryPeriod, soGioLamViecHopLe,
                    HrConstants.SalaryItemAutoConnectCode.SO_GIO_LAM_VIEC_HOP_LE.getValue());
        }

        // Tổng số giờ tăng ca được xác nhận
        Double confirmedOtHours = salaryAutoCalculationService.getSoGioOTDuocXacNhan(staff.getId(), salaryPeriod);
        if (confirmedOtHours != null) {
            this.createAndImportSalaryResultItem(staff, salaryPeriod, confirmedOtHours,
                    HrConstants.SalaryItemAutoConnectCode.SO_GIO_OT_DUOC_XAC_NHAN.getValue());
        }

        // Số giờ tăng ca thử việc
        Double soGioTangCaThuViec = salaryAutoCalculationService.getSoGioTangCaThuViec(staff.getId(), salaryPeriod);
        if (soGioTangCaThuViec != null) {
            this.createAndImportSalaryResultItem(staff, salaryPeriod, soGioTangCaThuViec,
                    HrConstants.SalaryItemAutoConnectCode.SO_GIO_TANG_CA_TV.getValue());
        }

        // Số giờ tăng ca chính thức
        Double soGioTangCaChinhThuc = salaryAutoCalculationService.getSoGioTangCaChinhThuc(staff.getId(), salaryPeriod);
        if (soGioTangCaChinhThuc != null) {
            this.createAndImportSalaryResultItem(staff, salaryPeriod, soGioTangCaChinhThuc,
                    HrConstants.SalaryItemAutoConnectCode.SO_GIO_TANG_CA_CT.getValue());
        }

        Double soPhutTreSomThuViec = salaryAutoCalculationService.getSoPhutTreSomThuViec(staff.getId(), salaryPeriod);
        if (soPhutTreSomThuViec != null) {
            this.createAndImportSalaryResultItem(staff, salaryPeriod, soPhutTreSomThuViec,
                    HrConstants.SalaryItemAutoConnectCode.SO_PHUT_TRE_SOM_TV.getValue());
        }

        Double soPhutTreSomChinhThuc = salaryAutoCalculationService.getSoPhutTreSomChinhThuc(staff.getId(), salaryPeriod);
        if (soPhutTreSomChinhThuc != null) {
            this.createAndImportSalaryResultItem(staff, salaryPeriod, soPhutTreSomChinhThuc,
                    HrConstants.SalaryItemAutoConnectCode.SO_PHUT_TRE_SOM_CT.getValue());
        }

//        Double ngayCongChuan = salaryAutoCalculationService.getSoNgayCongChuan(staff.getId(), salaryPeriod);
//        if (ngayCongChuan != null) {
//            this.createAndImportSalaryResultItem(staff, salaryPeriod, ngayCongChuan,
//                    HrConstants.SalaryItemAutoConnectCode.NGAY_CONG_CHUAN.getValue());
//        }

        Double ngayCongHuongLuongThuViec = salaryAutoCalculationService.getSoNgayCongHuongLuongThuViec(staff.getId(), salaryPeriod);
        if (ngayCongHuongLuongThuViec != null) {
            this.createAndImportSalaryResultItem(staff, salaryPeriod, ngayCongHuongLuongThuViec,
                    HrConstants.SalaryItemAutoConnectCode.NGAY_CONG_HUONG_LUONG_TV.getValue());
        }

        Double ngayCongHuongLuongChinhThuc = salaryAutoCalculationService.getSoNgayCongHuongLuongChinhThuc(staff.getId(), salaryPeriod);
        if (ngayCongHuongLuongChinhThuc != null) {
            this.createAndImportSalaryResultItem(staff, salaryPeriod, ngayCongHuongLuongChinhThuc,
                    HrConstants.SalaryItemAutoConnectCode.NGAY_CONG_HUONG_LUONG_CT.getValue());
        }

//        Double daTamUng = salaryAutoCalculationService.getTamUng(staff.getId(), salaryPeriod);
//        if (daTamUng != null) {
//            this.createAndImportSalaryResultItem(staff, salaryPeriod, daTamUng,
//                    HrConstants.SalaryItemAutoConnectCode.DA_TAM_UNG.getValue());
//        }

//        Double soNguoiPhuThuoc = salaryAutoCalculationService.getSoNguoiPhuThuocThue(staff.getId(), salaryPeriod);
//        if (soNguoiPhuThuoc != null) {
//            this.createAndImportSalaryResultItem(staff, salaryPeriod, soNguoiPhuThuoc,
//                    HrConstants.SalaryItemAutoConnectCode.SO_NGUOI_PHU_THUOC_THUE.getValue());
//        }

        Double ngayCongTinhLuong = salaryAutoCalculationService.getNgayCongTinhLuongThue(staff.getId(), salaryPeriod);
        if (ngayCongTinhLuong != null) {
            this.createAndImportSalaryResultItem(staff, salaryPeriod, ngayCongTinhLuong,
                    HrConstants.SalaryItemAutoConnectCode.NGAY_CONG_TINH_LUONG_THUE.getValue());
        }
    }


    @Override
    public void createAndImportSalaryResultItem(Staff staff, SalaryPeriod salaryPeriod, Double value,
                                                String salaryItemCode) {


        SalaryResultStaffItemImportDto importDto = new SalaryResultStaffItemImportDto();

        importDto.setStaffCode(staff.getStaffCode());
        importDto.setSalaryPeriodCode(salaryPeriod.getCode());
        importDto.setSalaryItemValue(value.toString());
        importDto.setRecalculateStaffPayslipAfterProcess(true);
        importDto.setSalaryItemCode(salaryItemCode);

//        // Mã của các thành phần lương được kết nối
//        List<String> salaryItemCodes = salaryAutoMapService.getCorrespondingSalaryItemsCode(salaryAutoMap);
//        if (salaryItemCodes == null || salaryItemCodes.isEmpty()) {
//            return;
//        }
//
//        for (String itemCode : salaryItemCodes) {
//            importDto.setSalaryItemCode(itemCode);

        this.importSalaryResultStaffItemValue(importDto);
//        }
    }

    public List<Date> getDaysBetweenDates(Date fromDate, Date toDate) {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);

        while (!calendar.getTime().after(toDate)) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dates;
    }

}
