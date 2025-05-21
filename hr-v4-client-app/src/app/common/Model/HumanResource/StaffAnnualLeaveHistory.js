export class StaffAnnualLeaveHistory {
    id = null;
    staff = null; // Nhân viên nào nghỉ phép
    staffId = null;
    year = null; // Năm thống kê nghỉ phép
    grantedLeaveDays = null; // Số ngày nghỉ phép được cấp trong năm
    grantedLeaveDaysNote = null; // ghi chú
    carriedOverLeaveDays = null; // Số ngày nghỉ phép được chuyển từ năm trước
    carriedOverLeaveDaysNote = null; // ghi chú
    seniorityLeaveDays = null; // Số ngày nghỉ phép tăng theo thâm niên
    seniorityLeaveDaysNote = null; // ghi chú
    bonusLeaveDays = null; // Số ngày nghỉ phép được thưởng khác
    bonusLeaveDaysNote = null; // ghi chú
    cancelledLeaveDays = null; // Số ngày nghỉ phép bị hủy/không được dùng
    cancelledLeaveDaysNote = null; // ghi chú
    totalUsedLeaveDays = null; // Tổng số ngày nghỉ phép đã sử dụng

    // Thống kê số ngày đã nghỉ theo từng tháng
    monthlyLeaveHistories = [];

    constructor() {
        this.grantedLeaveDays = 0.0; // Số ngày nghỉ phép được cấp trong năm
        this.carriedOverLeaveDays = 0.0; // Số ngày nghỉ phép được chuyển từ năm trước
        this.seniorityLeaveDays = 0.0; // Số ngày nghỉ phép tăng theo thâm niên
        this.bonusLeaveDays = 0.0; // Số ngày nghỉ phép được thưởng khác
        this.cancelledLeaveDays = 0.0; // Số ngày nghỉ phép bị hủy/không được dùng

        this.monthlyLeaveHistories = [
            {
                leaveDays: 0.0,
                month: 1
            },
            {
                leaveDays: 0.0,
                month: 2
            },
            {
                leaveDays: 0.0,
                month: 3
            },
            {
                leaveDays: 0.0,
                month: 4
            },
            {
                leaveDays: 0.0,
                month: 5
            },
            {
                leaveDays: 0.0,
                month: 6
            },
            {
                leaveDays: 0.0,
                month: 7
            },
            {
                leaveDays: 0.0,
                month: 8
            },
            {
                leaveDays: 0.0,
                month: 9
            },
            {
                leaveDays: 0.0,
                month: 10
            },
            {
                leaveDays: 0.0,
                month: 11
            },
            {
                leaveDays: 0.0,
                month: 12
            }
        ]
    }
}