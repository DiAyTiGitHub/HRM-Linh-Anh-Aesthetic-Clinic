export class HrOrganization {
    id = null;
    name = null;
    code = null;
    website = null;
    organizationType = null;
    level = null;
    parent = null;
    subOrganizations = [];
    users = [];
    isActive = true;
    parentId = null;
    departments = [];//danh sách phòng ban, chi nhánh trực thuộc
    administrativeUnit = null;//địa chỉ của đơn vị
    sortNumber = null;
    addressDetail = null; // Chi tiết đơn vị/pháp nhân
    taxCode = null; // Mã số thuế
    foundedDate = null; // Ngày thành lập
    representative = null; // Nhân viên đại diện
    province = null;
    district = null;
    constructor() {

    }
}