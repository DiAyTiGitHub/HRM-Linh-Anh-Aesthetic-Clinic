import LocalConstants from "app/LocalConstants";

export class OvertimeRequest {
    id = null;
    staffWorkSchedule = null;
    dateRequest = null; 
    approvalStatus = LocalConstants.OvertimeRequestApprovalStatus.NOT_APPROVED_YET.value;
    staff = null;
    requestOTHoursBeforeShift = null;
    requestOTHoursAfterShift = null;

    constructor() {
        this.approvalStatus = LocalConstants.OvertimeRequestApprovalStatus.NOT_APPROVED_YET.value; // trang thái: HrConstants.OvertimeRequestApprovalStatus

    }
}