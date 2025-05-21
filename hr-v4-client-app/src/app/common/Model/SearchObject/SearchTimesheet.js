import { getFirstDateOfWeek, getLastDateOfWeek } from "app/LocalFunction";

export class SearchTimesheet {
  pageIndex = null;
  pageSize = null;
  keyword = null;
  workingDate = null;
  staffCode = null;
  addressIPCheckIn = null;
  addressIPCheckOut = null;

  displayName = null;
  codeAndName = null;
  projectId = null;
  staffId = null;
  staff = null;
  shiftWorkId = null;
  shiftWork = null;
  fromDate = null;
  toDate = null;
  workingStatusId = null;

  priority = null;
  isExportExcel = null;
  projectActivityId = null;

  timeReport = null; // =1: report tuần, =2: report Tháng, =3: report theo năm
  weekReport = null;
  monthReport = null;
  yearReport = null;
  dayReport = null;

  organization = null;
  organizationId = null;
  department = null;
  departmentId = null;
  positionTitle = null;
  positionTitleId = null;
  position = null;
  positionId = null;
  salaryPeriod = null;
  notScheduled = null;

  constructor () {
    // this.fromDate = getFirstDateOfWeek();
    // this.toDate = getLastDateOfWeek();

    this.pageIndex = 1;
    this.pageSize = 10;
  }
}