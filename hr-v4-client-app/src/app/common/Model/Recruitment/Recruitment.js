export class Recruitment {
    id = null;
    // Thông tin tuyển dụng
    code = null; // mã đợt tuyển
    name = null; // tên đợt tuyển
    recruitmentPlan = null; // kế hoạch tuyển dụng
    position = null; // vị trí tuyển dụng
    positionTitle = null; // vị trí tuyển dụng
    organization = null;
    department = null;
    quantity = null; // số lượng tuyển
    startDate = null; // ngày bắt đầu
    endDate = null; // ngày kết thúc
    note = null; // ghi chú, yêu cầu

    // Thông tin liên hệ
    contactStaff = null; // thông tin nhân viên liên lạc
    positionCS = null; // vị trí công tác của nhân viên liên lạc
    hrDepartmentCS = null; // phòng ban của nhân viên liên lạc
    phoneNumber = null; // số điện thoại
    officePhoneNumber = null; // số điện thoại văn phòng
    contactEmail = null; // email liên lạc
    contactWebsite = null; // website liên lạc

    // Vòng tuyển dụng
    recruitmentRounds = []; // danh sách các vòng tuyển dụng

    // for display only
    numberAppliedCandidates = 0;
    organization = null;
    recruitmentItems = [];

    constructor() {

    }
}