const {values} = require("lodash");

const SYSTEM_ROLE = {
    ROLE_ADMIN: {
        value: "ROLE_ADMIN",
        name: "The general administrative role with high-level system access and management capabilities.",
    },
    HR_MANAGER: {
        value: "HR_MANAGER",
        name: "The role responsible for overseeing all HR functions, including employee management and HR policies.",
    },
    HR_USER: {
        value: "HR_USER",
        name: "A general HR role with access to employee records and HR-related functionalities.",
    },
    HR_ASSIGNMENT_ROLE: {
        value: "HR_ASSIGNMENT_ROLE",
        name: "The role responsible for assigning employees to specific projects or departments.",
    },
    HR_STAFF_VIEW: {
        value: "HR_STAFF_VIEW",
        name: "The role with read-only access to employee records and HR-related information.",
    },
    HR_RECRUITMENT: {
        value: "HR_RECRUITMENT",
        name: "The role with read-only access to employee records and HR-related information.",
    },
    HR_APPROVAL_RECRUITMENT_REQUEST: {
        value: "HR_APPROVAL_RECRUITMENT_REQUEST",
        name: "The role with read-only access to employee records and HR-related information.",
    },
    HR_CREATE_RECRUITMENT_REQUEST: {
        value: "HR_CREATE_RECRUITMENT_REQUEST",
        name: "The role with read-only access to employee records and HR-related information.",
    },
    HR_COMPENSATION_BENEFIT: {
        value: "HR_COMPENSATION_BENEFIT",
        name: "The role with read-only access to employee records and HR-related information.",
    },
    HR_VIEW_RECRUITMENT_REQUEST: {
        value: "HR_VIEW_RECRUITMENT_REQUEST",
        name: "The role with read-only access to employee records and HR-related information.",
    },
    HR_LEGISLATION: {
        value: "HR_LEGISLATION",
        name: "The role with read-only access to employee records and HR-related information.",
    },

    IS_POSITION_MANAGER: {
        value: "IS_POSITION_MANAGER",
        name: "Head of Department",
    },

    IS_GENERAL_DIRECTOR: {
        value: "IS_POSITION_MANAGER",
        name: "General director",
    },

    IS_DEPUTY_GENERAL_DIRECTOR: {
        value: "IS_DEPUTY_GENERAL_DIRECTOR",
        name: "Deputy general director",
    },

    getListData: function () {
        return Object.values(this).filter((role) => typeof role === "object");
    },
};

const ListMaritalStatus = [
    {value: 0, name: "Độc thân"}, //Single
    {value: 1, name: "Đính hôn"}, //Engaged
    {value: 2, name: "Đã kết hôn"}, //Married
    {value: 3, name: "Ly thân"}, //Separated
    {value: 4, name: "Đã ly hôn"}, //Divorced
];

const ListGender = [
    {id: "M", name: "Nam"},
    {id: "F", name: "Nữ"},
    {id: "U", name: "Không rõ"},
];

const GENDER = {
    MALE: {value: "M", name: "Nam"},
    FEMALE: {value: "F", name: "Nữ"},
    UNKNOW: {value: "U", name: "Không rõ"},
    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object" && status.value);
    },

    getNameByValue: function (value) {
        for (let key in this) {
            if (this[key]?.value === value) {
                return this[key].name;
            }
        }
        return null;
    },

    getValue: function (key) {
        return this[key]?.value ?? null;
    },
};
//trạng thái hồ sơ ứng viên:  1- chưa phê duyệt, 2- đã duyệt, 3- đã từ chối
const ListCandidateApproveStatus = [
    {value: 1, name: "Chưa phê duyệt"},
    {value: 2, name: "Đã phê duyệt"},
    {value: 3, name: "Đã từ chối"},
];

const ListSortItem = [
    {value: 3, name: "Năm"},
    {value: 2, name: "Tháng"},
    {value: 1, name: "Tuần"},
];

const ListMonth = [
    {value: 1, name: "1"},
    {value: 2, name: "2"},
    {value: 3, name: "3"},
    {value: 4, name: "4"},
    {value: 5, name: "5"},
    {value: 6, name: "6"},
    {value: 7, name: "7"},
    {value: 8, name: "8"},
    {value: 9, name: "9"},
    {value: 10, name: "10"},
    {value: 11, name: "11"},
    {value: 12, name: "12"},
];

const ListWeeks = [
    {name: "listweek.mon", index: 1, value: 1, valueWeeks: 0},
    {name: "listweek.tus", index: 2, value: 0, valueWeeks: 1},
    {name: "listweek.wed", index: 3, value: -1, valueWeeks: 2},
    {name: "listweek.thu", index: 4, value: -2, valueWeeks: 3},
    {name: "listweek.fri", index: 5, value: -3, valueWeeks: 4},
    {name: "listweek.sat", index: 6, value: -4, valueWeeks: 5},
    {name: "listweek.sun", index: 0, value: -5, valueWeeks: 6},
];

const STAFFWORKINGHISTORYTRANSFERTYPE = [
    {name: "Điều chuyển nội bộ", value: 1},
    {name: "Điều chuyển sang đơn vị khác", value: 2},
    {name: "Tạm nghỉ", value: 3},
    {name: "Ngừng công tác", value: 4},
];

const STAFFWORKINGHISTORYTRANSFERWITHOUTBREAKTYPE = [
    {name: "Điều chuyển nội bộ", value: 1},
    {name: "Điều chuyển sang đơn vị khác", value: 2},
];

const ListFourWeek = [
    {value: 1, name: "Tuần 1"},
    {value: 2, name: "Tuần 2"},
    {value: 3, name: "Tuần 3"},
    {value: 4, name: "Tuần 4"},
];

const ListFiveWeek = [
    {value: 1, name: "Tuần 1"},
    {value: 2, name: "Tuần 2"},
    {value: 3, name: "Tuần 3"},
    {value: 4, name: "Tuần 4"},
    {value: 5, name: "Tuần 5"},
];

const ListYear = Array.from({length: 7}).map((item, index) => ({
    name: new Date().getFullYear() - index + "",
    value: new Date().getFullYear() - index,
}));

const ListFamilyRelationship = [
    {value: 0, name: "Vợ chồng"},
    {value: 1, name: "Con"},
    {value: 2, name: "Bố mẹ"},
    {value: 3, name: "Anh chị em"},
    {value: 4, name: "Ông bà"},
    {value: 5, name: "Cháu"},
];

const ListHighSchoolEducation = [
    {value: 1, name: "1/12"},
    {value: 2, name: "2/12"},
    {value: 3, name: "3/12"},
    {value: 4, name: "4/12"},
    {value: 5, name: "5/12"},
    {value: 6, name: "6/12"},
    {value: 7, name: "7/12"},
    {value: 8, name: "8/12"},
    {value: 9, name: "9/12"},
    {value: 10, name: "10/12"},
    {value: 11, name: "11/12"},
    {value: 12, name: "12/12"},
    {value: 13, name: "Cấp I"},
    {value: 14, name: "Tốt nghiệp THCS/BTCS"},
    {value: 15, name: "Tốt nghiệp THPT/BTTH"},
];
const ListCivilServantType = [
    {value: 0, name: "Công chức hợp đồng"},
    {value: 1, name: "Công chức được tuyển dụng"},
];

const ListPosition = [
    {value: "1", name: "Vị trí chính"},
    {value: "2", name: "Vị trí kiêm nhiệm"},
];

const POSITION = {
    MAIN: {value: "1", name: "Vị trí chính"},
    CONCURRENT: {value: "2", name: "Vị trí kiêm nhiệm"},
    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object" && status.value);
    },
};

const ListPositionTitle = [
    {value: "1", name: "Chính quyền"},
    {value: "2", name: "Đoàn thể"},
];

const ListStatus = [
    {value: "-1", name: "Nghỉ"},
    {value: "0", name: "Đi làm"},
    {value: "1", name: "Làm online"},
    {
        value: "2",
        name: "Đi công tác",
    },
];

const Priority = [
    {id: 4, name: "Cấp bách", className: "bgc-danger-tp1"},
    {id: 3, name: "Cao", className: "bgc-warning-d1"},
    {id: 2, name: "Trung bình", className: "bgc-primary"},
    {id: 1, name: "Thấp", className: "bgc-success"},
];

const ListFamilyComeFrom = [
    {id: 1, name: "Cán bộ"},
    {id: 2, name: "Công chức NN"},
    {id: 3, name: "Công chức(chế độ cũ)"},
    {id: 4, name: "Công nhân"},
    {id: 5, name: "Nông dân"},
    {id: 6, name: "Ngư dân"},
    {id: 7, name: "Quân nhân"},
    {id: 8, name: "Quân nhân(chế độ cũ)"},
    {id: 9, name: "Tiểu chủ"},
    {id: 10, name: "Tiểu thương"},
    {id: 11, name: "Thợ thủ công"},
];

const ListFamilyPriority = [
    {id: 1, name: "Anh hùng Lao động"},
    {id: 2, name: "Anh hùng LLVT"},
    {id: 3, name: "Bà mẹ VN anh hùng"},
    {id: 4, name: "BB có thương tật đặc biệt"},
    {id: 5, name: "Bệnh binh"},
    {id: 6, name: "GĐ có người bị địch bắt tù đày"},
    {id: 7, name: "GĐ liệt sĩ"},
    {id: 8, name: "Gia đình có công với CM"},
    {id: 9, name: "Gia đình thương binh"},
    {id: 10, name: "Lão thành CM"},
    {id: 11, name: "Người hưởng CS như T.binh"},
    {id: 12, name: "Quân nhân bị bệnh nghề nghiệp"},
    {id: 13, name: "TB có thương tật đặc biệt"},
];

const ListPriorityYourself = [
    {id: 1, name: "Anh hùng Lao động"},
    {id: 2, name: "Anh hùng LLVT"},
    {id: 3, name: "BB 1/4"},
    {id: 4, name: "BB 2/4"},
    {id: 5, name: "BB 3/4"},
    {id: 6, name: "BB 4/4"},
    {id: 7, name: "BB hạng 1 có thương tật đặc biệt"},
    {id: 8, name: "Bị địch bắt tù đày"},
    {id: 9, name: "Con thương/bệnh binh"},
    {id: 10, name: "Hạng khác"},
    {id: 11, name: "Người có công với cách mạng"},
    {id: 12, name: "Người hưởng CS như thương binh"},
    {id: 13, name: "Người tham gia kháng chiến"},
    {id: 14, name: "Quân nhân bị bệnh nghề nghiệp"},
    {id: 15, name: "Quân nhân bị tai nạn lao động"},
    {id: 16, name: "TB 1/4"},
    {id: 17, name: "TB 2/4"},
    {id: 18, name: "TB 3/4"},
    {id: 19, name: "TB 4/4"},
    {id: 20, name: "TB hạng 1 có thương tật đặc biệt"},
    {id: 21, name: "Thân nhân liệt sỹ"},
];

const AdminitractiveLevel = [
    {value: 3, name: "Cấp Tỉnh/Thành phố"},
    {value: 2, name: "Cấp Quận/Huyện"},
    {value: 1, name: "Cấp Xã/Phường"},
];

const ListLanguage = [
    {id: 1, name: "Tiếng Anh"},
    {id: 2, name: "Tiếng Trung Quốc (Quan Thoại)"},
    {id: 3, name: "Tiếng Hindi"},
    {id: 4, name: "Tiếng Tây Ban Nha"},
    {id: 5, name: "Tiếng Pháp"},
    {id: 6, name: "Tiếng Ả Rập (Chuẩn)"},
    {id: 7, name: "Tiếng Bengal"},
    {id: 8, name: "Tiếng Nga"},
    {id: 9, name: "Tiếng Bồ Đào Nha"},
    {id: 10, name: "Tiếng Indonesia"},
    {id: 11, name: "Tiếng Urdu"},
    {id: 12, name: "Tiếng Đức"},
    {id: 13, name: "Tiếng Nhật"},
    {id: 14, name: "Tiếng Swahili"},
    {id: 15, name: "Tiếng Marathi"},
    {id: 16, name: "Tiếng Telugu"},
    {id: 17, name: "Tiếng Thổ Nhĩ Kì"},
    {id: 18, name: "Tiếng Trung Quốc (Quảng Đông)"},
    {id: 19, name: "Tiếng Tamil"},
    {id: 20, name: "Tiếng Punjab (Tây)"},
    {id: 21, name: "Tiếng Trung Quốc (Ngô)"},
    {id: 22, name: "Tiếng Hàn"},
    {id: 23, name: "Tiếng Việt"},
    {id: 24, name: "Tiếng Hausa"},
    {id: 25, name: "Tiếng Java"},
    {id: 26, name: "Tiếng Ả Rập (Ai Cập)"},
    {id: 27, name: "Tiếng Italia"},
    {id: 28, name: "Tiếng Gujarat"},
    {id: 29, name: "Tiếng Thái"},
    {id: 30, name: "Tiếng Amhara"},
];

const ListPositionTitleType = [
    {value: 1, name: "Chính quyền"},
    {value: 2, name: "Đoàn thể"},
];

const LIST_YEAR = Array.from({length: 7}).map((item, index) => ({
    name: "Năm " + (new Date().getFullYear() - index),
    value: new Date().getFullYear() - index,
}));

const LIST_IS_ACTION_OPTION = [
    {value: 1, name: "Được sử dụng"},
    {value: 0, name: "Chưa sử dụng"},
];

const RECRUITMENT_TYPE = [
    {value: 1, name: "Phỏng vấn trực tiếp"},
    {value: 2, name: "Phỏng vấn gián tiếp"},
];

// OFFLINE(1, "Phỏng vấn trực tiếp"),
// ONLINE(2, "Phỏng vấn gián tiếp");

// const LIST_MONTH = [
//     {value:1 , name:"Tháng 1"} ,
//     {value:2 , name:"Tháng 2"} ,
//     {value:3 , name:"Tháng 3"} ,
//     {value:4 , name:"Tháng 4"} ,
//     {value:5 , name:"Tháng 5"} ,
//     {value:6 , name:"Tháng 6"} ,
//     {value:7 , name:"Tháng 7"} ,
//     {value:8 , name:"Tháng 8"} ,
//     {value:9 , name:"Tháng 9"} ,
//     {value:10 , name:"Tháng 10"} ,
//     {value:11 , name:"Tháng 11"} ,
//     {value:12 , name:"Tháng 12"} ,
// ];
const LIST_MONTH = [
    {id: 1, value: 1, name: "Tháng 1"},
    {id: 2, value: 2, name: "Tháng 2"},
    {id: 3, value: 3, name: "Tháng 3"},
    {id: 4, value: 4, name: "Tháng 4"},
    {id: 5, value: 5, name: "Tháng 5"},
    {id: 6, value: 6, name: "Tháng 6"},
    {id: 7, value: 7, name: "Tháng 7"},
    {id: 8, value: 8, name: "Tháng 8"},
    {id: 9, value: 9, name: "Tháng 9"},
    {id: 10, value: 10, name: "Tháng 10"},
    {id: 11, value: 11, name: "Tháng 11"},
    {id: 12, value: 12, name: "Tháng 12"},
];

const ListSalaryConfigStatus = [
    {value: 0, name: "Được sử dụng"},
    {value: 1, name: "Không được sử dụng"},
];

const FilterOptionKanban = [
    {value: true, name: "Tăng dần"},
    {value: false, name: "Giảm dần"},
];

const HrDocumentItemRequired = [
    {value: true, name: "Bắt buộc"},
    {value: false, name: "Không bắt buộc"},
];

const REGEX_SPECIAL_CHARACTERS = /^[^<>{}^$`~]*$/;

const hrmFileFolder = "D:/Working/GLOBITS/ProjectV3/Hr-v3/Data/";
const PhoneRegExp = /((^(84|0){1})(2|3|5|7|8|9))+([0-9]{8})$|((^(01|02){1}))+([0-9]{9})$/;

const SupperAdmin = "ROLE_SUPER_ADMIN";
const Admin = "ROLE_ADMIN";
const HRManager = "HR_MANAGER";
const User = "ROLE_USER";
const HRRecruitment = "HR_RECRUITMENT";
const HRInsuranceManager = "HR_INSURANCE_MANAGER";
const HRUser = "HR_USER";

const RECRUITMENT_PLAN_STATUS = {
    NOT_APPROVED_YET: {value: 0, name: "Chưa duyệt"}, // Chưa duyệt
    APPROVED: {value: 1, name: "Đã duyệt"}, // Đã duyệt
    REJECTED: {value: 2, name: "Đã từ chối"}, // Đã từ chối
    COMPLETED: {value: 3, name: "Đã hoàn thành"}, // Đã hoàn thành

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

const RECRUITMENT_REQUEST_STATUS = {
    CREATED: {value: 0, name: "Chưa duyệt"}, // Chưa duyệt
    SENT: {value: 1, name: "Đã gửi"}, // Đã duyệt
    APPROVED: {value: 2, name: "Đã duyệt"}, // Đã từ chối
    REJECTED: {value: 3, name: "Đã từ chối"}, // Đã hoàn thành
    HR_LEADER: {value: 4, name: "Đã gửi cho HR"}, // Đã hoàn thành
    START_RECRUITING: {value: 5, name: "Bắt đầu tuyển dụng"}, // Đã hoàn thành
    RECRUITING: {value: 6, name: "Đang tuyển dụng"}, // Đã hoàn thành
    STOP: {value: 7, name: "Ngừng tuyển dụng"}, // Đã hoàn thành

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};
const RECRUITMENT_REQUEST = {
    CREATED: "CREATED",
    SENT: "SENT",
    APPROVED: "APPROVED",
    REJECTED: "REJECTED",
    HR_LEADER: "HR_LEADER",
    START_RECRUITING: "START_RECRUITING",
    RECRUITING: "RECRUITING",
    STOP: "STOP"
};



const POSITION_STATUS = [
    {name: "Không được sử dụng", value: 0},
    {name: "Được sử dụng", value: 1},
];

const CANDIDATE_APPROVAL_STATUS = {
    NOT_APPROVED_YET: {value: 1, name: "Chưa duyệt"}, // Hồ sơ mới, chưa duyệt
    SCREENED_PASS: {value: 2, name: "Đã sơ lọc"}, // Qua sơ lọc
    NOT_SCREENED: {value: 3, name: "Không qua sơ lọc"}, // Trượt từ vòng sơ lọc
    APPROVED: {value: 4, name: "Đã duyệt"}, // Được duyệt (sau phỏng vấn)
    REJECTED: {value: 5, name: "Đã từ chối"}, // Trượt sau khi duyệt

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object" && status.value);
    },
};

const CANDIDATE_STATUS = {
    NOT_APPROVED_YET: {value: 1, name: "Mới"}, // Hồ sơ mới, chưa duyệt
    SCREENED_PASS: {value: 2, name: "Đã sơ lọc"}, // Qua sơ lọc
    NOT_SCREENED: {value: 3, name: "Không qua sơ lọc"}, // Trượt từ vòng sơ lọc
    APPROVED: {value: 4, name: "Đã duyệt"}, // Được duyệt (sau phỏng vấn)
    APPROVE_CV: {value: 13, name: "Gửi duyệt CV"},
    REJECTED: {value: 5, name: "Đã từ chối"}, // Trượt sau khi duyệt
    NOT_RESULT_YET: {value: 7, name: "Chưa có kết quả"}, // Đang chờ kết quả phỏng vấn
    CV_NOT_APPROVED: {value: 8, name: "Không được duyệt CV"}, // Không duyệt hồ sơ
    PENDING_CANDIDATE_CONFIRMATION: {value: 9, name: "Chờ ứng viên xác nhận"}, // Chờ ứng viên xác nhận
    PENDING_ASSIGNMENT: {value: 10, name: "Chờ nhận việc"},
    DECLINED_ASSIGNMENT: {value: 12, name: "Từ chối nhận việc"},
    SEND_OFFER: {value: 16, name: "Gửi Offer"},
    REFUSE_OFFER: {value: 14, name: "Từ chối Offer"},
    ACCEPTED_ASSIGNMENT: {value: 11, name: "Đã nhận việc"},
    RESIGN: {value: 15, name: "Nghỉ việc trong lúc thử việc"},

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object" && status.value);
    },

    getNameByValue: function (value) {
        for (let key in this) {
            if (this[key]?.value === value) {
                return this[key].name;
            }
        }
        return null;
    },

    getValue: function (key) {
        return this[key]?.value ?? null;
    },
};
const CandidateStatus = {
    NOT_APPROVED_YET: "NOT_APPROVED_YET",
    SCREENED_PASS: "SCREENED_PASS",
    NOT_SCREENED: "NOT_SCREENED",
    APPROVED: "APPROVED",
    REJECTED: "REJECTED",
    NOT_RESULT_YET: "NOT_RESULT_YET",
    CV_NOT_APPROVED: "CV_NOT_APPROVED",
    PENDING_CANDIDATE_CONFIRMATION: "PENDING_CANDIDATE_CONFIRMATION",
    PENDING_ASSIGNMENT: "PENDING_ASSIGNMENT",
    ACCEPTED_ASSIGNMENT: "ACCEPTED_ASSIGNMENT",
    DECLINED_ASSIGNMENT: "DECLINED_ASSIGNMENT",
    REFUSE_OFFER: "REFUSE_OFFER",
    RESIGN: "RESIGN",
    ACCEPT_OFFER: "ACCEPT_OFFER",
};

const CANDIDATE_EXAM_STATUS = {
    NOT_TESTED_YET: {value: 1, name: "Chưa dự thi"}, // Chưa thực hiện bài test
    PASSED: {value: 2, name: "Đạt"}, // Ứng viên Pass
    FAILED: {value: 3, name: "Không đạt"}, // Ứng viên Fail
    REJECTED: {value: 4, name: "Đã từ chối"}, // Đã từ chối (HR từ chối ứng viên)
    RECRUITING: {value: 5, name: "Đang dự tuyển"}, // Đang dự tuyển

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },

    getValueByKey: function (value) {
        return Object.values(this)
            .filter((status) => typeof status === "object")
            .find((f) => f?.value === value)?.name;
    },
};

// Candidate Reception Status
const CANDIDATE_RECEPTION_STATUS = {
    NOT_RECEPTED_YET: {value: 1, name: "Chưa phân nhận việc"}, // Ứng viên chưa được TIẾP NHẬN
    RECEPTED: {value: 2, name: "Đã phân nhận việc"}, // Ứng viên đã được TIẾP NHẬN
    REJECTED: {value: 3, name: "Đã từ chối tiếp nhận"}, // Đã từ chối TIẾP NHẬN (HR từ chối ứng viên)

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

const PRE_SCREEN_STATUS = {
    PENDING: {value: 1, name: "Chờ sơ lọc"},
    PASSED: {value: 2, name: "Đạt"},
    FAILED: {value: 3, name: "Không đạt"},

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

// Candidate Onboard Status
const CANDIDATE_ONBOARD_STATUS = {
    WAITING: {value: 1, name: "Đang chờ nhận việc"}, // Ứng viên ĐANG CHỜ NHẬN VIỆC
    NOT_COME: {value: 2, name: "Không đến nhận việc"}, // Ứng viên không đến nhận việc = không nhận việc
    ONBOARDED: {value: 3, name: "Đã nhận việc"}, // Ứng viên đã nhận việc

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

const CERTIFICATE_TYPE = {
    ENGLISH_CERTIFICATE: {
        value: 1,
        name: "Chứng chỉ tiếng anh",
    },
    ENGLISH_LEVEL: {
        value: 0,
        name: "Trình độ tiếng anh",
    },
    POLITICAL_THEORY_LEVEL: {
        value: 2,
        name: "Trình độ lý luận chính trị",
    },
    STATE_MANAGEMENT_QUALIFICATIONS: {
        value: 3,
        name: "Trình độ quản lý nhà nước",
    },
    INFORMATIC_DEGREE: {
        value: 4,
        name: "Trình độ tin học",
    },
    EDUCATIONAL_MANAGEMENT_QUALIFICATIONS: {
        value: 5,
        name: "Trình độ quản lý giáo dục",
    },

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

// Tính chất của thành phần lương
const SALARY_ITEM_TYPE = {
    ADDITION: {value: 1, name: "Thu nhập (+)"}, // 1. Thu nhập (+)
    DEDUCTION: {value: 2, name: "Khấu trừ (-)"}, // 2. Khấu trừ (-)
    INFORMATION: {value: 3, name: "Thông tin nhân viên/cột hiển thị"}, // 3. Có thể là thông tin nhân viên, cột hiển thị
    OTHERS: {value: 4, name: "Khác"}, // 4. Khác (...)

    getListData: function () {
        return Object.values(this).filter((type) => typeof type === "object");
    },
};

const INTERVIEW_SCHEDULE_STATUS = {
    PENDING_CANDIDATE_CONFIRMATION: {value: 0, name: "Chờ ứng viên xác nhận"},
    CANDIDATE_CONFIRMED: {value: 1, name: "Ứng viên đã xác nhận"},
    CANDIDATE_DECLINED: {value: 2, name: "Ứng viên từ chối tham gia"},
    INTERVIEW_COMPLETED: {value: 3, name: "Đã phỏng vấn"},
    CANCELLED: {value: 4, name: "Đã hủy"},

    getListData: function () {
        return Object.values(this).filter((type) => typeof type === "object");
    },

    // Hàm trả về giá trị (value) của một trạng thái
    getValue: function (status) {
        if (this[status]) {
            return this[status].value;
        }
        return null;
    },

    // Hàm trả về tên (name) của một trạng thái từ giá trị
    getNameByValue: function (value) {
        for (let key in this) {
            if (this[key]?.value === value) {
                return this[key].name;
            }
        }
        return null;
    },
};

// Các mã thành phần lương của hệ thống
const SALARY_ITEM_CODE_SYSTEM_DEFAULT = {
    LUONG_CO_BAN_SYSTEM: {code: "LUONG_CO_BAN_SYSTEM", name: "Lương cơ bản", value: "LUONG_CO_BAN_SYSTEM"},
    LUONG_DONG_BHXH_SYSTEM: {
        code: "LUONG_DONG_BHXH_SYSTEM",
        name: "Lương đóng BHXH",
        value: "LUONG_DONG_BHXH_SYSTEM",
    },

    SO_NGAY_CONG_SYSTEM: {code: "SO_NGAY_CONG_SYSTEM", name: "Số ngày công", value: "SO_NGAY_CONG_SYSTEM"},
    SO_NGAY_CONG_CHUAN_SYSTEM: {
        code: "SO_NGAY_CONG_CHUAN_SYSTEM",
        name: "Số ngày công chuẩn",
        value: "SO_NGAY_CONG_CHUAN_SYSTEM",
    },
    LUONG_THEO_NGAY_CONG_SYSTEM: {
        code: "LUONG_THEO_NGAY_CONG_SYSTEM",
        name: "Lương theo ngày công",
        value: "LUONG_THEO_NGAY_CONG_SYSTEM",
    },

    SO_GIO_CONG_SYSTEM: {code: "SO_GIO_CONG_SYSTEM", name: "Số giờ công", value: "SO_GIO_CONG_SYSTEM"},
    SO_GIO_CONG_OT_SYSTEM: {code: "SO_GIO_CONG_OT_SYSTEM", name: "Số giờ công OT", value: "SO_GIO_CONG_OT_SYSTEM"},

    MA_NV_SYSTEM: {code: "MA_NHAN_VIEN_SYSTEM", name: "Mã nhân viên", value: "MA_NHAN_VIEN_SYSTEM"},
    HO_VA_TEN_NV_SYSTEM: {code: "HO_VA_TEN_NV_SYSTEM", name: "Họ và tên nhân viên", value: "HO_VA_TEN_NV_SYSTEM"},
    HO_NV_SYSTEM: {code: "HO_NV_SYSTEM", name: "Họ nhân viên", value: "HO_NV_SYSTEM"},
    TEN_NV_SYSTEM: {code: "TEN_NV_SYSTEM", name: "Tên nhân viên", value: "TEN_NV_SYSTEM"},
    CHUC_VU_NV_SYSTEM: {code: "CHUC_VU_NV_SYSTEM", name: "Vị trí nhân viên", value: "CHUC_VU_NV_SYSTEM"},
    DON_VI_NV_SYSTEM: {code: "DON_VI_NV_SYSTEM", name: "Đơn vị nhân viên", value: "DON_VI_NV_SYSTEM"},
    PHONG_BAN_NV_SYSTEM: {code: "PHONG_BAN_NV_SYSTEM", name: "Phòng ban nhân viên", value: "PHONG_BAN_NV_SYSTEM"},
    SDT_NV_SYSTEM: {code: "SDT_NV_SYSTEM", name: "Số điện thoại nhân viên", value: "SDT_NV_SYSTEM"},
    EMAIL_NV_SYSTEM: {code: "EMAIL_NV_SYSTEM", name: "Email nhân viên", value: "EMAIL_NV_SYSTEM"},
    GIOI_TINH_NV_SYSTEM: {code: "GIOI_TINH_NV_SYSTEM", name: "Giới tính nhân viên", value: "GIOI_TINH_NV_SYSTEM"},
    QUOC_TICH_NV_SYSTEM: {code: "QUOC_TICH_NV_SYSTEM", name: "Quốc tịch nhân viên", value: "QUOC_TICH_NV_SYSTEM"},
    NGUYEN_QUAN_NV_SYSTEM: {
        code: "NGUYEN_QUAN_NV_SYSTEM",
        name: "Nguyên quán nhân viên",
        value: "NGUYEN_QUAN_NV_SYSTEM",
    },

    BH_XA_HOI_SYSTEM: {code: "BH_XA_HOI_SYSTEM", name: "Bảo hiểm xã hội", value: "BH_XA_HOI_SYSTEM"},
    BH_Y_TE_SYSTEM: {code: "BH_Y_TE_SYSTEM", name: "Bảo hiểm y tế", value: "BH_Y_TE_SYSTEM"},
    BH_THAT_NGHIEP_SYSTEM: {
        code: "BH_THAT_NGHIEP_SYSTEM",
        name: "Bảo hiểm thất nghiệp",
        value: "BH_THAT_NGHIEP_SYSTEM",
    },

    BH_XA_HOI_CONG_TY_DONG_SYSTEM: {
        code: "BH_XA_HOI_CONG_TY_DONG_SYSTEM",
        name: "BHXH công ty đóng",
        value: "BH_XA_HOI_CONG_TY_DONG_SYSTEM",
    },
    BH_Y_TE_CONG_TY_DONG_SYSTEM: {
        code: "BH_Y_TE_CONG_TY_DONG_SYSTEM",
        name: "BHYT công ty đóng",
        value: "BH_Y_TE_CONG_TY_DONG_SYSTEM",
    },
    BH_THAT_NGHIEP_CONG_TY_DONG_SYSTEM: {
        code: "BH_THAT_NGHIEP_CONG_TY_DONG_SYSTEM",
        name: "BHTN công ty đóng",
        value: "BH_THAT_NGHIEP_CONG_TY_DONG_SYSTEM",
    },
    KHOAN_PHI_CONG_DOAN_CONG_TY_DONG_SYSTEM: {
        code: "KHOAN_PHI_CONG_DOAN_CONG_TY_DONG_SYSTEM",
        name: "Khoản phí công đoàn công ty đóng",
        value: "KHOAN_PHI_CONG_DOAN_CONG_TY_DONG_SYSTEM",
    },

    SO_NGUOI_PHU_THUOC_SYSTEM: {
        code: "SO_NGUOI_PHU_THUOC_SYSTEM",
        name: "Số người phụ thuộc",
        value: "SO_NGUOI_PHU_THUOC_SYSTEM",
    },
    GIAM_TRU_BAN_THAN_SYSTEM: {
        code: "GIAM_TRU_BAN_THAN_SYSTEM",
        name: "Giảm trừ bản thân",
        value: "GIAM_TRU_BAN_THAN_SYSTEM",
    },
    GIAM_TRU_1_NGUOI_PHU_THUOC_SYSTEM: {
        code: "GIAM_TRU_1_NGUOI_PHU_THUOC_SYSTEM",
        name: "Giảm trừ 1 người phụ thuộc",
        value: "GIAM_TRU_1_NGUOI_PHU_THUOC_SYSTEM",
    },
    GIAM_TRU_NGUOI_PHU_THUOC_SYSTEM: {
        code: "GIAM_TRU_NGUOI_PHU_THUOC_SYSTEM",
        name: "Giảm trừ người phụ thuộc",
        value: "GIAM_TRU_NGUOI_PHU_THUOC_SYSTEM",
    },
    GIAM_TRU_GIA_CANH_SYSTEM: {
        code: "GIAM_TRU_GIA_CANH_SYSTEM",
        name: "Giảm trừ gia cảnh",
        value: "GIAM_TRU_GIA_CANH_SYSTEM",
    },
    CAC_KHOAN_GIAM_TRU_SYSTEM: {
        code: "CAC_KHOAN_GIAM_TRU_SYSTEM",
        name: "Các khoản giảm trừ",
        value: "CAC_KHOAN_GIAM_TRU_SYSTEM",
    },
    THUE_TNCN_SYSTEM: {code: "THUE_TNCN_SYSTEM", name: "Thuế TNCN", value: "THUE_TNCN_SYSTEM"},
    THU_NHAP_TINH_THUE_SYSTEM: {
        code: "THU_NHAP_TINH_THUE_SYSTEM",
        name: "Thu nhập tính thuế",
        value: "THU_NHAP_TINH_THUE_SYSTEM",
    },
    THU_NHAP_CHIU_THUE_SYSTEM: {
        code: "THU_NHAP_CHIU_THUE_SYSTEM",
        name: "Thu nhập chịu thuế",
        value: "THU_NHAP_CHIU_THUE_SYSTEM",
    },
    TONG_THU_NHAP_SYSTEM: {code: "TONG_THU_NHAP_SYSTEM", name: "Tổng thu nhập", value: "TONG_THU_NHAP_SYSTEM"},
    TONG_THU_NHAP_MIEN_THUE_SYSTEM: {
        code: "TONG_THU_NHAP_MIEN_THUE_SYSTEM",
        name: "Tổng thu nhập miễn thuế",
        value: "TONG_THU_NHAP_MIEN_THUE_SYSTEM",
    },
    TONG_KHAU_TRU_SYSTEM: {code: "TONG_KHAU_TRU_SYSTEM", name: "Tổng khấu trừ", value: "TONG_KHAU_TRU_SYSTEM"},

    STT_SYSTEM: {code: "STT_SYSTEM", name: "STT", value: "STT_SYSTEM"},
    LUONG_KY_NAY_SYSTEM: {code: "LUONG_KY_NAY_SYSTEM", name: "Lương kỳ này", value: "LUONG_KY_NAY_SYSTEM"},

    getListData: function () {
        return Object.values(this).filter((type) => typeof type === "object");
    },
};

// Cách tính giá trị của thành phần lương
const SALARY_ITEM_CALCULATION_TYPE = {
    AUTO_SYSTEM: {value: 1, name: "Hệ thống lấy dữ liệu"}, // 1. Hệ thống lấy dữ liệu
    USER_FILL: {value: 2, name: "Tự nhập"}, // 2. Tự nhập
    USING_FORMULA: {value: 3, name: "Dùng công thức"}, // 3. Dùng công thức
    THRESHOLD: {value: 4, name: "Mức ngưỡng"}, // 4. Mức ngưỡng
    FIX: {value: 5, name: "Giá trị cố định"}, // 5. Giá trị cố định

    getListData: function () {
        return Object.values(this).filter((type) => typeof type === "object");
    },

    getListExceptFix: function () {
        return this.getListData().filter((type) => type.value !== this.FIX.value);
    },
};

// Kiểu giá trị (hiển thị cho kết quả của thành phần lương) của thành phần lương
const SALARY_ITEM_VALUE_TYPE = {
    TEXT: {value: 1, name: "Chữ"}, // 1. Chữ
    MONEY: {value: 2, name: "Tiền tệ"}, // 2. Tiền tệ
    NUMBER: {value: 3, name: "Số"}, // 3. Số
    PERCENT: {value: 4, name: "Phần trăm"}, // 4. Phần trăm
    OTHERS: {value: 5, name: "Khác"}, // 5. Khác

    // Phương thức để lấy danh sách tất cả các giá trị
    getListData: function () {
        return Object.values(this).filter((type) => typeof type === "object");
    },

    // Phương thức để lấy giá trị theo key
    getValueByKey: function (key) {
        return this[key]?.value || null;
    },
};

// Loại tệp đính kèm trong hồ sơ ứng viên
const CANDIDATE_ATTACHMENT_TYPE = {
    CV_RESUME: {value: 1, name: "CV/Resume"}, // CV, Resume
    COVER_LETTER: {value: 2, name: "Thư ứng tuyển"}, // Thư ứng tuyển
    PORTFOLIO: {value: 3, name: "Hồ sơ năng lực"}, // Hồ sơ năng lực
    APPLICATION_FORM: {
        value: 4,
        name: "Đơn ứng tuyển (ứng viên điền theo mẫu công ty cung cấp)",
    }, // Đơn ứng tuyển (ứng viên điền theo mẫu công ty cung cấp)
    INTERVIEW_INVITATION: {value: 5, name: "Thư mời phỏng vấn"},
    CERTIFICATES_DEGREES: {value: 6, name: "Chứng chỉ, bằng cấp"},
    OFFER_LETTER: {value: 7, name: "Thư mời nhận việc"},
    PERSONAL_DOCUMENTS: {value: 8, name: "Giấy tờ cá nhân"},
    REJECTION_LETTER: {value: 9, name: "Thư từ chối"},
    REFERENCE_LETTER: {value: 10, name: "Thư giới thiệu"},
    OTHERS: {value: 11, name: "Khác"},

    getListData: function () {
        return Object.values(this).filter((type) => typeof type === "object");
    },
};

// Trạng thái bảo hiểm xã hội của nhân viên
const STAFF_SOCIAL_INSURANCE_PAID_STATUS = {
    PAID: {value: 1, name: "Đã đóng"},
    UNPAID: {value: 2, name: "Chưa đóng"},

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },

    getValueByKey: function (value) {
        return Object.values(this)
            .filter((status) => typeof status === "object")
            .find((f) => f?.value === value)?.name;
    },
};

// Trạng thái phiếu lương nhân viên
const STAFF_PAYSLIPS_PAID_STATUS = {
    PAID: {value: 1, name: "Đã chi trả"},
    UNPAID: {value: 2, name: "Chưa chi trả"},

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },

    getValueByKey: function (value) {
        return Object.values(this)
            .filter((status) => typeof status === "object")
            .find((f) => f?.value === value)?.name;
    },
};

// Trạng thái xác nhận tạm ứng tiền
const STAFF_ADVANCE_PAYMENT_APPROVAL_STATUS = {
    NOT_APPROVED_YET: {value: 1, name: "Chưa duyệt"}, // Chưa duyệt
    APPROVED: {value: 2, name: "Đã duyệt"}, // Đã duyệt
    NOT_APPROVED: {value: 3, name: "Không duyệt"}, // Không duyệt

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

const SALARY_STAFF_PAYSLIP_APPROVAL_STATUS = {
    NOT_APPROVED_YET: {value: 1, name: "Chưa duyệt"}, // Chưa duyệt
    APPROVED: {value: 2, name: "Đã duyệt"}, // Đã duyệt
    NOT_APPROVED: {value: 3, name: "Không duyệt"},
    LOCKED: {value: 4, name: "Đã chốt"},

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

const RECRUITMENT_ROUNDS_RESULT = {
    RESULT: {value: 1, name: "Đạt"},
    NOT_RESULT_YET: {value: 0, name: "Không đạt"},

    getListData: function () {
        return Object.values(this).filter((type) => typeof type === "object");
    },
};
const CONFIG_TYPE = {
    FIX: {value: 1, name: "Giá trị cố định"},
    USING_FORMULA: {value: 2, name: "Dùng công thức"},

    getListData: function () {
        return Object.values(this).filter((type) => typeof type === "object");
    },
};
const KPI_USED_FOR_SALARY = {
    YES: {value: true, name: "Dùng để tính tương"},
    NO: {value: false, name: "Không dùng để tính lương"},

    getListData: function () {
        return Object.values(this).filter((type) => typeof type === "object");
    },
};

// Trạng thái đăng kí ca làm việc
const SHIFT_REGISTRATION_APPROVAL_STATUS = {
    NOT_APPROVED_YET: {value: 1, name: "Chưa duyệt"}, // Chưa duyệt
    APPROVED: {value: 2, name: "Đã duyệt"}, // Đã duyệt
    NOT_APPROVED: {value: 3, name: "Không duyệt"}, // Không duyệt

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

// Mối quan hệ vị trí công tác
const RELATIONSHIP_TYPE = {
    DIRECT_MANAGER: {value: 1, name: "Quản lý trực tiếp"},
    INDIRECT_MANAGER: {value: 2, name: "Quản lý gián tiếp"},
    UNDER_DIRECT_MANAGEMENT: {value: 3, name: "Chịu sự quản lý trực tiếp"},
    UNDER_INDIRECT_MANAGEMENT: {value: 4, name: "Chịu sự quản lý gián tiếp"},

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

// Loại mối quan hệ của các vị tri
const POSITION_RELATIONSHIP_TYPE = {
    DIRECT_MANAGER: {value: 1, name: "Quản lý trực tiếp phòng ban"},
    INDIRECT_MANAGER: {value: 2, name: "Quản lý gián tiếp phòng ban"},
    UNDER_DIRECT_MANAGEMENT: {value: 3, name: "Chịu sự quản lý trực tiếp"},
    UNDER_INDIRECT_MANAGEMENT: {value: 4, name: "Chịu sự quản lý gián tiếp"},

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

const OVERTIME_REQUEST_APPROVAL_STATUS = {
    NOT_APPROVED_YET: {value: 1, name: "Chưa duyệt"}, // Chưa duyệt
    APPROVED: {value: 2, name: "Đã duyệt"}, // Đã duyệt
    NOT_APPROVED: {value: 3, name: "Không duyệt"},

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

const STAFF_WORK_SCHEDULE_APPROVE_STATUS = {
    NOT_APPROVED_YET: {value: 1, name: "Chưa duyệt"}, // Chưa duyệt
    APPROVED: {value: 2, name: "Đã duyệt"}, // Đã duyệt
    NOT_APPROVED: {value: 3, name: "Không duyệt"},

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

const ABSENCE_REQUEST_APPROVAL_STATUS = {
    NOT_APPROVED_YET: {value: 1, name: "Chưa duyệt"}, // Chưa duyệt
    APPROVED: {value: 2, name: "Đã duyệt"}, // Đã duyệt
    NOT_APPROVED: {value: 3, name: "Không duyệt"},

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

const LEAVE_REQUEST_APPROVAL_STATUS = {
    NOT_APPROVED_YET: {value: 1, name: "Chưa duyệt"}, // Chưa duyệt
    APPROVED: {value: 2, name: "Đã duyệt"}, // Đã duyệt
    NOT_APPROVED: {value: 3, name: "Không duyệt"},

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

const LEAVE_TYPE_CODE = {
    ANNUAL_LEAVE: {name: "Nghỉ phép", code: "NGHI_PHEP"},
    BUSINESS_TRIP: {name: "Công tác", code: "CONG_TAC"},
    UNPAID_LEAVE: {name: "Nghỉ không lương", code: "NGHI_KHONG_LUONG"},
    PUBLIC_HOLIDAY: {name: "Nghỉ lễ", code: "NGHI_LE"},
    COMPENSATORY_LEAVE: {name: "Nghỉ bù", code: "NGHI_BU"},
    SPECIAL_LEAVE: {name: "Nghỉ chế độ", code: "NGHI_CHE_DO"},

    getListData: function () {
        return Object.values(this).filter((type) => typeof type === "object");
    },
};

const SHIFT_CHANGE_REQUEST_APPROVAL_STATUS = {
    NOT_APPROVED_YET: {value: 1, name: "Chưa duyệt"}, // Chưa duyệt
    APPROVED: {value: 2, name: "Đã duyệt"}, // Đã duyệt
    NOT_APPROVED: {value: 3, name: "Không duyệt"},

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

// Loại nghỉ phép
const ABSENCE_REQUEST_TYPE = {
    PAID_LEAVE: {value: 1, name: "Nghỉ có lương"},
    UNPAID_LEAVE: {value: 2, name: "Nghỉ không lương"},

    getListData: function () {
        return Object.values(this).filter((type) => typeof type === "object");
    },
};

// Trạng thái đăng kí ca làm việc
const STAFF_HAS_SOCIAL_INSURANCE_EXPORT_TYPE = {
    INCREASE_2007: {value: 1, name: "Báo tăng lao động (Excel 2007)"}, // Báo tăng lao động
    INCREASE_97_2003: {value: 2, name: "Báo tăng lao động (Excel 97/2003)"}, // Báo giảm lao động
    DECREASE_2007: {value: 3, name: "Báo giảm lao động (Excel 2007)"}, // Báo giảm lao động (Excel 2007)
    DECREASE_97_2003: {value: 4, name: "Báo giảm lao động (Excel 97/2003)"}, // Báo giảm lao động (Excel 97/2003)
    MODIFY_2007: {value: 5, name: "Điều chính đóng BHXH, BHYT, BHTN (Excel 2007)"}, // Điều chính đóng BHXH, BHYT, BHTN (Excel 2007)
    MODIFY_97_2003: {value: 6, name: "Điều chính đóng BHXH, BHYT, BHTN (Excel 97/2003)"}, // Điều chính đóng BHXH, BHYT, BHTN (Excel 2007)

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

// Trạng thái làm việc của nhân viên trong ca làm việc
const STAFF_WORK_SCHEDULE_WORKING_STATUS = {
    FULL_ATTENDANCE: {value: 1, name: "Đi làm đủ"},
    PARTIAL_ATTENDANCE: {value: 2, name: "Đi làm thiếu giờ"},
    // LEAVE_WITH_PERMISSION: { value: 3, name: "Nghỉ có phép" },
    // LEAVE_WITHOUT_PERMISSION: { value: 4, name: "Nghỉ không phép" },
    NOT_ATTENDANCE: {value: 5, name: "Không đi làm"},
    // LATE_FOR_WORK: { value: 6, name: "Đi làm muộn" },

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

const STAFF_WORK_SCHEDULE_WORKING_STATUS_FULL = {
    FULL_ATTENDANCE: {value: 1, name: "Đi làm đủ"},
    PARTIAL_ATTENDANCE: {value: 2, name: "Đi làm thiếu giờ"},
    LEAVE_WITH_PERMISSION: {value: 3, name: "Nghỉ có phép"},
    LEAVE_WITHOUT_PERMISSION: {value: 4, name: "Nghỉ không phép"},
    NOT_ATTENDANCE: {value: 5, name: "Không đi làm"},
    LATE_FOR_WORK: {value: 6, name: "Đi làm muộn"},

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

// Trạng thái làm việc của nhân viên trong ca làm việc
const STAFF_WORK_SCHEDULE_WORKING_TYPE = {
    NORMAL_WORK: {value: 1, name: "Làm việc bình thường"},
    EXTENDED_OVERTIME: {value: 2, name: "Tăng ca kéo dài"},

    getListData: function () {
        return Object.values(this).filter((type) => typeof type === "object");
    },
};

const AGREEMENT_STATUS = {
    UNSIGNED: {value: 1, name: "Hợp đồng chưa được ký"},
    SIGNED: {value: 2, name: "Hợp đồng đã được ký"},
    TERMINATED: {value: 3, name: "Đã chấm dứt"},

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

// loai ngay nghi
const HOLIDAY_LEAVE_TYPE = {
    WEEKEND: {value: 1, name: "Ngày nghỉ cuối tuần"},
    PULBIC_HOLIDAY: {value: 2, name: "Ngày nghỉ lễ chung"},
    OTHERS: {value: 3, name: "Khác"},

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

// Loại ca làm việc
const SHIFT_WORK_TYPE = {
    ADMINISTRATIVE: {value: 1, name: "Hành chính"},
    OVERTIME: {value: 2, name: "Tăng ca"},

    getListData: function () {
        return Object.values(this).filter((type) => typeof type === "object");
    },
};

const STAFF_POSITION_TYPE = {
    NHA_QUAN_LY: {value: 1, name: "Nhà quản lý"},
    CHUYEN_MON_KY_THUAT_BAC_CAO: {value: 2, name: "Chuyên môn kỹ thuật bậc cao"},
    CHUYEN_MON_KY_THUAT_BAC_TRUNG: {value: 3, name: "Chuyên môn kỹ thuật bậc trung"},
    KHAC: {value: 4, name: "Khác"},

    getListData: function () {
        return Object.values(this).filter((type) => typeof type === "object");
    },
};

const TIME_SHEET_DETAIL_TYPE = {
    CHECKIN: {value: 1, name: "Bắt đầu - Checkin"},
    CHECKOUT: {value: 2, name: "Kết thúc - Checkout"},

    getListData: function () {
        return Object.values(this).filter((type) => typeof type === "object");
    },
};

// Các toán tử so sánh trong cấu hình tính lương theo ngưỡng
const SALARY_TEMPLATE_ITEM_CONFIG_OPERATOR = {
    EQUALS: {value: 1, name: "Bằng (=)"},
    NOT_EQUALS: {value: 2, name: "Khác (!=)"},
    GREATER_THAN: {value: 3, name: "Lớn hơn (>)"},
    LESS_THAN: {value: 4, name: "Nhỏ hơn (<)"},
    GREATER_THAN_OR_EQUALS: {value: 5, name: "Lớn hơn bằng (>=)"},
    LESS_THAN_OR_EQUALS: {value: 6, name: "Nhỏ hơn bằng (<=)"},

    getListData: function () {
        return Object.values(this).filter((op) => typeof op === "object");
    },

    getMinOperatorList: function () {
        // Chỉ lấy >=, >
        return Object.values(this).filter((op) => typeof op === "object" && [3, 5].includes(op.value));
    },

    getMaxOperatorList: function () {
        // Chỉ lấy <=, <
        return Object.values(this).filter((op) => typeof op === "object" && [4, 6].includes(op.value));
    },
};

// Tình trạng nộp hồ sơ của nhân viên
const STAFF_DOCUMENT_STATUS = {
    UNSUBMITTED: {value: 1, name: "Chưa nộp hồ sơ"},
    INCOMPLETED: {value: 2, name: "Thiếu hồ sơ"},
    COMPLETED: {value: 3, name: "Đủ hồ sơ"},

    getListData: function () {
        return Object.values(this).filter((status) => typeof status === "object");
    },
};

// Hình thức làm việc của nhân viên
const STAFF_WORKING_FORMAT = {
    COLLABORATE: {value: 1, name: "Cộng tác"},
    PARTTIME: {value: 2, name: "Bán thời gian (Part-time)"},
    FULLTIME: {value: 3, name: "Toàn thời gian (Full-time)"},

    getListData: function () {
        return Object.values(this).filter((format) => typeof format === "object");
    },
};

// Tình trạng nhân viên
const STAFF_PHASE = {
    INTERN: {value: 1, name: "Học việc (HV)"},
    PROBATION: {value: 2, name: "Thử việc (TV)"},
    OFFICIAL: {value: 3, name: "Chính thức (CT)"},

    getListData: function () {
        return Object.values(this).filter((phase) => typeof phase === "object");
    },
};

// hard code giá trị mặc định của form thông tin nhân viên
const DEFAULT_INFORMATION_STAFF = {
    COUNTRY: {code: "VN", name: "Quốc gia việt nam"},
    RELIGION: {code: "KHONG", name: "Không có tôn giáo"},
    ETHNICS: {code: "VN-KH", name: "Dân tộc Kinh"},

    getListData: function () {
        return Object.values(this).filter((phase) => typeof phase === "object");
    },
};

// hard code trạng thái làm việc của nhân viên để bãi nhiện tất cả các vị trí nhân viên đó
const DISMISS_POSITIONS = {
    DA_NGHI_VIEC: "DA_NGHI_VIEC",
};

const EVALUATION_STATUS = {
    PASS: "PASS",
    FAIL: "FAIL",
};

const EVALUATION_STATUS_V2 = {
    PASS: {value: 0, name: "Đạt"},
    FAIL: {value: 1, name: "Không đạt"},

    getListData: function () {
        return Object.values(this).filter((item) => typeof item === "object");
    },
};

const TIMEKEEPING_CALCULATION_TYPE = {
    FIRST_IN_FIRST_OUT: {value: 1, code: "FIFO", name: "Vào đầu, Ra đầu"},
    FIRST_IN_LAST_OUT: {value: 2, code: "FILO", name: "Vào đầu, Ra cuối"},
    LAST_IN_LAST_OUT: {value: 3, code: "LILO", name: "Vào cuối, Ra cuối"},

    getListData: function () {
        return Object.values(this).filter((item) => typeof item === "object");
    },
};

const POSITION_TITLE_WORKDAY_CALCULATION_TYPE = {
    FIXED: {value: 1, name: "Cố định"},
    CHANGE_BY_PERIOD: {value: 2, name: "Thay đổi theo kỳ lương"},

    getListData: function () {
        return Object.values(this).filter((item) => typeof item === "object");
    },
};

const HttpStatus = {
    OK: 200, // Successful request
    CREATED: 201, // Resource successfully created
    ACCEPTED: 202, // Request accepted, but not yet processed
    NO_CONTENT: 204, // Request succeeded, but no content to return
    MOVED_PERMANENTLY: 301, // Resource moved permanently to another URL
    FOUND: 302, // Resource found at a different URL temporarily
    NOT_MODIFIED: 304, // Resource not modified since the last request
    BAD_REQUEST: 400, // Invalid request, bad syntax
    UNAUTHORIZED: 401, // Authentication required, invalid credentials
    FORBIDDEN: 403, // Authentication succeeded, but access is forbidden
    NOT_FOUND: 404, // Resource not found
    METHOD_NOT_ALLOWED: 405, // HTTP method not allowed for the resource
    CONFLICT: 409, // Conflict with the current state of the resource
    UNPROCESSABLE_ENTITY: 422, // Request entity is correct, but unable to process instructions
    INTERNAL_SERVER_ERROR: 500, // Internal server error
    NOT_IMPLEMENTED: 501, // Server doesn't support the requested feature
    BAD_GATEWAY: 502, // Invalid response from upstream server
    SERVICE_UNAVAILABLE: 503, // Service temporarily unavailable
    GATEWAY_TIMEOUT: 504, // Server timed out waiting for a response from upstream server
};
const WorkType = [
    {
        name: "Nhân viên chính thức",
        id: "FULL_TIME",
    },
    {
        name: "Cộng tác viên",
        id: "PART_TIME",
    },
    {
        name: "Thực tập sinh",
        id: "INTERN",
    },
];

const EVALUATE_PERSON = {
    STAFF: "STAFF",
    DIRECT_MANAGER: "DIRECT_MANAGER",
};

const CandidateRecruitmentRoundStatus = Object.freeze({
    WAIT_RESPONSE: "WAIT_RESPONSE",
    PARTICIPATED: "PARTICIPATED",
    NOT_PARTICIPATED: "NOT_PARTICIPATED",
    REJECTED: "REJECTED",
});

const CandidateRecruitmentRoundStatusLabel = {
    WAIT_RESPONSE: "Chờ xác nhận",
    PARTICIPATED: "Tham gia",
    NOT_PARTICIPATED: "Không tham gia",
    REJECTED: "Loại hồ sơ",
};

const CandidateRecruitmentRoundResult = {
    PASS: "Đạt",
    FAIL: "Trượt",
};
const EvaluationItemType = Object.freeze([
    {name: "TECHNICAL_EXPERTISE", value: "Chuyên môn kỹ thuật"},
    {name: "SKILL", value: "Kỹ năng"},
    {name: "ATTITUDE", value: "Thái độ"},
    {name: "OTHER", value: "Khác"},
]);
const EvaluationTemplateContentType = Object.freeze([
    {name: "TITLE", value: "Tiêu đề"},
    {name: "CONTENT", value: "Nội dung"},
]);

const ORGANIZATION_TYPE = {
    LEGAL_ENTITY: {value: 1, name: "Pháp nhân"},
    OPERATION: {value: 2, name: "Vận hành"},
    getListData: function () {
        return Object.values(this).filter((item) => typeof item === "object");
    },
};

const ORTHER_INCOME_TYPE = {
    INCOME: {value: 1, name: "Thu nhập"},
    DEDUCTION: {value: 2, name: "Khấu trừ"},
    getListData: function () {
        return Object.values(this).filter((item) => typeof item === "object");
    },
};

const STAFF_WORK_SHIFT_TYPE = {
    FIXED: {value: 1, name: "Ca làm việc cố định"},
    FLEXIBLE: {value: 2, name: "Ca làm việc linh hoạt"},
    getListData: function () {
        return Object.values(this).filter((item) => typeof item === "object");
    },
};

const STAFF_LEAVE_SHIFT_TYPE = {
    FIXED: {value: 1, name: "Nghỉ cố định"},
    FLEXIBLE: {value: 2, name: "Nghỉ linh hoạt theo tháng"},
    getListData: function () {
        return Object.values(this).filter((item) => typeof item === "object");
    },
};

const PAID_STATUS_OF_LEAVE_STAFF = {
    PAID: {value: 1, name: "Đã chi trả"},
    UNPAID: {value: 2, name: "Chưa chi trả"},
    NOTPAID: {value: 3, name: "Không chi trả"},

    getListData: function () {
        return Object.values(this).filter((item) => typeof item === "object");
    },
};

const WEEK_DAYS = {
    MON: {value: 2, name: "Thứ Hai"},
    TUE: {value: 3, name: "Thứ Ba"},
    WED: {value: 4, name: "Thứ Tư"},
    THU: {value: 5, name: "Thứ Năm"},
    FRI: {value: 6, name: "Thứ Sáu"},
    SAT: {value: 7, name: "Thứ Bảy"},
    SUN: {value: 8, name: "Chủ Nhật"},

    getListData: function () {
        return Object.values(this).filter((item) => typeof item === "object");
    },
};

const SYSTEM_CONFIG_CODE = {
    GEN_FIXED_SCHEDULES_DAY: {
        code: "GEN_FIXED_SCHEDULES_DAY",
        name: "Ngày tạo lịch làm việc cố định cho nhân viên trong tháng tiếp theo",
    },
    GEN_FIXED_SCHEDULES_HOUR_AND_MINUTE: {
        code: "GEN_FIXED_SCHEDULES_HOUR_AND_MINUTE",
        name: "Giờ và phút tạo lịch làm việc cố định cho nhân viên trong tháng tiếp theo",
    },
    TAX_SALARYTEMPLATE_CODE: {
        code: "TAX_SALARY_TEMPLATE_CODE",
        name: "Mã mẫu bảng lương thuế",
    },
    ACTUAL_SALARYTEMPLATE_CODE: {
        code: "ACTUAL_SALARY_TEMPLATE_CODE",
        name: "Mã mẫu bảng lương thực tế",
    },
    BASE_WAGE_SALARYITEM_CODE: {
        code: "BASE_WAGE_SALARYITEM_CODE",
        name: "Thành phần lương Lương cơ bản",
    },
    MIN_OT_MINUTES_TO_SHOW_CONFIRM: {
        code: "MIN_OT_MINUTES_TO_SHOW_CONFIRM",
        name: "Số phút làm thêm giờ tối thiểu để hiển thị cần được xác nhận làm thêm giờ",
    },

    getListData: function () {
        return Object.values(this).filter(item => typeof item === "object");
    },

    fromCode: function (code) {
        return this.getListData().find(item => item.code === code);
    }
};


const HR_RESOURCE_PLAN_APPROVAL_STATUS = {
    NOT_APPROVED_YET: {value: 1, name: "Chưa duyệt"},
    APPROVED: {value: 2, name: "Đã duyệt"},
    NOT_APPROVED: {value: 3, name: "Không duyệt"},

    getListData: function () {
        return Object.values(this).filter((item) => typeof item === "object");
    },

    fromValue: function (value) {
        return this.getListData().find((item) => item.value === value);
    },
};


const RESULT_STATUS = [
    {value: "PASS", name: "Đạt"},
    {value: "FAIL", name: "Trượt"},
];

const CodePrefixes = {
    NHOM_NGACH: "NHOM_NGACH",
    CHUC_DANH: "CHUC_DANH",
    CAP_BAC: "CAP_BAC",
    LOAI_PHONG_BAN: "LOAI_PHONG_BAN",
    DANH_SACH_PHONG_BAN: "DANH_SACH_PHONG_BAN",
    DON_VI: "DON_VI",
    LOAI_CONG_CU_DUNG_CU: "LOAI_CONG_CU_DUNG_CU",
    CONG_CU_DUNG_CU: "CONG_CU_DUNG_CU",
    LOAI_KY_LUAT: "LOAI_KY_LUAT",
    KY_LUONG: "KY_LUONG", // Có thể cần xử lý đặc biệt vì định dạng khác
    TAI_LIEU: "TAI_LIEU",
    MAU_TAI_LIEU: "MAU_TAI_LIEU",
    TINH_TRANG_NV: "TINH_TRANG_NV",
    LOAI_DIEU_CHUYEN: "LOAI_DIEU_CHUYEN",
    DIA_DIEM: "DIA_DIEM",
    LOAI_DAO_TAO: "LOAI_DAO_TAO",
    TIEU_CHI_DANH_GIA: "TIEU_CHI_DANH_GIA",
    MAU_DANH_GIA: "MAU_DANH_GIA",
    VI_TRI: "VI_TRI",
    YEU_CAU_TUYEN_DUNG: "YEU_CAU_TUYEN_DUNG",
    KE_HOACH_TUYEN_DUNG: "KE_HOACH_TUYEN_DUNG",
    UNG_VIEN: "UNG_VIEN",
    YEU_CAU_DINH_BIEN: "YEU_CAU_DINH_BIEN",
    CHUYEN_NGANH: "CHUYEN_NGANH"
};
const HR_DEPARTMENT_TYPE_ENUM = {
    LPB_0004: { value: "LPB_0004", display: "Ban" },
    LPB_0005: { value: "LPB_0005", display: "Phòng" },
    LPB_0006: { value: "LPB_0006", display: "Cơ sở kinh doanh" },
    LPB_0007: { value: "LPB_0007", display: "Bộ phận" },
    LPB_0008: { value: "LPB_0008", display: "Nhóm" },
}

module.exports = Object.freeze({
    ListGender: ListGender,
    ListMaritalStatus: ListMaritalStatus,
    ListFamilyRelationship: ListFamilyRelationship,
    ListCivilServantType: ListCivilServantType,
    ListHighSchoolEducation: ListHighSchoolEducation,
    ListWeeks: ListWeeks,
    hrmFileFolder: hrmFileFolder,
    ListPosition: ListPosition,
    Position: POSITION,
    ListSortItem: ListSortItem,
    TimekeepingCalculationType: TIMEKEEPING_CALCULATION_TYPE,
    ListMonth: ListMonth,
    StaffWorkingHistoryTransferType: STAFFWORKINGHISTORYTRANSFERTYPE,
    StaffWorkingHistoryTransferWithOutBreakType: STAFFWORKINGHISTORYTRANSFERWITHOUTBREAKTYPE,
    InterviewScheduleStatus: INTERVIEW_SCHEDULE_STATUS,
    ListFourWeek: ListFourWeek,
    ListFiveWeek: ListFiveWeek,
    ListYear: ListYear,
    ListFamilyComeFrom: ListFamilyComeFrom,
    ListFamilyPriority: ListFamilyPriority,
    ListPriorityYourself: ListPriorityYourself,
    ListStatus: ListStatus,
    ListLanguage: ListLanguage,
    Priority: Priority,
    Admin: Admin,
    HRManager: HRManager,
    User: User,
    AdminitractiveLevel: AdminitractiveLevel,
    REGEX_SPECIAL_CHARACTERS: REGEX_SPECIAL_CHARACTERS,
    PhoneRegExp: PhoneRegExp,
    LIST_YEAR: LIST_YEAR,
    LIST_MONTH: LIST_MONTH,
    FilterOptionKanban: FilterOptionKanban,
    HrDocumentItemRequired: HrDocumentItemRequired,
    ListIsActiveOption: LIST_IS_ACTION_OPTION,
    RECRUITMENT_TYPE: RECRUITMENT_TYPE,
    RESULT_STATUS: RESULT_STATUS,
    ListCandidateApproveStatus: ListCandidateApproveStatus,
    ListPositionTitle: ListPositionTitle,
    ListPositionTitleType: ListPositionTitleType,
    ListSalaryConfigStatus: ListSalaryConfigStatus,
    RecruitmentPlanStatus: RECRUITMENT_PLAN_STATUS,
    RecruitmentRequestStatus: RECRUITMENT_REQUEST_STATUS,
    RecruitmentRoundsResult: RECRUITMENT_ROUNDS_RESULT,
    ListPositionStatus: POSITION_STATUS,
    StaffWorkScheduleApprovalStatus: STAFF_WORK_SCHEDULE_APPROVE_STATUS,
    HrResourcePlanApprovalStatus: HR_RESOURCE_PLAN_APPROVAL_STATUS,

    // Các quyền của phần mềm
    SystemRole: SYSTEM_ROLE,
    SystemConfigCode: SYSTEM_CONFIG_CODE,

    CandidateAttachmentType: CANDIDATE_ATTACHMENT_TYPE,
    CandidateApprovalStatus: CANDIDATE_APPROVAL_STATUS,
    CandidateStatus: CANDIDATE_STATUS,
    CandidateExamStatus: CANDIDATE_EXAM_STATUS,
    CandidateReceptionStatus: CANDIDATE_RECEPTION_STATUS,
    CandidateOnboardStatus: CANDIDATE_ONBOARD_STATUS,
    CertificateType: CERTIFICATE_TYPE,

    SalaryItemType: SALARY_ITEM_TYPE,
    SalaryItemCalculationType: SALARY_ITEM_CALCULATION_TYPE,
    SalaryItemCodeSystemDefault: SALARY_ITEM_CODE_SYSTEM_DEFAULT,
    SalaryItemValueType: SALARY_ITEM_VALUE_TYPE,
    KpiUsedForSalary: KPI_USED_FOR_SALARY,
    SalaryStaffPayslipApprovalStatus: SALARY_STAFF_PAYSLIP_APPROVAL_STATUS,
    SalaryTemplateItemConfigOperator: SALARY_TEMPLATE_ITEM_CONFIG_OPERATOR,

    StaffSocialInsurancePaidStatus: STAFF_SOCIAL_INSURANCE_PAID_STATUS,
    StaffPayslipsPaidStatus: STAFF_PAYSLIPS_PAID_STATUS,
    StaffHasSocialInsuranceExportType: STAFF_HAS_SOCIAL_INSURANCE_EXPORT_TYPE,
    StaffAdvancePaymentApprovalStatus: STAFF_ADVANCE_PAYMENT_APPROVAL_STATUS,
    ConfigType: CONFIG_TYPE,
    ShiftRegistrationApprovalStatus: SHIFT_REGISTRATION_APPROVAL_STATUS,
    RelationshipType: RELATIONSHIP_TYPE,
    PositionRelationshipType: POSITION_RELATIONSHIP_TYPE,

    StaffWorkScheduleWorkingType: STAFF_WORK_SCHEDULE_WORKING_TYPE,
    StaffWorkScheduleWorkingStatus: STAFF_WORK_SCHEDULE_WORKING_STATUS,
    StaffWorkScheduleWorkingStatusFull: STAFF_WORK_SCHEDULE_WORKING_STATUS_FULL,
    AgreementStatus: AGREEMENT_STATUS,
    HolidayLeaveType: HOLIDAY_LEAVE_TYPE,
    AbsenceRequestApprovalStatus: ABSENCE_REQUEST_APPROVAL_STATUS,
    LeaveRequestApprovalStatus: LEAVE_REQUEST_APPROVAL_STATUS,
    ShiftChangeRequestApprovalStatus: SHIFT_CHANGE_REQUEST_APPROVAL_STATUS,
    ApprovalStatus: ABSENCE_REQUEST_APPROVAL_STATUS,
    OvertimeRequestApprovalStatus: OVERTIME_REQUEST_APPROVAL_STATUS,
    ShiftWorkType: SHIFT_WORK_TYPE,
    StaffPositionType: STAFF_POSITION_TYPE,
    AbsenceRequestType: ABSENCE_REQUEST_TYPE,
    TimesheetDetailType: TIME_SHEET_DETAIL_TYPE,

    StaffDocumentStatus: STAFF_DOCUMENT_STATUS,
    StaffPhase: STAFF_PHASE,
    StaffWorkingFormat: STAFF_WORKING_FORMAT,
    DefaultInformationStaff: DEFAULT_INFORMATION_STAFF,
    HttpStatus: HttpStatus,
    EVALUATION_STATUS: EVALUATION_STATUS,
    EVALUATION_STATUS_V2: EVALUATION_STATUS_V2,
    DismissPositions: DISMISS_POSITIONS,
    EVALUATE_PERSON: EVALUATE_PERSON,

    PositionTitleWorkdayCalculationType: POSITION_TITLE_WORKDAY_CALCULATION_TYPE,
    WorkType: WorkType,
    LeaveTypeCode: LEAVE_TYPE_CODE,
    CandidateRecruitmentRoundStatus: CandidateRecruitmentRoundStatus,
    CandidateRecruitmentRoundStatusLabel: CandidateRecruitmentRoundStatusLabel,
    CandidateStatusEnum: CandidateStatus,
    CandidateRecruitmentRoundResult: CandidateRecruitmentRoundResult,
    GENDER: GENDER,
    EvaluationItemType: EvaluationItemType,
    EvaluationTemplateContentType: EvaluationTemplateContentType,
    OrganizationType: ORGANIZATION_TYPE,
    PaidStatusOfLeaveStaff: PAID_STATUS_OF_LEAVE_STAFF,
    OtherIncomeType: ORTHER_INCOME_TYPE,
    StaffLeaveShiftType: STAFF_LEAVE_SHIFT_TYPE,
    StaffWorkShiftType: STAFF_WORK_SHIFT_TYPE,
    PRE_SCREEN_STATUS: PRE_SCREEN_STATUS,
    WeekDays: WEEK_DAYS,
    CodePrefixes: CodePrefixes,
    RECRUITMENT_REQUEST: RECRUITMENT_REQUEST,
    HR_DEPARTMENT_TYPE_ENUM: HR_DEPARTMENT_TYPE_ENUM
});
