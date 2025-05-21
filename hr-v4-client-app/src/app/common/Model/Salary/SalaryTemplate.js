export class SalaryTemplate {
    id = null;
    code = null;
    name = null;
    description = null;
    isActive = null; // Đang còn được sử dụng hay không. VD: = false => Không thể chọn sử dụng mẫu
    isCreatePayslip = null; // Có tạo phiếu lương cho bảng lương sử dụng mẫu bảng lương này hay không
    // bảng lương này cho bảng lương mới nữa
    numberOfItems = null; // Số thành phần lương được sử dụng trong mẫu
    templateItemGroups = []; // các nhóm cột trong mẫu bảng lương
    templateItems = []; // thành phần lương chính là các cột trong mẫu bảng lương

    constructor() {
        this.id = crypto.randomUUID();
        this.isActive = true;
        this.isCreatePayslip = false;
    }
}

