export class SalaryPeriod {
    id = null;
    code = null;        // ky cong/ky luong
    name = null;        // ma ky cong/ky luong
    description = null; // mo ta them = mo ta ky luong
    fromDate = null;
    toDate = null;
    parentPeriod = null; // thuộc kỳ lương nào. VD: tháng 9 chi trả 2 kì lương => có 2 kì lương con
    subPeriods = []; // các kỳ lương con thuộc kì lương này
}

