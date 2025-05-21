import LocalConstants from "app/LocalConstants";

export class LeaveRequest {
    id = null;
    requestStaff = null;
    requestDate = null;
    fromDate = null;
    toDate = null;
    totalDays = null;
    totalHours = null;
    approvalStatus = LocalConstants.LeaveRequestApprovalStatus.NOT_APPROVED_YET.value; // trang thái: HrConstants.RecruitmentRequestStatus
    requestReason = null;
    approvalStaff = null;

    constructor() {
        this.requestDate = new Date();
        this.approvalStatus = LocalConstants.LeaveRequestApprovalStatus.NOT_APPROVED_YET.value; // trang thái: HrConstants.RecruitmentRequestStatus
    }
}