package com.globits.salary.service.impl;

import com.globits.hr.HrConstants;
import com.globits.hr.domain.*;
import com.globits.hr.repository.*;
import com.globits.hr.utils.DateTimeUtil;
import com.globits.salary.domain.SalaryPeriod;
import com.globits.salary.domain.StaffAdvancePayment;
import com.globits.salary.repository.SalaryAutoMapRepository;
import com.globits.salary.repository.SalaryItemRepository;
import com.globits.salary.repository.StaffAdvancePaymentRepository;
import com.globits.salary.service.SalaryAutoCalculationService;
import com.globits.salary.service.SalaryAutoMapService;
import com.globits.timesheet.repository.PublicHolidayDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class SalaryAutoCalculationServiceImpl implements SalaryAutoCalculationService {
    @Autowired
    private StaffWorkScheduleRepository staffWorkScheduleRepository;

    @Autowired
    private PublicHolidayDateRepository publicHolidayDateRepository;

    @Autowired
    private SalaryAutoMapRepository salaryAutoMapRepository;

    @Autowired
    private SalaryItemRepository salaryItemRepository;

    @Autowired
    private SalaryAutoMapService salaryAutoMapService;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StaffAdvancePaymentRepository staffAdvancePaymentRepository;

    @Autowired
    private StaffFamilyRelationshipRepository staffFamilyRelationshipRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private OtherIncomeRepository otherIncomeRepository;

    @Autowired
    private StaffMaternityHistoryRepository staffMaternityHistoryRepository;

    @Override
    public boolean isAutoConnectionCode(String code) {
        if (code == null)
            return false;

        code = code.trim();

        for (HrConstants.SalaryItemAutoConnectCode field : HrConstants.SalaryItemAutoConnectCode.values()) {
            String fieldCode = field.getValue().trim();

            if (fieldCode.equals(code)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Double detectConstantsAndGetValue(String salaryTemplateItemCode, SalaryPeriod salaryPeriod, UUID staffId) {
        DecimalFormat df = new DecimalFormat("0.##"); // Keeps all significant digits
        Double result = 0D;

        if (salaryTemplateItemCode == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return result;
        }

        salaryPeriod.setFromDate(DateTimeUtil.getStartOfDay(salaryPeriod.getFromDate()));
        salaryPeriod.setToDate(DateTimeUtil.getEndOfDay(salaryPeriod.getToDate()));

        // Assuming this is the dynamic code that will be checked
        String code = salaryTemplateItemCode;

        code = code.trim();

        if (HrConstants.SalaryItemAutoConnectCode.SO_GIO_TANG_CA_TV.getValue().equals(code)) {
            result = convertToDouble(getSoGioTangCaThuViec(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.SO_GIO_TANG_CA_CT.getValue().equals(code)) {
            result = convertToDouble(getSoGioTangCaChinhThuc(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.SO_GIO_CONG_TIEU_CHUAN.getValue().equals(code)) {
            result = convertToDouble(getSoGioCongTieuChuan(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.SO_PHUT_TRE_SOM_TV.getValue().equals(code)) {
            result = convertToDouble(getSoPhutTreSomThuViec(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.SO_PHUT_TRE_SOM_CT.getValue().equals(code)) {
            result = convertToDouble(getSoPhutTreSomChinhThuc(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.NGAY_CONG_CHUAN.getValue().equals(code)) {
//            result = convertToDouble(getSoNgayCongChuan(staffId, salaryPeriod));
//            result = convertToDouble(getSoNgayCongChuanByPublicHolidayDate(staffId, salaryPeriod));
            result = convertToDouble(getSoNgayCongChuanByPositionTitle(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.NGAY_CONG_HUONG_LUONG_TV.getValue().equals(code)) {
            result = convertToDouble(getSoNgayCongHuongLuongThuViec(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.NGAY_CONG_HUONG_LUONG_CT.getValue().equals(code)) {
            result = convertToDouble(getSoNgayCongHuongLuongChinhThuc(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.DA_TAM_UNG.getValue().equals(code)) {
            result = convertToDouble(getTamUng(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.SO_NGUOI_PHU_THUOC_THUE.getValue().equals(code)) {
            result = convertToDouble(getSoNguoiPhuThuocThue(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.NGAY_CONG_TINH_LUONG_THUE.getValue().equals(code)) {
            result = convertToDouble(getNgayCongTinhLuongThue(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.NGAY_CONG_THUC_TE_DI_LAM.getValue().equals(code)) {
            result = convertToDouble(getNgayCongThucTeDiLam(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.NGAY_VUOT_CONG_CHINH_THUC.getValue().equals(code)) {
            result = convertToDouble(getSoNgayVuotCongChinhThuc(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.NGAY_VUOT_CONG_THU_VIEC.getValue().equals(code)) {
            result = convertToDouble(getSoNgayVuotCongThuViec(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.TIEN_TRU_KHAC.getValue().equals(code)) {
            result = convertToDouble(getTrichTruKhac(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.THU_NHAP_KHAC.getValue().equals(code)) {
            result = convertToDouble(getThuNhapKhac(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.SO_GIO_LAM_VIEC_HOP_LE.getValue().equals(code)) {
            result = convertToDouble(getSoGioLamViecHopLe(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.SO_GIO_OT_DUOC_XAC_NHAN.getValue().equals(code)) {
            result = convertToDouble(getSoGioOTDuocXacNhan(staffId, salaryPeriod));
        } else if (HrConstants.SalaryItemAutoConnectCode.CO_DONG_BHXH.getValue().equals(code)) {
            result = convertToDouble(getCoDongBHXH(staffId, salaryPeriod));
        }
//        if (HrConstants.SalaryAutoMapField.SO_GIO_LAM_VIEC_DUOC_PHAN.getValue().equals(code)) {
//            result = convertToString(getSoGioLamViecDuocPhan(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_GIO_LAM_VIEC_THUC_TE.getValue().equals(code)) {
//            result = convertToString(getSoGioLamViecThucTe(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_GIO_LAM_THEM_TRUOC_CA.getValue().equals(code)) {
//            result = convertToString(getSoGioLamThemTruocCa(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_GIO_LAM_THEM_SAU_CA.getValue().equals(code)) {
//            result = convertToString(getSoGioLamThemSauCa(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_GIO_LAM_VIEC_CONG_QUY_DOI.getValue().equals(code)) {
//            result = convertToString(getSoGioLamViecCongQuyDoi(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_CA_DUOC_PHAN.getValue().equals(code)) {
//            result = convertToString(getSoCaDuocPhan(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_CA_DI_LAM_DU.getValue().equals(code)) {
//            result = convertToString(getSoCaDiLamDu(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_CA_DI_LAM_THIEU.getValue().equals(code)) {
//            result = convertToString(getSoCaDiLamThieu(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_CA_KHONG_DI_LAM.getValue().equals(code)) {
//            result = convertToString(getSoCaKhongDiLam(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_CA_CONG_TAC.getValue().equals(code)) {
//            result = convertToString(getSoCaCongTac(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_CA_NGHI_BU.getValue().equals(code)) {
//            result = convertToString(getSoCaNghiBu(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_CA_NGHI_CHE_DO.getValue().equals(code)) {
//            result = convertToString(getSoCaNghiCheDo(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_CA_NGHI_KHONG_LUONG.getValue().equals(code)) {
//            result = convertToString(getSoCaNghiKhongLuong(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_CA_NGHI_LE.getValue().equals(code)) {
//            result = convertToString(getSoCaNghiLe(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_CA_NGHI_PHEP.getValue().equals(code)) {
//            result = convertToString(getSoCaNghiPhep(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_LAN_DI_MUON.getValue().equals(code)) {
//            result = convertToString(getSoLanDiMuon(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_LAN_VE_SOM.getValue().equals(code)) {
//            result = convertToString(getSoLanVeSom(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_PHUT_DI_MUON.getValue().equals(code)) {
//            result = convertToString(getSoPhutDiMuon(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_PHUT_VE_SOM.getValue().equals(code)) {
//            result = convertToString(getSoPhutVeSom(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_PHUT_DI_SOM.getValue().equals(code)) {
//            result = convertToString(getSoPhutDiSom(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_PHUT_VE_MUON.getValue().equals(code)) {
//            result = convertToString(getSoPhutVeMuon(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.SO_CONG_DUOC_TINH.getValue().equals(code)) {
//            result = convertToString(getSoCongDuocTinh(staffId, salaryPeriod));
//        }

//        else if (HrConstants.SalaryAutoMapField.TAM_UNG.getValue().equals(code)) {
//            result = convertToString(getTamUng(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.BHXH_NHAN_VIEN_DONG.getValue().equals(code)) {
//            result = convertToString(getBHXHNV(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.BHYT_NHAN_VIEN_DONG.getValue().equals(code)) {
//            result = convertToString(getBHYTNV(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.BHTN_NHAN_VIEN_DONG.getValue().equals(code)) {
//            result = convertToString(getBHTNNV(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.KPCD_NHAN_VIEN_DONG.getValue().equals(code)) {
//            result = convertToString(getKPCDNV(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.BHXH_CONG_TY_DONG.getValue().equals(code)) {
//            result = convertToString(getBHXHCT(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.BHYT_CONG_TY_DONG.getValue().equals(code)) {
//            result = convertToString(getBHYTCT(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.BHTN_CONG_TY_DONG.getValue().equals(code)) {
//            result = convertToString(getBHTNCT(staffId, salaryPeriod));
//        } else if (HrConstants.SalaryAutoMapField.KPCD_CONG_TY_DONG.getValue().equals(code)) {
//            result = convertToString(getKPCDCT(staffId, salaryPeriod));
//        }

//        // 20-11-2024 - default set value of the cell with default value
//        else if (cell.getSalaryResultItem().getDefaultValue() != null && !cell.getSalaryResultItem().getDefaultValue().isEmpty()) {
//            if (cell.getSalaryResultItem().getValueType().equals(HrConstants.SalaryItemValueType.TEXT.getValue())
//                    || cell.getSalaryResultItem().getValueType().equals(HrConstants.SalaryItemValueType.OTHERS.getValue())) {
//                DecimalFormat df = new DecimalFormat("0.##"); // Keeps all significant digits
//                String stringFormat = df.format(cell.getValue());
//                cell.setValue(stringFormat);
//            } else {
//                cell.setValue(cell.getSalaryResultItem().getDefaultValue());
//            }
//        }

        else {
            System.out.println("Unhanded constants " + salaryTemplateItemCode);
        }

        if (result == null)
            result = 0D;

//        return df.format("0.00");

        return result;
    }

    public static String convertToString(Object input) {
        return String.valueOf(input);
    }

    public static double convertToDouble(Object input) {
        if (input == null) {
            return 0.0; // or throw an exception depending on your use case
        }
        try {
            return Double.parseDouble(input.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cannot convert to double: " + input, e);
        }
    }


    @Override
    public Double getCoDongBHXH(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        Staff staff = staffRepository.findById(staffId).orElse(null);

        if (staff == null || staff.getSkipOvertimeCount() != null && staff.getSkipOvertimeCount().equals(true)) {
            return 0D;
        }

        double response = 0D;

        // Duyệt qua tất cả các StaffWorkSchedule
        if (staff.getHasSocialIns() != null && staff.getHasSocialIns().equals(true)) {
            response = 1;
        }

        return response;
    }

    @Override
    public Double getSoGioLamViecDuocPhan(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Tổng số giờ làm việc được phân
        Double countResult = staffWorkScheduleRepository.sumTotalAssignedHours(staffId, salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        // Biến dùng để cộng dồn tổng số giờ làm việc
        double totalHours = 0D;

        // Duyệt qua tất cả các StaffWorkSchedule
        if (countResult != null) {
            totalHours = countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoGioLamViecThucTe(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Tổng số giờ làm việc thực tế (đã chấm công)
        Double countResult = staffWorkScheduleRepository.sumActualWorkedHours(staffId, salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        // Biến dùng để cộng dồn tổng số giờ làm việc
        double totalHours = 0D;

        // Gán kết quả nếu khác null
        if (countResult != null) {
            totalHours = countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoGioLamThemTruocCa(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        Staff staff = staffRepository.findById(staffId).orElse(null);

        // Nhân viên không tính OT và không tính ngày vượt công
        if (staff == null || staff.getSkipOvertimeCount() != null && staff.getSkipOvertimeCount().equals(true)) {
            return 0D;
        }

        // Tổng số giờ làm thêm trước ca (OT trước ca)
        Double countResult = staffWorkScheduleRepository.sumConfirmedOTHoursBeforeShift(staffId,
                salaryPeriod.getFromDate(), salaryPeriod.getToDate());

        double totalHours = 0D;

        if (countResult != null) {
            totalHours = countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoGioLamThemSauCa(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        Staff staff = staffRepository.findById(staffId).orElse(null);

        // Nhân viên không tính OT và không tính ngày vượt công
        if (staff == null || staff.getSkipOvertimeCount() != null && staff.getSkipOvertimeCount().equals(true)) {
            return 0D;
        }

        // Tổng số giờ làm thêm sau ca (OT sau ca)
        Double countResult = staffWorkScheduleRepository.sumConfirmedOTHoursAfterShift(staffId,
                salaryPeriod.getFromDate(), salaryPeriod.getToDate());

        double totalHours = 0D;

        if (countResult != null) {
            totalHours = countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoGioLamViecCongQuyDoi(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Tổng số giờ làm việc công quy đổi
        Double countResult = staffWorkScheduleRepository.sumConvertedWorkingHours(staffId, salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        double totalHours = 0D;

        if (countResult != null) {
            totalHours = countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoCaDuocPhan(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Truy xuất danh sách các StaffWorkSchedule của nhân viên trong kỳ lương
        List<StaffWorkSchedule> staffWorkSchedules = staffWorkScheduleRepository
                .findByStaffIdAndWorkingDateBetween(staffId, salaryPeriod.getFromDate(), salaryPeriod.getToDate());

        double totalHours = 0.0;

        if (staffWorkSchedules != null && !staffWorkSchedules.isEmpty()) {
            totalHours = (double) staffWorkSchedules.size();
        }

        return totalHours;
    }

    @Override
    public Double getSoCaDiLamDu(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Truy xuất danh sách các StaffWorkSchedule của nhân viên trong kỳ lương
        Long countResult = staffWorkScheduleRepository.countByStaffIdWorkingStatusAndWorkingDateBetween(staffId,
                HrConstants.StaffWorkScheduleWorkingStatus.FULL_ATTENDANCE.getValue(), salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        double totalHours = 0D;
        if (countResult != null) {
            totalHours = (double) countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoCaDiLamThieu(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Truy xuất danh sách các StaffWorkSchedule của nhân viên trong kỳ lương
        Long countResult = staffWorkScheduleRepository.countByStaffIdWorkingStatusAndWorkingDateBetween(staffId,
                HrConstants.StaffWorkScheduleWorkingStatus.PARTIAL_ATTENDANCE.getValue(), salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        double totalHours = 0D;
        if (countResult != null) {
            totalHours = (double) countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoCaKhongDiLam(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Truy xuất danh sách các StaffWorkSchedule của nhân viên trong kỳ lương
        Long countResult = staffWorkScheduleRepository.countByStaffIdWorkingStatusAndWorkingDateBetween(staffId,
                HrConstants.StaffWorkScheduleWorkingStatus.NOT_ATTENDANCE.getValue(), salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        double totalHours = 0D;
        if (countResult != null) {
            totalHours = (double) countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoCaCongTac(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Truy xuất danh sách các StaffWorkSchedule của nhân viên trong kỳ lương
        Long countResult = staffWorkScheduleRepository.countByStaffIdLeaveTypeAndWorkingDateBetween(staffId,
                HrConstants.LeaveTypeCode.BUSINESS_TRIP.getCode(), salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        double totalHours = 0D;
        if (countResult != null) {
            totalHours = (double) countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoCaNghiBu(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Truy xuất danh sách các StaffWorkSchedule của nhân viên trong kỳ lương
        Long countResult = staffWorkScheduleRepository.countByStaffIdLeaveTypeAndWorkingDateBetween(staffId,
                HrConstants.LeaveTypeCode.COMPENSATORY_LEAVE.getCode(), salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        double totalHours = 0D;
        if (countResult != null) {
            totalHours = (double) countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoCaNghiCheDo(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Truy xuất danh sách các StaffWorkSchedule của nhân viên trong kỳ lương
        Long countResult = staffWorkScheduleRepository.countByStaffIdLeaveTypeAndWorkingDateBetween(staffId,
                HrConstants.LeaveTypeCode.SPECIAL_LEAVE.getCode(), salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        double totalHours = 0D;
        if (countResult != null) {
            totalHours = (double) countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoCaNghiKhongLuong(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Truy xuất danh sách các StaffWorkSchedule của nhân viên trong kỳ lương
        Long countResult = staffWorkScheduleRepository.countByStaffIdLeaveTypeAndWorkingDateBetween(staffId,
                HrConstants.LeaveTypeCode.UNPAID_LEAVE.getCode(), salaryPeriod.getFromDate(), salaryPeriod.getToDate());

        double totalHours = 0D;
        if (countResult != null) {
            totalHours = (double) countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoCaNghiLe(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Truy xuất danh sách các StaffWorkSchedule của nhân viên trong kỳ lương
        Long countResult = staffWorkScheduleRepository.countByStaffIdLeaveTypeAndWorkingDateBetween(staffId,
                HrConstants.LeaveTypeCode.PUBLIC_HOLIDAY.getCode(), salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        double totalHours = 0D;
        if (countResult != null) {
            totalHours = (double) countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoCaNghiPhep(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Truy xuất danh sách các StaffWorkSchedule của nhân viên trong kỳ lương
        Long countResult = staffWorkScheduleRepository.countByStaffIdLeaveTypeAndWorkingDateBetween(staffId,
                HrConstants.LeaveTypeCode.ANNUAL_LEAVE.getCode(), salaryPeriod.getFromDate(), salaryPeriod.getToDate());

        double totalHours = 0D;
        if (countResult != null) {
            totalHours = (double) countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoLanDiMuon(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Truy xuất danh sách các StaffWorkSchedule của nhân viên trong kỳ lương
        Long countResult = staffWorkScheduleRepository.countTotalLateArrivals(staffId, salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        double totalHours = 0D;
        if (countResult != null) {
            totalHours = (double) countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoLanVeSom(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Truy xuất danh sách các StaffWorkSchedule của nhân viên trong kỳ lương
        Long countResult = staffWorkScheduleRepository.countTotalEarlyExits(staffId, salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        double totalHours = 0D;
        if (countResult != null) {
            totalHours = (double) countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoPhutDiMuon(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Truy xuất danh sách các StaffWorkSchedule của nhân viên trong kỳ lương
        Long countResult = staffWorkScheduleRepository.countTotalLateArrivalMinutes(staffId, salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        double totalHours = 0D;
        if (countResult != null) {
            totalHours = (double) countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoPhutVeSom(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Truy xuất danh sách các StaffWorkSchedule của nhân viên trong kỳ lương
        Long countResult = staffWorkScheduleRepository.countTotalEarlyExitMinutes(staffId, salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        double totalHours = 0D;
        if (countResult != null) {
            totalHours = (double) countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoPhutDiSom(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Truy xuất danh sách các StaffWorkSchedule của nhân viên trong kỳ lương
        Long countResult = staffWorkScheduleRepository.countTotalEarlyArrivalMinutes(staffId,
                salaryPeriod.getFromDate(), salaryPeriod.getToDate());

        double totalHours = 0D;
        if (countResult != null) {
            totalHours = (double) countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoPhutVeMuon(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Truy xuất danh sách các StaffWorkSchedule của nhân viên trong kỳ lương
        Long countResult = staffWorkScheduleRepository.countTotalLateExitMinutes(staffId, salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        double totalHours = 0D;
        if (countResult != null) {
            totalHours = (double) countResult;
        }

        return totalHours;
    }

    @Override
    public Double getSoCongDuocTinh(UUID staffId, SalaryPeriod salaryPeriod) {
        if (salaryPeriod == null || salaryPeriod.getFromDate() == null || salaryPeriod.getToDate() == null)
            return 0D;

        // Truy xuất danh sách các StaffWorkSchedule của nhân viên trong kỳ lương
        Double countResult = staffWorkScheduleRepository.sumTotalPaidWork(staffId, salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        double totalHours = 0D;
        if (countResult != null) {
            totalHours = countResult;
        }

        return totalHours;
    }

    private Date handleSetDefaulOfficialDate() {
        // Gán ngày mặc định 01/01/1900 nếu officialDate bị null
        Calendar calendar = Calendar.getInstance();
        calendar.set(1900, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @Override
    public Double getSoGioTangCaThuViec(UUID staffId, SalaryPeriod salaryPeriod) {
        Staff staff = staffRepository.findById(staffId).orElse(null);

        if (staff == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        Date officialDate = staff.getStartDate();
        if (officialDate == null) {
            officialDate = handleSetDefaulOfficialDate();
        }

        // Gọi repository để tính tổng giờ OT thử việc
        Double countResult = staffWorkScheduleRepository.sumOTDuringProbation(staffId, officialDate,
                salaryPeriod.getFromDate(), salaryPeriod.getToDate());

        return countResult != null ? countResult : 0D;
    }


    @Override
    public Double getSoGioOTDuocXacNhan(UUID staffId, SalaryPeriod salaryPeriod) {
        if (staffId == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        // Gọi repository để tính tổng giờ OT thử việc
        Double countResult = staffWorkScheduleRepository.sumConfirmedOTHoursInRangeTime(staffId,
                salaryPeriod.getFromDate(), salaryPeriod.getToDate());

        return countResult != null ? countResult : 0D;
    }

    @Override
    public Double getSoGioLamViecHopLe(UUID staffId, SalaryPeriod salaryPeriod) {
        Staff staff = staffRepository.findById(staffId).orElse(null);

        if (staff == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        // Gọi repository để tính tổng giờ
        Double countResult = staffWorkScheduleRepository.sumValidWorkingHoursStaff(staffId, salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        return countResult != null ? countResult : 0D;
    }

    @Override
    public Double getSoGioTangCaChinhThuc(UUID staffId, SalaryPeriod salaryPeriod) {
        Staff staff = staffRepository.findById(staffId).orElse(null);

        if (staff == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        Date officialDate = staff.getStartDate();
        if (officialDate == null) {
            officialDate = handleSetDefaulOfficialDate();
        }

        // Gọi repository để tính tổng giờ OT thử việc
        Double countResult = staffWorkScheduleRepository.sumOTDuringOfficialStaff(staffId, officialDate,
                salaryPeriod.getFromDate(), salaryPeriod.getToDate());

        return countResult != null ? countResult : 0D;
    }

    @Override
    public Double getSoPhutTreSomThuViec(UUID staffId, SalaryPeriod salaryPeriod) {
        Staff staff = staffRepository.findById(staffId).orElse(null);

        if (staff == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        Date officialDate = staff.getStartDate();
        if (officialDate == null) {
            officialDate = handleSetDefaulOfficialDate();
        }

        // Gọi repository để tính tổng phút trễ sớm trong thử việc
        Double countResult = staffWorkScheduleRepository.sumLateArrivalAndEarlyExitDuringProbation(staffId,
                officialDate, salaryPeriod.getFromDate(), salaryPeriod.getToDate());

        return countResult != null ? countResult : 0D;
    }

    @Override
    public Double getSoPhutTreSomChinhThuc(UUID staffId, SalaryPeriod salaryPeriod) {
        Staff staff = staffRepository.findById(staffId).orElse(null);

        if (staff == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        Date officialDate = staff.getStartDate();
        if (officialDate == null) {
            officialDate = handleSetDefaulOfficialDate();
        }

        // Gọi repository để tính tổng phút trễ sớm trong chính thức
        Double countResult = staffWorkScheduleRepository.sumLateArrivalAndEarlyExitDuringOfficialStaff(staffId,
                officialDate, salaryPeriod.getFromDate(), salaryPeriod.getToDate());

        return countResult != null ? countResult : 0D;
    }

//    @Override
//    public Double getSoPhutTreSomThuViec(UUID staffId, SalaryPeriod salaryPeriod) {
//        Staff staff = staffRepository.findById(staffId).orElse(null);
//
//        if (staff == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
//                || salaryPeriod.getToDate() == null) {
//            return 0D;
//        }
//
//        Set<Date> maternityDays = new HashSet<>();
//        if (staff.getGender() != null && staff.getGender().equals(HrConstants.Gender.FEMALE.getCode())) {
//            List<StaffMaternityHistory> staffMaternityHistories =
//                    staffMaternityHistoryRepository.findConjunctionInRangeTimeOfStaff(
//                            staffId,
//                            salaryPeriod.getFromDate(),
//                            salaryPeriod.getToDate()
//                    );
//
//            if (staffMaternityHistories != null && !staffMaternityHistories.isEmpty()) {
//                for (StaffMaternityHistory maternityHistory : staffMaternityHistories) {
//                    Date start = maternityHistory.getStartDate();
//                    Date end = maternityHistory.getEndDate();
//                    if (start != null && end != null) {
//                        // Add tất cả ngày từ start -> end vào set
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTime(start);
//                        while (!calendar.getTime().after(end)) {
//                            maternityDays.add(clearTime(calendar.getTime()));
//                            calendar.add(Calendar.DATE, 1);
//                        }
//                    } else if (start != null) {
//                        maternityDays.add(clearTime(start));
//                    } else if (end != null) {
//                        maternityDays.add(clearTime(end));
//                    }
//                }
//            }
//        }
//
//        Date officialDate = staff.getStartDate();
//        if (officialDate == null) {
//            officialDate = handleSetDefaulOfficialDate();
//        }
//
//        double countResult = 0D;
//
//        List<Object[]> queryResults = staffWorkScheduleRepository
//                .getObjectLateArrivalAndEarlyExitDuringProbation(staffId, officialDate, salaryPeriod.getFromDate(), salaryPeriod.getToDate());
//
//        if (queryResults != null && !queryResults.isEmpty()) {
//            for (Object[] row : queryResults) {
//                Number totalMinutesNumber = (Number) row[0];
//                Double totalMinutes = totalMinutesNumber != null ? totalMinutesNumber.doubleValue() : 0D;
//                Date workingDate = (Date) row[1];
//                if (totalMinutes == null) totalMinutes = 0D;
//
//                if (workingDate != null && maternityDays.contains(clearTime(workingDate))) {
//                    totalMinutes = totalMinutes - 60;
//                    if (totalMinutes < 0) {
//                        totalMinutes = 0D;
//                    }
//                }
//
//                countResult += totalMinutes;
//            }
//        }
//
//        return countResult;
//    }
//
//    /**
//     * Hàm để clear giờ phút giây, chỉ giữ ngày tháng năm
//     */
//    private Date clearTime(Date date) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//        return cal.getTime();
//    }
//
//
//    @Override
//    public Double getSoPhutTreSomChinhThuc(UUID staffId, SalaryPeriod salaryPeriod) {
//        Staff staff = staffRepository.findById(staffId).orElse(null);
//
//        if (staff == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
//                || salaryPeriod.getToDate() == null) {
//            return 0D;
//        }
//
//        Set<Date> maternityDays = new HashSet<>();
//        if (staff.getGender() != null && staff.getGender().equals(HrConstants.Gender.FEMALE.getCode())) {
//            List<StaffMaternityHistory> staffMaternityHistories =
//                    staffMaternityHistoryRepository.findConjunctionInRangeTimeOfStaff(
//                            staffId,
//                            salaryPeriod.getFromDate(),
//                            salaryPeriod.getToDate()
//                    );
//
//            if (staffMaternityHistories != null && !staffMaternityHistories.isEmpty()) {
//                for (StaffMaternityHistory maternityHistory : staffMaternityHistories) {
//                    Date start = maternityHistory.getStartDate();
//                    Date end = maternityHistory.getEndDate();
//                    if (start != null && end != null) {
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTime(start);
//                        while (!calendar.getTime().after(end)) {
//                            maternityDays.add(clearTime(calendar.getTime()));
//                            calendar.add(Calendar.DATE, 1);
//                        }
//                    } else if (start != null) {
//                        maternityDays.add(clearTime(start));
//                    } else if (end != null) {
//                        maternityDays.add(clearTime(end));
//                    }
//                }
//            }
//        }
//
//        Date officialDate = staff.getStartDate();
//        if (officialDate == null) {
//            officialDate = handleSetDefaulOfficialDate();
//        }
//
//        double countResult = 0D;
//
//        List<Object[]> queryResults = staffWorkScheduleRepository
//                .getObjectLateArrivalAndEarlyExitDuringOfficialStaff(staffId, officialDate, salaryPeriod.getFromDate(), salaryPeriod.getToDate());
//
//        if (queryResults != null && !queryResults.isEmpty()) {
//            for (Object[] row : queryResults) {
//                Number totalMinutesNumber = (Number) row[0];
//                Double totalMinutes = totalMinutesNumber != null ? totalMinutesNumber.doubleValue() : 0D;
//                Date workingDate = (Date) row[1];
//                if (totalMinutes == null) totalMinutes = 0D;
//
//                if (workingDate != null && maternityDays.contains(clearTime(workingDate))) {
//                    totalMinutes = totalMinutes - 60;
//                    if (totalMinutes < 0) {
//                        totalMinutes = 0D;
//                    }
//                }
//
//                countResult += totalMinutes;
//            }
//        }
//
//        return countResult;
//    }

    @Override
    public Double getSoNgayCongChuan(UUID staffId, SalaryPeriod salaryPeriod) {
        // Kiểm tra các tham số đầu vào
        if (staffId == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        // Truy vấn tổng workRatio từ repository
        Double countResult = staffWorkScheduleRepository.getTotalWorkRatio(staffId, salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        return countResult != null ? countResult : 0D;
    }

    @Override
    public Double getSoNgayCongChuanByPublicHolidayDate(UUID staffId, SalaryPeriod salaryPeriod) {
        if (staffId == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        Date fromDate = salaryPeriod.getFromDate();
        Date toDate = salaryPeriod.getToDate();

        // Chuyển sang LocalDate để xử lý chính xác ngày
        LocalDate fromLocalDate = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toLocalDate = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Tính tổng số ngày trong khoảng kỳ lương (bao gồm cả ngày cuối)
        long totalDays = ChronoUnit.DAYS.between(fromLocalDate, toLocalDate) + 1;

        // Lấy tổng số ngày nghỉ theo tỷ lệ (0.5 hoặc 1.0)
        Double holidayCount = publicHolidayDateRepository.getLeaveDayRatioHolidaysBetween(fromDate, toDate);
        if (holidayCount == null)
            holidayCount = 0D;

        // Số ngày công chuẩn = Tổng số ngày - số ngày nghỉ
        double workingDays = totalDays - holidayCount;

        return Math.max(workingDays, 0D);
    }




    @Override
    public Double getSoNgayCongChuanByFixLeaveWeekDays(UUID staffId, SalaryPeriod salaryPeriod) {
        if (staffId == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        Staff staff = staffRepository.findById(staffId).orElse(null);

        if (staff == null || staff.getStaffLeaveShiftType() == null || !staff.getStaffLeaveShiftType().equals(HrConstants.StaffLeaveShiftType.FIXED.getValue()) || (staff.getFixLeaveWeekDay2() == null && staff.getFixLeaveWeekDay() == null)) {
            return 0D;
        }

        Date fromDate = salaryPeriod.getFromDate();
        Date toDate = salaryPeriod.getToDate();

        // Chuyển sang LocalDate để xử lý chính xác ngày
        LocalDate fromLocalDate = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toLocalDate = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Tính tổng số ngày trong khoảng kỳ lương (bao gồm cả ngày cuối)
        long totalDays = ChronoUnit.DAYS.between(fromLocalDate, toLocalDate) + 1;

        // Lấy tổng số ngày nghỉ cố định
        Double holidayCount = 0D;
        if(staff.getFixLeaveWeekDay() != null){
            holidayCount += DateTimeUtil.countWeekdayInRange(staff.getFixLeaveWeekDay(), fromDate, toDate);
        }
        if(staff.getFixLeaveWeekDay2() != null){
            if(staff.getFixLeaveWeekDay() != null && staff.getFixLeaveWeekDay().equals(staff.getFixLeaveWeekDay2())){

            }
            else{
                holidayCount += DateTimeUtil.countWeekdayInRange(staff.getFixLeaveWeekDay2(), fromDate, toDate);
            }
        }

        // Số ngày công chuẩn = Tổng số ngày - số ngày nghỉ
        double workingDays = totalDays - holidayCount;

        return Math.max(workingDays, 0D);
    }

    @Override
    public Double getSoNgayCongChuanByPositionTitle(UUID staffId, SalaryPeriod salaryPeriod) {
        if (staffId == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        List<Position> mainPositions = positionRepository.findMainPositionByStaffId(staffId);
        if (mainPositions == null || mainPositions.isEmpty())
            return 0D;

        Double workingDays = 0D;

        Position mainPosition = mainPositions.get(0);
        PositionTitle positionTitle = mainPosition.getTitle();
        if (positionTitle == null || positionTitle.getWorkDayCalculationType() == null)
            return 0D;

        if (positionTitle.getWorkDayCalculationType()
                .equals(HrConstants.PositionTitleWorkdayCalculationType.FIXED.getValue())) {
            workingDays = mainPositions.get(0).getTitle().getEstimatedWorkingDays();
        } else if (positionTitle.getWorkDayCalculationType()
                .equals(HrConstants.PositionTitleWorkdayCalculationType.CHANGE_BY_PERIOD.getValue())) {
            workingDays = this.getSoNgayCongChuanByFixLeaveWeekDays(staffId, salaryPeriod);
        }

        if (workingDays == null)
            workingDays = 0D;

        return workingDays;
    }

    @Override
    public Double getSoGioCongTieuChuan(UUID staffId, SalaryPeriod salaryPeriod) {
        if (staffId == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        Date fromDate = salaryPeriod.getFromDate();
        Date toDate = salaryPeriod.getToDate();

        // Chuyển sang LocalDate để xử lý chính xác ngày
        LocalDate fromLocalDate = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toLocalDate = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Tính tổng số ngày trong khoảng kỳ lương (bao gồm cả ngày cuối)
        long totalDays = ChronoUnit.DAYS.between(fromLocalDate, toLocalDate) + 1;

        Double leaveHours = publicHolidayDateRepository.getTotalLeaveHoursInRangeTime(fromDate, toDate);
        if (leaveHours == null)
            leaveHours = 0D;

        // Số giờ công chuẩn = Tổng số giờ - số giờ nghỉ
        double workingDays = totalDays * 8 - leaveHours;

        return Math.max(workingDays, 0D);
    }

    @Override
    public Double getNgayCongThucTeDiLam(UUID staffId, SalaryPeriod salaryPeriod) {
        Staff staff = staffRepository.findById(staffId).orElse(null);

        if (staff == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }
        Date officialDate = staff.getStartDate();
        if (officialDate == null) {
            officialDate = handleSetDefaulOfficialDate();
        }
        Double result = 0D;
        // Tổng số ngày nhân viên đã làm việc
        result += staffWorkScheduleRepository.getTotalPaidWork(staffId, salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());
        return result;
    }

    @Override
    public Double getSoNgayVuotCongThuViec(UUID staffId, SalaryPeriod salaryPeriod) {
        Staff staff = staffRepository.findById(staffId).orElse(null);

        if (staff == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        // Nhân viên không tính OT và không tính ngày vượt công
        if (staff.getSkipOvertimeCount() != null && staff.getSkipOvertimeCount().equals(true)) {
            return 0D;
        }

        Date officialDate = staff.getStartDate();
        if (officialDate == null) {
            officialDate = handleSetDefaulOfficialDate();
            officialDate = truncateTime(officialDate);
        }
        Date periodStartDate = truncateTime(salaryPeriod.getFromDate());
        Date periodEndDate = truncateTime(salaryPeriod.getToDate());

        // TH: Ngày thử việc ở trước kỳ lương
        // => Tất cả các ngày trong kỳ lương là ngày công chính thức
        // => Chỉ có ngày vượt công chính thức
        // => Ngày vượt công thử việc = 0
        if (officialDate.before(periodStartDate)) {
            return 0D;
        }
        // TH: Ngày thử việc ở sau kỳ lương
        // => Tất cả các ngày trong kỳ lương là ngày công thử việc
        // => Chỉ có ngày vượt công thử việc
        // => Ngày vượt công thử việc = Ngày công hưởng lương - Ngày công chuẩn theo
        // chức danh
        else if (periodEndDate.before(officialDate)) {
            double ngayCongThucTeDiLam = staffWorkScheduleRepository.getTotalPaidWorkAndLeaveWorkRatio(staffId,
                    salaryPeriod.getFromDate(), salaryPeriod.getToDate());
            double ngayCongChuanTheoChucDanh = convertToDouble(
                    getSoNgayCongChuanByPositionTitle(staffId, salaryPeriod));

            // Ngày vượt công chính thức
            double result = ngayCongThucTeDiLam - ngayCongChuanTheoChucDanh;

            if (result < 0)
                result = 0D;
            return result;
        }
        // TH: Ngày thử việc là 1 ngày trong kỳ lương
        // => Có cả ngày công chính thức và ngày công thử việc
        // => Có cả ngày vượt công chính thức và thử việc
        // => Được tính ngày vượt công chính thức
        // => Ngày vượt công thử việc = 0
        else {
            return 0D;
        }
    }

    @Override
    public Double getSoNgayCongHuongLuongThuViec(UUID staffId, SalaryPeriod salaryPeriod) {
        Staff staff = staffRepository.findById(staffId).orElse(null);

        if (staff == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        Date officialDate = staff.getStartDate();
        if (officialDate == null) {
            officialDate = handleSetDefaulOfficialDate();
        }

        Double result = 0D;
        // Tổng số ngày công làm việc thử việc
        result += staffWorkScheduleRepository.getTotalProbationPaidWork(staffId, officialDate,
                salaryPeriod.getFromDate(), salaryPeriod.getToDate());
        // Công thêm các ngày nghỉ được hưởng lương
        result += staffWorkScheduleRepository.getTotalProbationPaidLeaveWork(staffId, officialDate,
                salaryPeriod.getFromDate(), salaryPeriod.getToDate());

        // Trừ đi các ngày vượt công thử việc
        result -= this.getSoNgayVuotCongThuViec(staffId, salaryPeriod);

        if (result < 0)
            result = 0D;

        return result;
    }

    @Override
    public Double getSoNgayVuotCongChinhThuc(UUID staffId, SalaryPeriod salaryPeriod) {
        Staff staff = staffRepository.findById(staffId).orElse(null);

        if (staff == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        // Nhân viên không tính OT và không tính ngày vượt công
        if (staff.getSkipOvertimeCount() != null && staff.getSkipOvertimeCount().equals(true)) {
            return 0D;
        }

        Date officialDate = staff.getStartDate();
        if (officialDate == null) {
            officialDate = handleSetDefaulOfficialDate();
            officialDate = truncateTime(officialDate);
        }
        Date periodStartDate = truncateTime(salaryPeriod.getFromDate());
        Date periodEndDate = truncateTime(salaryPeriod.getToDate());

        // TH: Ngày thử việc ở trước kỳ lương
        // => Tất cả các ngày trong kỳ lương là ngày công chính thức
        // => Chỉ có ngày vượt công chính thức
        // => Ngày vượt công chính thức = Ngày công hưởng lương - Ngày công chuẩn theo
        // chức danh
        if (officialDate.before(periodStartDate)) {
            double ngayCongThucTeDiLam = staffWorkScheduleRepository.getTotalPaidWorkAndLeaveWorkRatio(staffId,
                    salaryPeriod.getFromDate(), salaryPeriod.getToDate());
            double ngayCongChuanTheoChucDanh = convertToDouble(
                    getSoNgayCongChuanByPositionTitle(staffId, salaryPeriod));

            // Ngày vượt công chính thức
            double result = ngayCongThucTeDiLam - ngayCongChuanTheoChucDanh;

            if (result < 0)
                result = 0D;
            return result;
        }
        // TH: Ngày thử việc ở sau kỳ lương
        // => Tất cả các ngày trong kỳ lương là ngày công thử việc
        // => Chỉ có ngày vượt công thử việc
        // => Ngày vượt công chính thức = 0
        else if (periodEndDate.before(officialDate)) {
            return 0D;
        }
        // TH: Ngày thử việc là 1 ngày trong kỳ lương
        // => Có cả ngày công chính thức và ngày công thử việc
        // => Có cả ngày vượt công chính thức và thử việc
        // => Ngày vượt công chính thức = Ngày công hưởng lương - Ngày công chuẩn theo
        // chức danh
        else {
            double ngayCongThucTeDiLam = staffWorkScheduleRepository.getTotalPaidWorkAndLeaveWorkRatio(staffId,
                    salaryPeriod.getFromDate(), salaryPeriod.getToDate())
                    + staffWorkScheduleRepository.getTotalPaidWorkByLeaveTypeOfStaff(staffId, null, periodStartDate,
                    periodEndDate);
            double ngayCongChuanTheoChucDanh = convertToDouble(
                    getSoNgayCongChuanByPositionTitle(staffId, salaryPeriod));

            // Ngày vượt công chính thức
            double result = ngayCongThucTeDiLam - ngayCongChuanTheoChucDanh;

            if (result < 0)
                result = 0D;
            return result;
        }
    }

    public static Date truncateTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Override
    public Double getSoNgayCongHuongLuongChinhThuc(UUID staffId, SalaryPeriod salaryPeriod) {
        Staff staff = staffRepository.findById(staffId).orElse(null);

        if (staff == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        Date officialDate = staff.getStartDate();
        if (officialDate == null) {
            officialDate = handleSetDefaulOfficialDate();
        }

        Double result = 0D;
        // Tổng số ngày công làm việc chính thức
        result += staffWorkScheduleRepository.getTotalOfficialStaffPaidWork(staffId, officialDate,
                salaryPeriod.getFromDate(), salaryPeriod.getToDate());
        // Cộng thêm các ngày công nghỉ hưởng lương chính thức
        result += staffWorkScheduleRepository.getTotalOfficialStaffPaidLeaveWork(staffId, officialDate,
                salaryPeriod.getFromDate(), salaryPeriod.getToDate());

        // Trừ đi số ngày vượt công chính thức
        result -= this.getSoNgayVuotCongChinhThuc(staffId, salaryPeriod);

        if (result < 0)
            result = 0D;

        return result;
    }

    @Override
    public Double getTamUng(UUID staffId, SalaryPeriod salaryPeriod) {
        Staff staff = staffRepository.findById(staffId).orElse(null);

        if (staff == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        List<StaffAdvancePayment> availableResults = staffAdvancePaymentRepository
                .findByStaffIdAndSalaryPeriodIdAndApprovalStatus(staff.getId(), salaryPeriod.getId(),
                        HrConstants.StaffAdvancePaymentApprovalStatus.APPROVED.getValue());

        if (availableResults == null || availableResults.isEmpty()) {
            return 0D;
        }

        Double totalAdvanceAmount = 0.0;
        for (StaffAdvancePayment advancePayment : availableResults) {
            totalAdvanceAmount += advancePayment.getAdvancedAmount();
        }

        return totalAdvanceAmount;
    }

    @Override
    public Double getSoNguoiPhuThuocThue(UUID staffId, SalaryPeriod salaryPeriod) {
        if (staffId == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        long countResult = staffFamilyRelationshipRepository.countDependentRelationshipOfStaff(staffId);

        return (double) countResult;
    }

    @Override
    public Double getNgayCongTinhLuongThue(UUID staffId, SalaryPeriod salaryPeriod) {
        if (staffId == null || salaryPeriod == null || salaryPeriod.getFromDate() == null
                || salaryPeriod.getToDate() == null) {
            return 0D;
        }

        Double result = 0D;

        // Tổng số ngày nhân viên đã làm việc
        result += staffWorkScheduleRepository.getTotalPaidWork(staffId, salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        // CÁC NGÀY NGHỈ ĐƯỢC HƯỞNG LƯƠNG
        // Nghỉ phép
        result += staffWorkScheduleRepository.getTotalPaidWorkByLeaveTypeOfStaff(staffId,
                HrConstants.LeaveTypeCode.ANNUAL_LEAVE.getCode(), salaryPeriod.getFromDate(), salaryPeriod.getToDate());
        // Nghỉ công tác
        result += staffWorkScheduleRepository.getTotalPaidWorkByLeaveTypeOfStaff(staffId,
                HrConstants.LeaveTypeCode.BUSINESS_TRIP.getCode(), salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());
        // Nghỉ lễ
        result += staffWorkScheduleRepository.getTotalPaidWorkByLeaveTypeOfStaff(staffId,
                HrConstants.LeaveTypeCode.PUBLIC_HOLIDAY.getCode(), salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());
        // Nghỉ bù
        result += staffWorkScheduleRepository.getTotalPaidWorkByLeaveTypeOfStaff(staffId,
                HrConstants.LeaveTypeCode.COMPENSATORY_LEAVE.getCode(), salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());
        // Nghỉ chế độ
        result += staffWorkScheduleRepository.getTotalPaidWorkByLeaveTypeOfStaff(staffId,
                HrConstants.LeaveTypeCode.SPECIAL_LEAVE.getCode(), salaryPeriod.getFromDate(),
                salaryPeriod.getToDate());

        return result;
    }

    private Double getBHXHNV(UUID staffId, SalaryPeriod salaryPeriod) {
        return 0D;
    }

    private Double getBHYTNV(UUID staffId, SalaryPeriod salaryPeriod) {
        return 0D;
    }

    private Double getBHTNNV(UUID staffId, SalaryPeriod salaryPeriod) {
        return 0D;
    }

    private Double getKPCDNV(UUID staffId, SalaryPeriod salaryPeriod) {
        return 0D;
    }

    private Double getBHXHCT(UUID staffId, SalaryPeriod salaryPeriod) {
        return 0D;
    }

    private Double getBHYTCT(UUID staffId, SalaryPeriod salaryPeriod) {
        return 0D;
    }

    private Double getBHTNCT(UUID staffId, SalaryPeriod salaryPeriod) {
        return 0D;
    }

    private Double getKPCDCT(UUID staffId, SalaryPeriod salaryPeriod) {
        return 0D;
    }

    @Override
    public Double getThuNhapKhac(UUID staffId, SalaryPeriod salaryPeriod) {
        List<OtherIncome> otherIncomes = otherIncomeRepository.findByIncomeTypeStaffIdAndPeriodId(
                HrConstants.OtherIncomeType.INCOME.getValue(), staffId, salaryPeriod.getId());

        if (otherIncomes == null || otherIncomes.isEmpty())
            return 0.0;

        double result = 0D;

        for (OtherIncome otherIncome : otherIncomes) {
            double itemValue = otherIncome.getIncome();
            result += itemValue;
        }

        return result;
    }

    @Override
    public Double getTrichTruKhac(UUID staffId, SalaryPeriod salaryPeriod) {
        List<OtherIncome> otherIncomes = otherIncomeRepository.findByIncomeTypeStaffIdAndPeriodId(
                HrConstants.OtherIncomeType.DEDUCTION.getValue(), staffId, salaryPeriod.getId());

        if (otherIncomes == null || otherIncomes.isEmpty())
            return 0.0;

        double result = 0D;

        for (OtherIncome otherIncome : otherIncomes) {
            double itemValue = otherIncome.getIncome();
            result += itemValue;
        }

        return result;
    }
}
