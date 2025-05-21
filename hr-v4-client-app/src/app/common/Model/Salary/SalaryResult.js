export class SalaryResult {
    id = null;
    code = null; // ky cong/ky luong
    name = null; // ma ky cong/ky luong
    description = null; // mo ta them = mo ta ky luong
    salaryTemplate = null;
    salaryPeriod = null;

    // Các dòng trong bảng lương (Được lấy dữ liệu theo trường Staffs ở dưới) =>
    // Chứa dòng dữ liệu của các nhân viên trong bảng
    staffs = []; // Danh sách các nhân viên được chọn để tạo bảng lương
    salaryResultStaffs = [];

    resultItems = [];
    resultItemGroups = [];

    templateItems = [];
    templateItemGroups = [];
    isLocked = null; // Phiếu lương đã bị khóa hay chưa (phụ thuộc vào bảng lương đã bị khóa hay chưa)

    constructor() {

    }
}

