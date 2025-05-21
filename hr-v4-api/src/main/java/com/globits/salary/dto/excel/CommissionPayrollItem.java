package com.globits.salary.dto.excel;

import java.util.List;

public class CommissionPayrollItem {
    // 0. No
    private String order;
    // 1. Empl. Code
    private String staffCode;
    // 2. Vietnamese Name
    private String displayName;
    // 4. Position
    private String positionTitle;
    // 5. BAN/PHÒNG/CƠ SỞ
    private String department;
    // 6. TARGET NHẬN
    private List<CommissionPayrollItemDetail> targetNhan;
    private Double targetNhanSum;

    // 7. DOANH SỐ ĐẠT ĐƯỢC
    private List<CommissionPayrollItemDetail> doanhSoDatDuoc;
    private Double doanhSoDatDuocSum;

    // 8. TG KHÁCH MỚI
    private List<CommissionPayrollItemDetail> tgKhachMoi;
    private Double tgKhachMoiSum;

    // 9. DS KHÁCH MỚI
    private List<CommissionPayrollItemDetail> dsKhachMoi;
    private Double dsKhachMoiSum;

    // 10. TG KHÁCH CŨ
    private List<CommissionPayrollItemDetail> tgKhachCu;
    private Double tgKhachCuSum;

    // 11. DS KHÁCH CŨ
    private List<CommissionPayrollItemDetail> dsKhachCu;
    private Double dsKhachCuSum;

    // 12. DS VƯỢT
    private List<CommissionPayrollItemDetail> dsVuot;
    private Double dsVuotSum;

    // 13. CÁCH TÍNH %
    private List<CommissionPayrollItemDetail> cachTinhPT;
    private Double cachTinhPTSum;

    // 14. LƯƠNG KPI
    private List<CommissionPayrollItemDetail> luongKPI;
    private Double luongKPISum;

    // 15. TIỀN TOUR
    private List<CommissionPayrollItemDetail> tienTour;
    private Double tienTourSum;

    // 16. TRÁCH NHIỆM
    private List<CommissionPayrollItemDetail> trachNhiem;
    private Double trachNhiemSum;

    // 17. THƯỞNG
    private List<CommissionPayrollItemDetail> thuong;
    private Double thuongSum;

    // 18. TRỪ KHÁC
    private List<CommissionPayrollItemDetail> truKhac;
    private Double truKhacSum;

    // 19. LƯƠNG BỔ SUNG
    private List<CommissionPayrollItemDetail> luongBoSung;
    private Double luongBoSungSum;

    // 20. LƯƠNG THỰC LĨNH
    private List<CommissionPayrollItemDetail> luongThucLinh;
    private Double luongThucLinhSum;

    // 21. Ghi chú
    private String note;
    // 22. EMAIL
    private String email;
    // 23. PASS
    private String password;
    // 24. (Empty/Skipped index, possibly used for formatting)
    private String blankCell;
    // 25. SỐ TÀI KHOẢN
    private String bankAccount;
    // 26. NGÂN HÀNG
    private String bank;

    // Số dòng dữ liệu hỗ trợ in trong excel
    private Integer maximumPlus;


    public Double getTargetNhanSum() {
        return targetNhanSum;
    }

    public void setTargetNhanSum(Double targetNhanSum) {
        this.targetNhanSum = targetNhanSum;
    }

    public Double getDoanhSoDatDuocSum() {
        return doanhSoDatDuocSum;
    }

    public void setDoanhSoDatDuocSum(Double doanhSoDatDuocSum) {
        this.doanhSoDatDuocSum = doanhSoDatDuocSum;
    }

    public Double getTgKhachMoiSum() {
        return tgKhachMoiSum;
    }

    public void setTgKhachMoiSum(Double tgKhachMoiSum) {
        this.tgKhachMoiSum = tgKhachMoiSum;
    }

    public Double getDsKhachMoiSum() {
        return dsKhachMoiSum;
    }

    public void setDsKhachMoiSum(Double dsKhachMoiSum) {
        this.dsKhachMoiSum = dsKhachMoiSum;
    }

    public Double getTgKhachCuSum() {
        return tgKhachCuSum;
    }

    public void setTgKhachCuSum(Double tgKhachCuSum) {
        this.tgKhachCuSum = tgKhachCuSum;
    }

    public Double getDsKhachCuSum() {
        return dsKhachCuSum;
    }

    public void setDsKhachCuSum(Double dsKhachCuSum) {
        this.dsKhachCuSum = dsKhachCuSum;
    }

    public Double getDsVuotSum() {
        return dsVuotSum;
    }

    public void setDsVuotSum(Double dsVuotSum) {
        this.dsVuotSum = dsVuotSum;
    }

    public Double getCachTinhPTSum() {
        return cachTinhPTSum;
    }

    public void setCachTinhPTSum(Double cachTinhPTSum) {
        this.cachTinhPTSum = cachTinhPTSum;
    }

    public Double getLuongKPISum() {
        return luongKPISum;
    }

    public void setLuongKPISum(Double luongKPISum) {
        this.luongKPISum = luongKPISum;
    }

    public Double getTienTourSum() {
        return tienTourSum;
    }

    public void setTienTourSum(Double tienTourSum) {
        this.tienTourSum = tienTourSum;
    }

    public Double getTrachNhiemSum() {
        return trachNhiemSum;
    }

    public void setTrachNhiemSum(Double trachNhiemSum) {
        this.trachNhiemSum = trachNhiemSum;
    }

    public Double getThuongSum() {
        return thuongSum;
    }

    public void setThuongSum(Double thuongSum) {
        this.thuongSum = thuongSum;
    }

    public Double getTruKhacSum() {
        return truKhacSum;
    }

    public void setTruKhacSum(Double truKhacSum) {
        this.truKhacSum = truKhacSum;
    }

    public Double getLuongBoSungSum() {
        return luongBoSungSum;
    }

    public void setLuongBoSungSum(Double luongBoSungSum) {
        this.luongBoSungSum = luongBoSungSum;
    }

    public Double getLuongThucLinhSum() {
        return luongThucLinhSum;
    }

    public void setLuongThucLinhSum(Double luongThucLinhSum) {
        this.luongThucLinhSum = luongThucLinhSum;
    }

    public Integer getMaximumPlus() {
        return maximumPlus;
    }

    public void setMaximumPlus(Integer maximumPlus) {
        this.maximumPlus = maximumPlus;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPositionTitle() {
        return positionTitle;
    }

    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<CommissionPayrollItemDetail> getTargetNhan() {
        return targetNhan;
    }

    public void setTargetNhan(List<CommissionPayrollItemDetail> targetNhan) {
        this.targetNhan = targetNhan;
    }

    public List<CommissionPayrollItemDetail> getDoanhSoDatDuoc() {
        return doanhSoDatDuoc;
    }

    public void setDoanhSoDatDuoc(List<CommissionPayrollItemDetail> doanhSoDatDuoc) {
        this.doanhSoDatDuoc = doanhSoDatDuoc;
    }

    public List<CommissionPayrollItemDetail> getTgKhachMoi() {
        return tgKhachMoi;
    }

    public void setTgKhachMoi(List<CommissionPayrollItemDetail> tgKhachMoi) {
        this.tgKhachMoi = tgKhachMoi;
    }

    public List<CommissionPayrollItemDetail> getDsKhachMoi() {
        return dsKhachMoi;
    }

    public void setDsKhachMoi(List<CommissionPayrollItemDetail> dsKhachMoi) {
        this.dsKhachMoi = dsKhachMoi;
    }

    public List<CommissionPayrollItemDetail> getTgKhachCu() {
        return tgKhachCu;
    }

    public void setTgKhachCu(List<CommissionPayrollItemDetail> tgKhachCu) {
        this.tgKhachCu = tgKhachCu;
    }

    public List<CommissionPayrollItemDetail> getDsKhachCu() {
        return dsKhachCu;
    }

    public void setDsKhachCu(List<CommissionPayrollItemDetail> dsKhachCu) {
        this.dsKhachCu = dsKhachCu;
    }

    public List<CommissionPayrollItemDetail> getDsVuot() {
        return dsVuot;
    }

    public void setDsVuot(List<CommissionPayrollItemDetail> dsVuot) {
        this.dsVuot = dsVuot;
    }

    public List<CommissionPayrollItemDetail> getCachTinhPT() {
        return cachTinhPT;
    }

    public void setCachTinhPT(List<CommissionPayrollItemDetail> cachTinhPT) {
        this.cachTinhPT = cachTinhPT;
    }

    public List<CommissionPayrollItemDetail> getLuongKPI() {
        return luongKPI;
    }

    public void setLuongKPI(List<CommissionPayrollItemDetail> luongKPI) {
        this.luongKPI = luongKPI;
    }

    public List<CommissionPayrollItemDetail> getTienTour() {
        return tienTour;
    }

    public void setTienTour(List<CommissionPayrollItemDetail> tienTour) {
        this.tienTour = tienTour;
    }

    public List<CommissionPayrollItemDetail> getTrachNhiem() {
        return trachNhiem;
    }

    public void setTrachNhiem(List<CommissionPayrollItemDetail> trachNhiem) {
        this.trachNhiem = trachNhiem;
    }

    public List<CommissionPayrollItemDetail> getThuong() {
        return thuong;
    }

    public void setThuong(List<CommissionPayrollItemDetail> thuong) {
        this.thuong = thuong;
    }

    public List<CommissionPayrollItemDetail> getTruKhac() {
        return truKhac;
    }

    public void setTruKhac(List<CommissionPayrollItemDetail> truKhac) {
        this.truKhac = truKhac;
    }

    public List<CommissionPayrollItemDetail> getLuongBoSung() {
        return luongBoSung;
    }

    public void setLuongBoSung(List<CommissionPayrollItemDetail> luongBoSung) {
        this.luongBoSung = luongBoSung;
    }

    public List<CommissionPayrollItemDetail> getLuongThucLinh() {
        return luongThucLinh;
    }

    public void setLuongThucLinh(List<CommissionPayrollItemDetail> luongThucLinh) {
        this.luongThucLinh = luongThucLinh;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBlankCell() {
        return blankCell;
    }

    public void setBlankCell(String blankCell) {
        this.blankCell = blankCell;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }
}
