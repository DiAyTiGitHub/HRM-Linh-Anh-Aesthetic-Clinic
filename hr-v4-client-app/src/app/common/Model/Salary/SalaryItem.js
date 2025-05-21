import LocalConstants from "app/LocalConstants";

export class SalaryItem {
    id = null;
    name = null;
    code = null; // Được sinh theo trường name. VD: name: Lương cơ bản -> code: LUONG_CO_BAN
    type = LocalConstants.SalaryItemType.OTHERS.value; // Tính chất của thành phần lương: HrConstants.SalaryItemType
    isTaxable = null; // Thành phần lương này có chịu thuế hay không
    isInsurable = null; // Thành phần lương này có tính BHXH hay không
    isActive = true; // Đang có hiệu lực hay không. VD: = false => Không thể chọn sử dụng thành phần này cho bảng lương mới nữa
    maxValue = null; // Mức trần/giá trị tối đa thành phần lương này có thể đạt
    calculationType = LocalConstants.SalaryItemCalculationType.USER_FILL.value; // Cách tính giá trị của thành phần lương này: HrConstants.SalaryItemCalculationType
    formula = null; // Công thức/Gía trị của thành phần này. VD: 10000 / LUONG_CO_BAN * 1.5
    description = null;
    // kiểu giá trị: thể hiện giá trị của cell thuộc kiểu gì, chi tiết xem HrConstants.SalaryItemValueType
    valueType = LocalConstants.SalaryItemValueType.MONEY.value;
    defaultValue = null; // Mức trần/giá trị tối đa thành phần lương này có thể đạt
    // Các mức/ngưỡng của thành phần lương nếu có CalculationType là HrConstants.Threshold
    thresholds = [];
    allowanceId = null;
}

