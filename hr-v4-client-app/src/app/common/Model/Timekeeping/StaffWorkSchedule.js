import LocalConstants from "app/LocalConstants";

export class StaffWorkSchedule {
  id = null;
  staff = null;
  shiftWork = null;
  workingDate = null;
  totalHours = null; // Tổng số giờ nhân viên đã làm việc của ca này
  overtimeHours = null; // Thời gian đăng ký làm thêm. Có giá trị khi: workingType = EXTENDED_OVERTIME (Tăng ca kéo dài)
  workingType = null; // Loại làm việc. Chi tiết: HrConstants.StaffWorkScheduleWorkingType
  workingStatus = null; // Trạng thái làm việc của nhân viên đối với ca làm việc. Chi tiết: HrConstants.StaffWorkScheduleWorkingStatus
  paidWorkStatus = null; // Trạng thái tính công. Chi tiết: HrConstants.PaidWorkStatus
  lateArrivalCount = null; // Số lần đi làm muộn
  lateArrivalMinutes = null; // Số phút đi muộn
  earlyExitCount = null; // Số lần về sớm
  earlyExitMinutes = null; // Số phút về sớm
  earlyArrivalMinutes = null; // Số phút đến sớm
  lateExitMinutes = null; // Số phút về muộn
  totalPaidWork = null; // công được tính của nhân viên. VD: 0.5 ngày công, 1 ngày công
  allowOneEntryOnly = null;
  convertedWorkingHours = null; // Số giờ công quy đổi của nhân viên
  otEndorser = null; // Người xác nhận OT cho nhân viên
  confirmedOTHoursBeforeShift = null; // Số giờ làm thêm trước ca làm việc đã được xác nhận
  confirmedOTHoursAfterShift = null; // Số giờ làm thêm sau ca làm việc đã được xác nhận
  coordinator = null; // Người phân ca làm việc
  leaveType = null; // Loại nghỉ. Có giá trị khi workingStatus = NOT_ATTENDANCE (không đi làm)

  firstCheckIn = null; // Lần checkin đầu tiên (được lấy từ timesheetDetail)
  lastCheckout = null; // Lần checkout cuối cùng (được lấy từ timesheetDetail)

  timeSheetDetails = null; // Lịch sử chấm công

  earlyArrivalHours = null; // Số giờ đi sớm
  lateExitHours = null; // Số giờ về muộn

  timekeepingCalculationType = null;

  // Ca làm việc này cần người quản lý phê duyệt
  needManagerApproval = null;

  // Trường dưới có giá trị khi needManagerApproval = true
  approvalStatus = null;   // Trạng thái phê duyệt kết quả làm việc. Chi tiết: HrConstants.StaffWorkScheduleApprovalStatus
  paidLeaveWorkRatio = 0.0; // Công nghỉ phép có tính lương của nhân viên. VD: Nghỉ phép 0.5 ngày công. Tính theo paidLeaveHours
  unpaidLeaveWorkRatio = 0.0; // Công nghỉ phép KHÔNG tính lương của nhân viên. VD: Nghỉ KHÔNG phép 0.5 ngày công. Tính theo unpaidLeaveHours
  totalValidHours = 0.0;
  leavePeriod = null;
  paidLeaveHours = 0.00;
  unpaidLeaveHours = 0.00;
  getTotalConfirmedOTHours = 0.00;
  totalConfirmedOTHours = 0.00;
  isLocked = false;
  duringPregnancy = null;

  constructor () {
    this.workingDate = new Date ();
    this.timekeepingCalculationType = LocalConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.value;
    this.workingType = LocalConstants.StaffWorkScheduleWorkingType.NORMAL_WORK.value;
    // this.workingStatus = LocalConstants.StaffWorkScheduleWorkingStatus.LEAVE_WITHOUT_PERMISSION.value;

    // this.confirmedOTHoursAfterShift = 0;
    // this.confirmedOTHoursBeforeShift = 0;
    this.overtimeHours = 0.0;

    this.needManagerApproval = false;
    this.allowOneEntryOnly = true;
  }
}
