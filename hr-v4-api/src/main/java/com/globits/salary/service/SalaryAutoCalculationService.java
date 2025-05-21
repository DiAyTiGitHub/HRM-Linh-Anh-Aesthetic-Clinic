package com.globits.salary.service;

import com.globits.hr.HrConstants;
import com.globits.hr.domain.Staff;
import com.globits.hr.domain.StaffWorkSchedule;
import com.globits.salary.domain.SalaryPeriod;
import com.globits.salary.domain.StaffAdvancePayment;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface SalaryAutoCalculationService {
    Double detectConstantsAndGetValue(String salaryTemplateItemCode, SalaryPeriod salaryPeriod, UUID staffId);

    boolean isAutoConnectionCode(String code);

    Double getCoDongBHXH(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoGioCongTieuChuan(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoGioOTDuocXacNhan(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoGioLamViecHopLe(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoGioTangCaThuViec(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoGioTangCaChinhThuc(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoPhutTreSomThuViec(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoPhutTreSomChinhThuc(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoNgayCongChuan(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoNgayCongChuanByFixLeaveWeekDays(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoNgayCongChuanByPublicHolidayDate(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoNgayCongChuanByPositionTitle(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoNgayCongHuongLuongThuViec(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoNgayVuotCongThuViec(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoNgayCongHuongLuongChinhThuc(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoNgayVuotCongChinhThuc(UUID staffId, SalaryPeriod salaryPeriod);

    Double getTamUng(UUID staffId, SalaryPeriod salaryPeriod);

    Double getNgayCongTinhLuongThue(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoNguoiPhuThuocThue(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoGioLamViecDuocPhan(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoGioLamViecThucTe(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoGioLamThemTruocCa(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoGioLamThemSauCa(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoGioLamViecCongQuyDoi(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoCaDuocPhan(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoCaDiLamDu(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoCaDiLamThieu(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoCaKhongDiLam(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoCaCongTac(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoCaNghiBu(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoCaNghiCheDo(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoCaNghiKhongLuong(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoCaNghiLe(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoCaNghiPhep(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoLanDiMuon(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoLanVeSom(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoPhutDiMuon(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoPhutVeSom(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoPhutDiSom(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoPhutVeMuon(UUID staffId, SalaryPeriod salaryPeriod);

    Double getSoCongDuocTinh(UUID staffId, SalaryPeriod salaryPeriod);

    Double getNgayCongThucTeDiLam(UUID staffId, SalaryPeriod salaryPeriod);

    Double getThuNhapKhac(UUID staffId, SalaryPeriod salaryPeriod);

    Double getTrichTruKhac(UUID staffId, SalaryPeriod salaryPeriod);


}
