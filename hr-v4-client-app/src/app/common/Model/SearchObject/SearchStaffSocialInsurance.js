import { SearchObject } from "./SearchObject";

export class SearchStaffSocialInsurance extends SearchObject {
  salaryResult = null;
  salaryPeriod = null;
  salaryPeriodId = null;

  staff = null;
  staffId = null;

  organizationId = null;
  organization = null;
  departmentId = null;
  department = null;
  positionTitleId = null;
  positionTitle = null;
  positionId = null;

  contractOrganization = null;
  contractOrganizationId = null;
  paidStatus = null;
  chosenRecordIds = [];

  constructor () {
    super ();


  }


}