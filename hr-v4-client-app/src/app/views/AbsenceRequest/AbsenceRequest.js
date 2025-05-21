import LocalConstants from "app/LocalConstants";

export class AbsenceRequest {
    id = null;
    workSchedule = null;
    description = null; // mo ta cong viec
    dateRequest = null; // ngay yeu cau
    absenceReason = null;
    approvalStatus = LocalConstants.AbsenceRequestApprovalStatus.NOT_APPROVED_YET.value; // trang thái: HrConstants.RecruitmentRequestStatus
    staff = null;
    absenceType = null; // Loại nghỉ phép. Chi tiết: HrConstants.AbsenceRequestType

    constructor() {
        this.absenceType = LocalConstants.AbsenceRequestType.PAID_LEAVE.value;
        this.approvalStatus = LocalConstants.AbsenceRequestApprovalStatus.NOT_APPROVED_YET.value; // trang thái: HrConstants.RecruitmentRequestStatus

    }
}