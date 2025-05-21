import LocalConstants from "app/LocalConstants";

export class ShiftWorkTimePeriod {
    id = null;
    shiftWorkDto = null;
    endTime = null;
    startTime = null;
    code = null;
    displayTime = null;
    allowedLateMinutes = null; // Số phút đi muộn cho phép
    workRatio = null; // Tỉ lệ ngày công. VD: 0.375 ngày công
    minTimekeepingHour = null; // Thời gian tối thiểu để tính chấm công
    minWorkTimeHour = null; // Thời gian tối thiểu để tính đã đi làm

    constructor() {
        // this.allowedLateMinutes = 0.0;
    }

}

