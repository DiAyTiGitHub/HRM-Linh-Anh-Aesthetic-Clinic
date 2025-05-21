import { getFirstDateOfMonth, getFirstDateOfWeek, getLastDateOfMonth, getLastDateOfWeek } from "app/LocalFunction";
import { SearchObject } from "./SearchObject";

export class SearchStaffAdvancedPayment extends SearchObject {
  organization = null;
  organizationId = null;
  department = null;
  departmentId = null;
  positionTitle = null;
  positionTitleId = null;

  salaryPeriod = null;
  salaryPeriodId = null;

  staff = null;
  staffId = null;

  approvalStatus = null;
  chosenRecordIds = [];

  constructor() {
    super();


  }


}