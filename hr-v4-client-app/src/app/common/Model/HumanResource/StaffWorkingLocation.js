export class StaffWorkingLocation {
    id = null;
    staff = null; // Nhân viên có địa điêm làm việc
    staffId = null; // Nhân viên có địa điêm làm việc
    //workingLocation = null;  // địa điêm làm việc
    isMainLocation = null; // Là địa điểm làm việc chính
    workplace = null; // địa điểm làm việc
    workplaceId = null;
    constructor() {
        this.isMainLocation = false;
    }
}