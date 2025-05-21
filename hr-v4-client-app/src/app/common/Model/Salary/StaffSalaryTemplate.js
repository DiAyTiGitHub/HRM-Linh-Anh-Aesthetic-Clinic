export class StaffSalaryTemplate {
    id = null;
    staff = null; // nhân viên dùng mẫu bảng lương
    salaryTemplate = null; // Mẫu bảng lương được dùng
    fromDate = null; // thời gian bắt đầu áp dụng tính lương cho nhân viên theo mẫu
    toDate = null; // thời gian kết thúc áp dụng tính lương cho nhân viên theo mẫu

    constructor() {
        this.id = crypto.randomUUID();
    }
}

