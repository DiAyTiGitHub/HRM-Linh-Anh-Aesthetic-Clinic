import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
import StaffLabourAgreementRoutes from "../StaffLabourAgreement/StaffLabourAgreementRoutes";
import StaffWorkingHistoryRoutes from "../StaffWorkingHistory/StaffWorkingHistoryRoutes";
import StaffPositionRoutes from "../StaffPosition/StaffPositionRoutes";
import HrDocumentTemplateRoutes from "../HrDocumentTemplate/HrDocumentTemplateRoutes";
import EvaluationTicketRoutes from "../Staff/EvaluationTicket/EvaluationTicketRoutes";
import HrIntroduceCostRoutes from "../HrIntroduceCost/HrIntroduceCostRoutes";
import StaffDocumentItemRoutes from "../StaffDocumentItem/StaffDocumentItemRoutes";
import StaffCertificateRoutes from "../StaffCertificate/StaffCertificateRoutes";
import StaffAnnualLeaveHistoryRoutes from "../StaffAnnualLeaveHistory/StaffAnnualLeaveHistoryRoutes";

const Staff = EgretLoadable({
  loader: () => import("./StaffIndex"),
});
const StaffCreateEdit = EgretLoadable({
  loader: () => import("./StaffCreateEdit"),
});
const StaffProfile = EgretLoadable({
  loader: () => import("app/views/profile/ProfileIndex"),
});
const StaffTimeSheet = EgretLoadable({
  loader: () => import("../TimeSheetDetails/TimeSheetDetailsIndex"),
});
const ViewStaffCreateEdit = StaffCreateEdit;
const StaffTimeSheetComponent = StaffTimeSheet;
const ViewStaffProfile = StaffProfile;


const Routes = [{
  path: ConstantList.ROOT_PATH + "staff/create",
  exact: true,
  component: ViewStaffCreateEdit,
  auth: ["ROLE_ADMIN", "HR_MANAGER"],
},
{
  path: ConstantList.ROOT_PATH + "staff/:staffType",
  exact: true,
  component: Staff,
  auth: ["ROLE_ADMIN", "HR_MANAGER"],
},
{
  path: ConstantList.ROOT_PATH + "staff/edit/:id",
  exact: true,
  component: ViewStaffCreateEdit, // auth: ["ROLE_ADMIN", "HR_MANAGER", "HR_TESTER", "HR_USER"],
},

{
  path: ConstantList.ROOT_PATH + "staff/profile/:id",
  exact: true,
  component: ViewStaffProfile, // auth: ["ROLE_ADMIN", "HR_MANAGER", "HR_TESTER", "HR_USER"],
},
{
  path: ConstantList.ROOT_PATH + "staff/TimeSheet/:id",
  exact: true,
  component: StaffTimeSheetComponent,
  auth: ["ROLE_ADMIN", "HR_MANAGER"],
},

...EvaluationTicketRoutes, //hợp đồng lao động
...StaffLabourAgreementRoutes,
...StaffWorkingHistoryRoutes,
...StaffPositionRoutes,
...HrDocumentTemplateRoutes,
...HrIntroduceCostRoutes,
...StaffDocumentItemRoutes,
...StaffCertificateRoutes,
...StaffAnnualLeaveHistoryRoutes
];

export default Routes;
//