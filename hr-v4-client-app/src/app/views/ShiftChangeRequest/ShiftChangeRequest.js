import LocalConstants from "app/LocalConstants";

export class ShiftChangeRequest {
    id = null;

    fromShiftWork = null; // ca làm việc cần thay đổi
    fromWorkingDate = null;// ngày làm việc cần thay đổi
    toShiftWork = null;  // ca làm việc được yêu cầu đổi
    toWorkingDate = null; // ngày làm việc được yêu cầu đổi
    registerStaff = null; // nhân viên yêu cầu đổi ca
    requestDate = null; // ngày tạo yêu cầu đổi ca
    requestReason = null; // Lý do yêu cầu
    approvalStatus = LocalConstants.ShiftChangeRequestApprovalStatus.NOT_APPROVED_YET.value; // trang thái: HrConstants.RecruitmentRequestStatus

    constructor() {
        this.requestDate = new Date();
        this.approvalStatus = LocalConstants.ShiftChangeRequestApprovalStatus.NOT_APPROVED_YET.value;
    }
}