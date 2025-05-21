import { SearchObject } from "./SearchObject";
import { getFullYear, getMonth } from "../../../LocalFunction";

export class SearchHrIntroduceCost extends SearchObject {
  staff = null; // Nhân viên giới thiệu
  staffId = null; // Nhân viên giới thiệu
  introducedStaff = null; // Nhân viên được giới thiệu
  introducedStaffId = null; // Nhân viên được giới thiệu

  organizationId = null;
  organization = null;
  departmentId = null;
  department = null;
  positionTitleId = null;
  positionTitle = null;

  introStaffOrganizationId = null;
  introStaffOrganization = null;
  introStaffDepartmentId = null;
  introStaffDepartment = null;
  introStaffPositionTitleId = null;
  introStaffPositionTitle = null;

  month = null;
  year = null;

  constructor () {
    super ();
  }


}