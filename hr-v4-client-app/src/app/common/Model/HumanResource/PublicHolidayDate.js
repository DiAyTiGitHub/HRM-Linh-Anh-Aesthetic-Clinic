
export class PublicHolidayDate {
    id = null;
    holidayDate = null;
    holidayType = null;
    salaryCoefficient = null;
    description = null;
    isHalfDayOff = null; // Chỉ Nghỉ nửa ngày
    leaveHours = null;

    constructor() {
        this.isHalfDayOff = false;
        this.leaveHours = 8.0;
    }
}