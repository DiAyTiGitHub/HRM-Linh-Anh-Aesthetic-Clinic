import LocalConstants from "app/LocalConstants";

export class PositionTitle {
    id = null;
    name = null;
    code = null;
    shortName = null;
    otherName = null;
    description = null;
    positionCoefficient = null;
    type = null;
    parent = null;
    positionRole = null;
    rankTitle = null;
    recruitmentDays = null; // Số ngày tuyển dụng
    positionTitleType = null;    // Loại vị trí làm việc. Chi tiết: HrConstants.PositionTitleType
    departments = null;
    estimatedWorkingDays = null; // Số ngày làm việc được ước tính
    workDayCalculationType = null; // Cách tính ngày công chuẩn trong tháng

    constructor() {
        this.estimatedWorkingDays = 28;
        this.departments = [];
        this.workDayCalculationType = LocalConstants.PositionTitleWorkdayCalculationType.FIXED.value;

    }
}