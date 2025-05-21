import LocalConstants from "app/LocalConstants";
import { trim, upperCase } from "lodash";

export function extractVariables(expression) {
    // Danh sách các hàm hoặc từ khóa không phải là biến
    const keywords = [
        'IF', 'AND', 'OR', 'ROUND', 'DATE', 'MONTH', 'TODAY', 'INT', 'MAX', 'MIN'
    ];

    // Regex tìm các từ có dạng chữ cái, số và dấu gạch dưới không thuộc từ khóa
    const regex = /\b[A-Z_][A-Z0-9_]*\b/gi;

    // Tìm tất cả các từ trong biểu thức
    const matches = expression.match(regex) || [];

    // Lọc các từ không phải từ khóa
    const variables = matches.filter(word => !keywords.includes(word.toUpperCase()));

    // Loại bỏ trùng lặp
    return [...new Set(variables)];
}

export function evaluateExpression(expression, variableValues) {
    try {
        // Kiểm tra và thay thế các biến trong biểu thức bằng giá trị từ variableValues
        const replacedExpression = expression.replace(/\b[A-Z_][A-Z0-9_]*\b/gi, (match) => {
            if (variableValues.hasOwnProperty(match)) {
                return variableValues[match];
            } else {
                throw new Error(`Variable ${match} is undefined`);
            }
        });

        // Thay thế các hàm tùy chỉnh thành JavaScript tương ứng
        const transformedExpression = replacedExpression
            .replace(/\bIF\(([^,]+),([^,]+),([^\)]+)\)/gi, '(($1) ? ($2) : ($3))')
            .replace(/\bAND\(([^,]+),([^\)]+)\)/gi, '(($1) && ($2))')
            .replace(/\bOR\(([^,]+),([^\)]+)\)/gi, '(($1) || ($2))')
            .replace(/\bROUND\(([^,]+),\s*([^\)]+)\)/gi, '((Math.round($1 * Math.pow(10, $2)) / Math.pow(10, $2)))')
            .replace(/\bDATE\((\d+),\s*(\d+),\s*(\d+)\)/gi, '(new Date($1, $2 - 1, $3).getTime())')
            .replace(/\bMONTH\(([^\)]+)\)/gi, '(new Date($1).getMonth() + 1)')
            .replace(/\bTODAY\(\)/gi, '(new Date().getTime())')
            .replace(/\bINT\(([^\)]+)\)/gi, '(Math.floor($1))')
            .replace(/\bMAX\(([^\)]+)\)/gi, 'Math.max($1)')
            .replace(/\bMIN\(([^\)]+)\)/gi, 'Math.min($1)');

        // Tính toán biểu thức sử dụng eval
        const result = eval(transformedExpression);

        return result;
    } catch (error) {
        console.error(error.message);
        return null; // Trả về null nếu có lỗi (ví dụ: thiếu biến)
    }
}

export function convertDataToVariableValues(dataList) {
    return dataList.reduce((acc, item) => {
        if (item.referenceCode) {
            acc[item.referenceCode] = item.value || null; // Gán giá trị value hoặc null nếu không có value
        }
        return acc;
    }, {});
}

const specialList = [
    LocalConstants.SalaryItemCodeSystemDefault.TONG_THU_NHAP_SYSTEM.value,
    LocalConstants.SalaryItemCodeSystemDefault.TONG_KHAU_TRU_SYSTEM.value,
    LocalConstants.SalaryItemCodeSystemDefault.TONG_THU_NHAP_MIEN_THUE_SYSTEM.value
];

export function isSpecialCellItem(code) {
    
    if (specialList.includes(code)) {
        return true;
    }

    return false;
}

export function getSpecialCellItemFormula(vars) {
    const {
        code, // Mã cell cần tính toán đặc biệt
        items, // các cột trong bảng lương
    } = vars;

    if (code === LocalConstants.SalaryItemCodeSystemDefault.TONG_THU_NHAP_SYSTEM.value) {
        return getSITongThuNhapFormula(items);
    }
    if (code === LocalConstants.SalaryItemCodeSystemDefault.TONG_KHAU_TRU_SYSTEM.value) {
        return getSITongKhauTruFormula(items);
    }
    if (code === LocalConstants.SalaryItemCodeSystemDefault.TONG_THU_NHAP_MIEN_THUE_SYSTEM.value) {
        return getSITongThuNhapMienThueFormula(items);
    }
}

function getSITongThuNhapFormula(data) {
    const listVars = [];
    data?.forEach(function (item, index) {
        if (item.salaryItem.type == LocalConstants.SalaryItemType.ADDITION.value) {
            listVars.push(item.salaryItem.code);
        }
    });

    return listVars.join(' + ');
}

function getSITongKhauTruFormula(data) {
    const listVars = [];
    data?.forEach(function (item, index) {
        if (item.salaryItem.type == LocalConstants.SalaryItemType.DEDUCTION.value) {
            listVars.push(item.salaryItem.code);
        }
    });

    return listVars.join(' + ');
}

function getSITongThuNhapMienThueFormula(data) {
    const listVars = [];
    data?.forEach(function (item, index) {
        if (item.salaryItem.type == LocalConstants.SalaryItemType.ADDITION.value && !item.salaryItem.isTaxable) {
            listVars.push(item.salaryItem.code);
        }
    });

    return listVars.join(' + ');
}