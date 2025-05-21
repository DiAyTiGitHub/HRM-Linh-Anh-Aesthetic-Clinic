import LocalConstants from "app/LocalConstants";

export class TimesheetStaff {
    staffId = null;
    workingDate = null;
    staffWorkSchedule = null;
    shiftWorkTimePeriod = null;
    typeTimeSheetDetail = null; // 1- bắt đầu, 2- kết thúc
    currentTime = null; // thời điểm chấm

    //importExcel
    ipCheckOut = null;
    ipCheckIn = null;

    errorMessage = null; // Ghi chú chi tiết lỗi


    constructor() {
        this.workingDate = new Date();
        this.currentTime = new Date();
        this.typeTimeSheetDetail = LocalConstants.TimesheetDetailType.CHECKIN.value;
    }

}

