import LocalConstants from "app/LocalConstants";
import { getFirstDateOfMonth, getLastDateOfMonth } from "app/LocalFunction";

export class CreateStaffWorkScheduleList {
  department = null;
  organization = null;
  positionTitle = null;

  staffs = [];
  shiftWorks = [];
  fromDate = null;
  toDate = null;
  name = null;
  overtimeHours = null;
  workingType = null;
  allowOneEntryOnly = null;

  loopOnMonday = null;
  loopOnTuesDay = null;
  loopOnWednesday = null;
  loopOnThursday = null;
  loopOnFriday = null;
  loopOnSaturday = null;
  loopOnSunday = null;

  timekeepingCalculationType = null;
  needManagerApproval = null;

  constructor() {
    this.staffs = [];
    this.shiftWorks = [];
    this.allowOneEntryOnly = true;

    this.loopOnMonday = true;
    this.loopOnTuesDay = true;
    this.loopOnWednesday = true;
    this.loopOnThursday = true;
    this.loopOnFriday = true;
    this.loopOnSaturday = false;
    this.loopOnSunday = false;

    this.fromDate = getFirstDateOfMonth();
    this.toDate = getLastDateOfMonth();

    this.overtimeHours = 0.0;
    this.workingType = LocalConstants.StaffWorkScheduleWorkingType.NORMAL_WORK.value;
    this.needManagerApproval = false;
    this.timekeepingCalculationType = LocalConstants.TimekeepingCalculationType.FIRST_IN_LAST_OUT.value;
  }
}
