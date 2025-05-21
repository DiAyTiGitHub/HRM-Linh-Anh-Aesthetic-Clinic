export class HrDepartment {
    id = null;
    name = null;
    code = null;
    departmentType = null;
    parent = null;
    subDepartments = [];
    displayOrder = null;
    level = null;
    linePath = null;
    shortName = null;
    email = null;
    org = null;
    description = null;
    function = null;
    industryBlock = null;
    foundedDate = null;
    foundedNumber = null;
    departmentDisplayCode = null;// Số hiệu phòng ban
    establishDecisionCode = null;// Số quyết định thành lập
    establishDecisionDate = null;// Ngày quyết định thành lập
    hrdepartmentType = null;// loai phong ban
    departmentGroup = null;// nhom phong ban
    positionTitleManager = null;// chuc danh quan ly
    departmentPositions = [];// cac chuc danh trong phong ban
    timezone = null; // mui gio
    sortNumber = null;
    organization = null;// Đơn vị trực thuộc
    shiftWorks = [];

    constructor() {

    }
}