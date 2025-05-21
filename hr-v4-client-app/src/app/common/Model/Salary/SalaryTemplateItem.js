export class SalaryTemplateItem {
    id = null;
    displayOrder = null; // Thứ tự hiển thị
    displayName = null; // Tên cột hiển thị trên bảng lương
    usingFormula = null; // Công thức thực tế sử dụng để tính toán giá trị của hàng trong cột. Mặc định
    // sẽ lấy theo formula của salaryItem. \n Người dùng có thể chỉnh sửa công thức
    // nếu cột này có salaryItem có trường calculationType = 'Dùng công thức'
    salaryTemplateId = null; // thuộc mẫu bang luong nao
    templateItemGroupId = null; // thuộc nhóm cot nao
    salaryItem = null; // là thành phần lương nào
    description = null;
    hiddenOnPayslip = null; // thành phần này sẽ bị ẩn trong phiếu lương
    hiddenOnSalaryBoard = null; // thành phần này sẽ bị ẩn trong bảng lương

    code = null;
    type = null;
    isTaxable = null;
    isInsurable = null;

    defaultValue = null;
    maxValue = null;
    calculationType = null;
    valueType = null;
    // ancillaryDescription = null;
    // usingFormula = null;
    formula = null; // Công thức/Gía trị của thành phần này. VD: 10000 / LUONG_CO_BAN * 1.5

    value = null; // sử dụng để lưu trữ tạm thời value cho salaryvalue của staff và không có trong
    // cột của db
    allowance = null;

    templateItemConfigs =[];

    constructor() {
        this.id = crypto.randomUUID();
    }
}

