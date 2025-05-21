export class SalaryResultItem {
    id = null;
    displayOrder = null; // Thứ tự hiển thị
    displayName = null; // Tên cột hiển thị trên bảng lương
    salaryResultId = null; // là thành phần lương trong bảng lương nào
    resultItemGroupId = null; // thuộc nhóm cot nao
    salaryItem = null; // là thành phần lương nào

    // Công thức thực tế sử dụng để tính toán giá trị của hàng trong cột.
    // Mặc định sẽ lấy theo formula của salaryTemplateItem.
    // Người dùng có thể chỉnh sửa công thức nếu cột này có salaryItem có trường
    // calculationType = 'Dùng công thức'
    usingFormula = null;

    // các trường dưới đây được copy từ thành phần lương gốc
    code = null; // Được sinh theo trường name. VD: name: Lương cơ bản -> code: LUONG_CO_BAN
    type = null; // Tính chất của thành phần lương: HrConstants.SalaryItemType
    isTaxable = null; // Thành phần lương này có chịu thuế hay không
    isInsurable = null; // Thành phần lương này có tính BHXH hay không
    maxValue = null; // Mức trần/giá trị tối đa thành phần lương này có thể đạt
    calculationType = null; // Cách tính giá trị của thành phần lương này:
    // HrConstants.SalaryItemCalculationType
    valueType = null; // kiểu giá trị: thể hiện giá trị của cell thuộc kiểu gì, chi tiết xem
    // HrConstants.SalaryItemValueType
    description = null;
    defaultValue = null; // Mức trần/giá trị tối đa thành phần lương này có thể đạt

    constructor() {
        this.id = crypto.randomUUID();
    }
}

