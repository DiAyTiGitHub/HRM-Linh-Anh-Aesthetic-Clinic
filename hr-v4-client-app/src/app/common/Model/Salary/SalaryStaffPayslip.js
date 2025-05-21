export class SalaryStaffPayslip {
    id = null;
    salaryResult = null;
    staff = null;
    salaryPeriod = null;
    // Giá trị của từng thành phần lương của nhân viên trong bảng lương => Gía trị của từng cell trong dòng dữ liệu
    salaryResultStaffItems = null;
    note = null;
    approvalStatus = null; // Trạng thái duyệt phiếu lương. Chi tiết trong: HrConstants.SalaryResulStaffApprovalStatus
    paidStatus = null;
    isLocked = null; // Phiếu lương đã bị khóa hay chưa (phụ thuộc vào bảng lương đã bị khóa hay chưa)

    constructor() {

    }
}