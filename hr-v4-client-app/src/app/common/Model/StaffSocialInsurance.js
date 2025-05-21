export class StaffSocialInsurance {
    id = null;

    staff = null;

    insuranceSalary = null; // Mức lương đóng bảo hiểm xã hội

    // ===== Nhân viên đóng =====
    staffSocialInsurancePercentage = null; // Tỷ lệ đóng BHXH của nhân viên
    staffSocialInsuranceAmount = null;     // Số tiền BHXH nhân viên đóng

    staffHealthInsurancePercentage = null; // Tỷ lệ đóng BHYT của nhân viên
    staffHealthInsuranceAmount = null;     // Số tiền BHYT nhân viên đóng

    staffUnemploymentInsurancePercentage = null; // Tỷ lệ đóng BHTN của nhân viên
    staffUnemploymentInsuranceAmount = null;     // Số tiền BHTN nhân viên đóng

    staffTotalInsuranceAmount = null; // Tổng tiền bảo hiểm mà nhân viên đóng

    // ===== Công ty đóng =====
    orgSocialInsurancePercentage = null; // Tỷ lệ đóng BHXH của công ty
    orgSocialInsuranceAmount = null;     // Số tiền BHXH công ty đóng

    orgHealthInsurancePercentage = null; // Tỷ lệ đóng BHYT của công ty
    orgHealthInsuranceAmount = null;     // Số tiền BHYT công ty đóng

    orgUnemploymentInsurancePercentage = null; // Tỷ lệ đóng BHTN của công ty
    orgUnemploymentInsuranceAmount = null;     // Số tiền BHTN công ty đóng

    orgTotalInsuranceAmount = null; // Tổng tiền bảo hiểm mà công ty đóng

    totalInsuranceAmount = null; // Tổng tiền bảo hiểm mà nhân viên và công ty đóng

    salaryPeriod = null; // Kỳ lương

    salaryResult = null; // Kết quả lương

    paidStatus = null; // Bảo hiểm này đã được thanh toán hay chưa (HrConstants.StaffSocialInsurancePaidStatus)

    note = null; // Ghi chú

    startDate = new Date(); // Ngày bắt đầu mức đóng

    endDate = null; // Ngày kết thúc mức đóng

    constructor() {
        this.staffSocialInsurancePercentage = 8;
        this.staffHealthInsurancePercentage = 1.5;
        this.staffUnemploymentInsurancePercentage = 1;
        this.orgSocialInsurancePercentage = 17.5;
        this.orgHealthInsurancePercentage = 3;
        this.orgUnemploymentInsurancePercentage = 1;
    }
}

