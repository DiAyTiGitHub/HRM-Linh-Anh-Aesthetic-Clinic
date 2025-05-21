import LocalConstants from "app/LocalConstants";
import { SearchObject } from "./SearchObject";

export class SearchStaffAnnualLeaveHistory extends SearchObject {
    staffId = null;
    staff = null;

    organizationId = null;
    organization = null;
    departmentId = null;
    department = null;
    positionTitleId = null;
    positionTitle = null;

    yearReport = null;
    monthYear = null;

    constructor() {
        super();

        this.yearReport = new Date().getFullYear();
        this.monthYear = new Date().getMonth();
    }


}