import LocalConstants from "app/LocalConstants";

export class ShiftWork {
    id = null;
    code = null;
    name = null;
    totalHours = null;
    timePeriods = [];
    convertedWorkingHours = 0; // Số giờ công quy đổi;
    shiftWorkType = null; // Loại ca làm việc. Chi tiết: HrConstants.ShiftWorkType
    departments = []; // Danh sách phòng ban sử dụng ca làm việc này

    constructor() {
        this.shiftWorkType = LocalConstants.ShiftWorkType.ADMINISTRATIVE.value;
        // this.allowedLateMinutes = 0.0;
        this.convertedWorkingHours = 0; // Số giờ công quy đổi;

    }

}

