import { getFirstDateOfMonth, getFirstDateOfWeek, getLastDateOfMonth, getLastDateOfWeek } from "app/LocalFunction";
import { SearchObject } from "./SearchObject";

export class SearchStaffHasSocialInsurance extends SearchObject {
    staffId = null;
    staff = null;
    organization = null;
    organizationId = null;
    department = null;
    departmentId = null;
    positionTitle = null;
    positionTitleId = null;
    positionId = null;
    position = null;
    insuranceStartDate = null;
    insuranceEndDate = null;
    hasSocialIns = true;

    constructor() {
        super();
        this.insuranceStartDate = getFirstDateOfMonth();
        this.insuranceEndDate = getLastDateOfMonth();
    }


}
