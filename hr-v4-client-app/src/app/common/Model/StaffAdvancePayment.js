import LocalConstants from "app/LocalConstants";

export class StaffAdvancePayment {
    id = null;
    staff = null; // Nhân viên xin ứng truước
    salaryPeriod = null; // Kỳ lương xin ứng
    requestDate = null; // Ngày xin ứng tiền
    requestReason = null; // Lý do tạm ứng tiền
    advancedAmount = null; // Số tiền ứng trước
    approvalStatus = LocalConstants.StaffAdvancePaymentApprovalStatus.NOT_APPROVED_YET.value; // Trạng thái xác nhận. Chi tiết trong: HrConstants.StaffAdvancePaymentApprovalStatus
}