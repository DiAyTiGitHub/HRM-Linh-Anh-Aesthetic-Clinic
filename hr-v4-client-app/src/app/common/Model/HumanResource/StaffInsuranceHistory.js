export class StaffInsuranceHistory {
    id = null;

    staff = null; // Lịch sử của nhân viên nào
    startDate = null;//Ngày bắt đầu mức đóng
    endDate = null;//Ngày kết thúc mức đóng
    note = null;//Ghi chú
    insuranceSalary = null;//Mức lương đóng bảo hiểm xã hội
    staffPercentage = null;//Tỷ lệ cá nhân đóng bảo hiểm xã hội
    orgPercentage = null;//Tỷ lệ đơn vị đóng bảo hiểm xã hội
    staffInsuranceAmount = null;//Số tiền cá nhân đóng
    orgInsuranceAmount = null;//Số tiền đơn vị đóng
    socialInsuranceBookCode = null;//Số sổ bảo hiểm xã hội

    constructor() {
        this.insuranceSalary = 0.0;
        this.staffInsuranceAmount = 0.0;
        this.orgInsuranceAmount = 0.0;
        this.staffPercentage = 10.5;
        this.orgPercentage = 21.5;
    }
}