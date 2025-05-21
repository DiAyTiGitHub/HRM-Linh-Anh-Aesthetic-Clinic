import { getFirstDateOfMonth, getFirstDateOfWeek, getLastDateOfMonth, getLastDateOfWeek } from "app/LocalFunction";
import { SearchObject } from "./SearchObject";

export class SearchStaffWorkSchedule extends SearchObject {
  staffId = null;
  staff = null;
  shiftWorkId = null;
  shiftWork = null;
  organizationId = null;
  organization = null;
  departmentId = null;
  department = null;
  positionTitle = null;
  positionTitleId = null;
  position = null;
  positionId = null;
  fromDate = null;
  toDate = null;
  workingStatus = null;
  leaveType = null;
  leaveTypeId = null;
  coordinator = null;
  coordinatorId = null;
  isFutureDate = false;
  // used in get work calendar of staff
  chosenMonth = null;
  chosenYear = null;
  salaryPeriod = null;

  constructor () {
    super ();

    this.fromDate = getFirstDateOfWeek ();
    this.toDate = getLastDateOfWeek ();
    this.workingStatus = 0;
  }


}