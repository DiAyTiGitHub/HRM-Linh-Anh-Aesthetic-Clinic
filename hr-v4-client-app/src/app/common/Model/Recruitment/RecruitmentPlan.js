import LocalConstants from "app/LocalConstants";


export class RecruitmentPlan {
    id = null;
    code = null; // ma ke hoach
    name = null; // ten ke hoach
    recruitmentRequest = null; // yeu cau
    quantity = null; // so luong thuc te
    estimatedTimeFrom = null; // thoi gian du kien tu
    estimatedTimeTo = null; // thoi gian du kien den
    description = null; // mo ta ke hoach
    status = LocalConstants.RecruitmentPlanStatus.NOT_APPROVED_YET.value; // trang thái: HrConstants.RecruitmentPlanStatus

    organization = null;
    department = null;
    position = null;
    positionTitle = null;
    recruitmentPlanItems = [];
    roundOrder = null;
    candidates = [];
    postingSource = null;

    constructor() {
    }
}