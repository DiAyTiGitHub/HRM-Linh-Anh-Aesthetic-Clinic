import LocalConstants from "app/LocalConstants";

export class HrResourcePlan {
    id = null;
    planDate = null; // Ngày lập định biên
    department = null; // Phòng ban thực hiện định biên nhân sự
    resourcePlanItems = null; // Các vị trí định biên trong phòng ban
    //     parentPlan = null; // Thuộc kế hoạch định biên Tổng hợp nào
    childrenPlans = null; // Các định biên con được tổng hợp để tạo thành định biên này (đối với định biên toàn Group)
    viceGeneralDirector = null; // phó tổng giám đốc duyệt
    viceGeneralDirectorStatus = null; // trạng thái phó tổng giám đốc duyệt
    generalDirector = null; // tổng giám đốc duyệt
    generalDirectorStatus = null; // trạng thái tổng giám đốc duyệt
    requester = null; // người yêu cầu định biên
    status = null; // trạng thái của yêu cầu
    
    constructor() {

    }
}
