import LocalConstants from "app/LocalConstants";

export class HrResourcePlanItem {
    id = null;

    resourcePlanId = null; // Thuộc bảng định biên nhân sự nào
    positionTitle = null; // chức danh tương ứng
    currentPositionNumber = null; // số lượng định biên (tất cả position của chức danh và phòng ban đó)
    currentStaffNumber = null; //số lượng thực tế (tất cả position có staff của chức danh và phòng ban đó)
    eliminatePlanNumber = null; //số lượng cần lọc
    additionalNumber = null; //số lượng cần bổ sung (số lượng định biên - số lượng thực tế)

    needToAdd = null;
    needToReduce = null;

    constructor() {

    }
}
