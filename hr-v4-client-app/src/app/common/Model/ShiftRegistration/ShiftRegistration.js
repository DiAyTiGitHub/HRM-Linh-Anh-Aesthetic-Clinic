import LocalConstants from "app/LocalConstants";

export class ShiftRegistration {
    id = null;
    registerStaff = null;
    shiftWork = null;
    workingDate = null;
    approvalStaff = null;
    approvalStatus = LocalConstants.ShiftRegistrationApprovalStatus.NOT_APPROVED_YET.value;
    organizationId = null;
    workingType = null; // Loại làm việc. Chi tiết: HrConstants.StaffWorkScheduleWorkingType
    overtimeHours = null; // Thời gian đăng ký làm thêm. Có giá trị khi: workingType = EXTENDED_OVERTIME (Tăng ca kéo dài)

    constructor() {
        this.status = null;
        this.workingType = LocalConstants.StaffWorkScheduleWorkingType.NORMAL_WORK.value;
        this.overtimeHours = 0.0;
    }
}