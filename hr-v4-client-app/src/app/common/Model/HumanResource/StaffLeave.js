export class StaffLeave {
    id = null;
    staff = null;
    staffId = null;

    decisionNumber = null; // Số quyết định nghỉ việc
    leaveDate = null; // Ngày nghỉ việc
    stillInDebt = null; // Vẫn còn nợ. VD: Không / Chưa trả máy tính / Nợ tiền thuế
    handleOverItems = null; // Các hạng mục bàn giao
    paidStatus = null; // Trạng thái chi trả phiếu lương. Chi tiết trong: HrConstants.SalaryResulStaffPaidStatus

    constructor() {

    }
}