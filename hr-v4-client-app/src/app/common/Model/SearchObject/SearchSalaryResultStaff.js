import { getFirstDateOfMonth, getFirstDateOfWeek, getLastDateOfMonth, getLastDateOfWeek } from "app/LocalFunction";
import { SearchObject } from "./SearchObject";

export class SearchSalaryResultStaff extends SearchObject {
  staffId = null;

  organizationId = null;
  organization = null;
  departmentId = null;
  department = null;
  positionTitleId = null;
  positionTitle = null;
  positionId = null;

  salaryPeriodId = null;
  salaryResultId = null;
  salaryTemplateId = null;

  staffs = [];

  salaryResult = null;
  salaryPeriod = null;
  staff = null;
  approvalStatus = null; // Trạng thái duyệt phiếu lương. Chi tiết trong:
  // HrConstants.SalaryResulStaffApprovalStatus
  paidStatus = null; // Trạng thái chi trả phiếu lương. Chi tiết trong: HrConstants.SalaryResulStaffPaidStatus

  chosenPayslipIds = [];
  salaryResultStaffIds = [];

  isPayslip = null;

  constructor() {
    super();


  }


}