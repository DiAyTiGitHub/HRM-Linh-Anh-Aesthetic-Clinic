import { Redirect } from "react-router-dom";
import ConstantList from "./appConfig";
import LeaveRequestRoutes from "./views/LeaveRequest/LeaveRequestRoutes";
import academicRoutes from "./views/Academic/AcademicRoutes";
import administrativeUnitRoutes from "./views/AdministrativeUnit/AdministrativeUnitRoutes";
import allowanceRoutes from "./views/Allowance/AllowanceRoutes";
import allowancePolicyRoutes from "./views/AllowancePolicy/AllowancePolicyRoutes";
import allowanceTypeRoutes from "./views/AllowanceType/AllowanceTypeRoutes";
import AssetManagementRoutes from "./views/AssetManagement/AssetManagementRoute";
import BudgetRoutes from "./views/Budget/BudgetRoutes";
import VoucherRoutes from "./views/Budget/Voucher/VoucherRoutes";
import calenderRoutes from "./views/Calendar/CalendarRoutes";
import CandidateRoutes from "./views/Candidate/Candidate/CandidateRoutes";
import ExamCandidateRoutes from "./views/Candidate/ExamCandidate/ExamCandidateRoutes";
import ExportCandidateReportRoutes from "./views/Candidate/ExportCandidates/ExportCandidateRoutes";
import NotComeCandidateRoutes from "./views/Candidate/NotComeCandidate/NotComeCandidateRoutes";
import OnboaredCandidateRoutes from "./views/Candidate/OnboaredCandidate/OnboaredCandidateRoutes";
import PassedCandidateRoutes from "./views/Candidate/PassedCandidate/PassedCandidateRoutes";
import WaitingJobCandidateRoutes from "./views/Candidate/WaitingJobCandidate/WaitingJobCandidateRoutes";
import categoryListRoutes from "./views/Category/CategoryRouter";
import certificateRoutes from "./views/Certificate/CertificateRoutes";
import civilServantCategoryRoutes from "./views/CivilServantCategory/CivilServantCategoryRoutes";
import civilServantTypeRoutes from "./views/CivilServantType/CivilServantTypeRoutes";
import ConfirmOvertimeRoutes from "./views/ConfirmOvertime/ConfirmOvertimeRoutes";
import countryRoutes from "./views/Country/CountryRoutes";
import CurrentOrgInfoRoutes from "./views/CurrentOrgInfo/CurrentOrgInfoRoutes";
import dashboardRoutes from "./views/dashboard/DashboardRoutes";
import DepartmentRoutes from "./views/Department/DepartmentRoutes";
import DepartmentGroupRoutes from "./views/DepartmentGroup/DepartmentGroupRoutes";
import DepartmentTypeRoutes from "./views/DepartmentType/DepartmentTypeRoutes";
import DepartmentV2Routes from "./views/DepartmentV2/DepartmentV2Routes";
import DisciplineRoutes from "./views/Discipline/DisciplineRoutes";
import educationalManagementLevelRoutes from "./views/EducationalManagementLevel/EducationalManagementLevelRoutes";
import educationDegreeRoutes from "./views/EducationDegree/EducationDegreeRoutes";
import educationTypeRoutes from "./views/EducationType/EducationTypeRoutes";
import evaluationItemRoutes from "./views/System/SystemParam/Evaluation/EvaluationItem/EvaluationItemRoutes";
import employeeStatusRoutes from "./views/EmployeeStatus/EmployeeStatusRoutes";
import ethnicsRoutes from "./views/Ethnics/EthnicsRoutes";
import familyRelationship from "./views/FamilyRelationship/FamilyRelationshipRoutes";
import globalPropertyRoutes from './views/GlobalProperty/GlobalPropertyRoutes';
import gradeRoutes from "./views/Grade/GradeRoutes";
import HrDepartmentIpRoutes from "./views/HrDepartmentIp/HrDepartmentIpRoutes";
import HrDepartmentItemRoutes from "./views/HrDocumentItem/HrDocumentItemRoutes";
import HrResourcePlanRoutes from "./views/HrResourcePlan/HrResourcePlanRoutes";
import staffRoutes from "./views/HumanResourcesInformation/StaffRoutes";
import informaticDegreeRoutes from "./views/InformaticDegree/InformaticDegreeRoutes";
import InsuranceRoutes from "./views/Insurance/InsuranceRoutes";
import locationRoutes from "./views/Location/LocationRoutes";
import OrganizationRoutes from "./views/Organization/OrganizationRoutes";
import OrganizationalChartRoutes from "./views/OrganizationalChartData/OrganizationalChartDataRoutes";
import OrganizationBranchRoutes from "./views/OrganizationBranch/OrganizationBranchRoutes";
import OrganizationDiagramRoutes from "./views/OrganizationDiagram/OrganizationDiagramRoutes";
import otherLanguageRoutes from "./views/OtherLanguage/OtherLanguageRoutes";
import OvertimeRequestRoutes from "./views/OvertimeRequest/OvertimeRequestRoutes";
import PersonnelRoutes from "./views/Personnel/PersonelRoutes";
import politicaltheoryLevelRoutes from "./views/PoliticaltheoryLevel/PoliticaltheoryLevelRoutes";
import PositionRoleRoutes from "./views/PositionRole/PositionRoleRoutes";
import positionTitleRoutes from "./views/PositionTitle/PositionTitleRoutes";
import PositionTitleV2Routes from "./views/PositionTitleV2/PositionTitleV2Routes";
import professionRoutes from "./views/Profession/ProfessionRoutes";
import professionalDegreeRoutes from "./views/ProfessionalDegree/ProfessionalDegreeRoutes";
import UserProfileRoutes from "./views/profile/ProfileRoutes";
import projectRoutes from "./views/Project/ProjectRoutes";
import PublicHolidayDateRoutes from "./views/PublicHolidayDate/PublicHolidayDateRoutes";
import RankTitleRoutes from "./views/RankTitle/RankTitleRoutes";
import RecruitmentRoutes from "./views/Recruitment/Recruitment/RecruitmentRoutes";
import RecruitmentExamTypeRoutes from "./views/Recruitment/RecruitmentExamType/RecruitmentExamTypeRoutes";
import RecruitmentPlanV2Routes from "./views/Recruitment/RecruitmentPlanV2/RecruitmentPlanV2Routes";
import RecruitmentRequestV2Routes from "./views/Recruitment/RecruitmentRequestV2/RecruitmentRequestV2Routes";
import InterviewScheduleRoutes from './views/InterviewSchedule/InterviewScheduleRoutes';
import religionRoutes from "./views/Religion/ReligionRoutes";
import rewardRoutes from "./views/Reward/RewardRoutes";
import roleRoutes from "./views/Role/RoleRoutes";
import KPIRoutes from "./views/Salary/KPI/KPIRoutes";
import KPIResultRoutes from "./views/Salary/KPIResult/KPIResultRoutes";
import SalaryAreaRoutes from "./views/Salary/SalaryArea/SalaryAreaRoutes";
import SalaryAutoMapRoutes from "./views/Salary/SalaryAutoMap/SalaryAutoMapRoutes";
import SalaryConfigRoutes from "./views/Salary/SalaryConfig/SalaryConfigRoutes";
import SalaryIncrementRoutes from "./views/Salary/SalaryIncrement/SalaryIncrementRoutes";
import SalaryItemRoutes from "./views/Salary/SalaryItemV2/SalaryItemRoutes";
import SalaryOutcomeRoutes from "./views/Salary/SalaryOutcome/SalaryOutcomeRoutes";
import SalaryPeriodRoutes from "./views/Salary/SalaryPeriod/SalaryPeriodRoutes";
import SalaryResultRoutes from "./views/Salary/SalaryResult/SalaryResultRoutes";
import SalaryResultConfigRoutes from "./views/Salary/SalaryResultBoardConfig/SalaryResultConfigRoutes";
import SalaryResultDetailRoutes from "./views/Salary/SalaryResultDetail/SalaryResultDetailRoutes";
import SalaryStaffPayslipRoutes from "./views/Salary/SalaryStaffPayslip/SalaryStaffPayslipRoutes";
import SalaryTemplateRoutes from "./views/Salary/SalaryTemplate/SalaryTemplateRoutes";
import SalaryTemplateCURoutes from "./views/Salary/SalaryTemplateCU/SalaryTemplateCURoutes";
import SalaryTypeRoutes from "./views/Salary/SalaryType/SalaryTypeRoutes";
import SalaryUnitRoutes from "./views/Salary/SalaryUnit/SalaryUnitRoutes";
import StaffPersonalSalaryItemValueRoutes
  from "./views/Salary/StaffPersonalSalaryItemValue/StaffPersonalSalaryItemValueRoutes";
import StaffSalaryItemValueRoutes from "./views/Salary/StaffSalaryItemValue/StaffSalaryItemValueRoutes";
import sessionRoutes from "./views/sessions/SessionRoutes";
import ShiftRegistrationRoutes from "./views/ShiftRegistration/ShiftRegistrationRoutes";
import shiftWorkRoutes from "./views/ShiftWork/ShiftWorkRoutes";
import specialityRoutes from "./views/Speciality/SpecialityRoutes";
import StaffAdvancePaymentRoutes from "./views/StaffAdvancePayment/StaffAdvancePaymentRoutes";
import staffAllowanceRoutes from "./views/StaffAllowance/StaffAllowanceRoutes";
import StaffHasSocialInsuranceRoutes from "./views/StaffHasSocialInsurance/StaffHasSocialInsuranceRoutes";
import StaffIpKeepingRoutes from "./views/StaffIpKeeping/StaffIpKeepingRoutes";
import StaffMonthScheduleRoutes from "./views/StaffMonthSchedule/StaffMonthScheduleRoutes";
import StaffSalaryTemplateRoutes from "./views/StaffSalaryTemplate/StaffSalaryTemplateRoutes";
import StaffSocialInsuranceRoutes from "./views/StaffSocialInsurance/StaffSocialInsuranceRoutes";
import StaffWorkScheduleCalendarRoutes from "./views/StaffWorkScheduleCalendar/StaffWorkScheduleCalendarRoutes";
import StaffWorkScheduleRoutes from "./views/StaffWorkScheduleV2/StaffWorkScheduleRoutes";
import stateManagementLevelRoutes from "./views/StateManagementLevel/StateManagementLevelRoutes";
import TaskRoutes from "./views/Task/TaskRoutes";
import timeKeepingRoutes from "./views/TimeKeeping/TimeKeepingRoutes";
import TimekeepingReportRoutes from "./views/TimekeepingReport/TimekeepingReportRoutes";
import timesheetDetailRoutes from "./views/TimeSheetDetail/TimeSheetDetailRoutes.js";
import timesheetDetailsRoutes from "./views/TimeSheetDetails/TimeSheetDetailsRoutes.js";
import titleConferredRoutes from "./views/TitleConferred/TitleConferredRoutes";
import trainingBaseRoutes from "./views/TrainingBase/TrainingBaseRoutes";
import userRoutes from "./views/User/UserRoutes";
import workingStatusRoutes from "./views/WorkingStatus/WorkingStatusRoutes";
import BankRoutes from "./views/Bank/BankRoutes";
import LeaveTypeRoutes from "./views/LeaveType/LeaveTypeRoutes";
import ShiftChangeRequestRoutes from "./views/ShiftChangeRequest/ShiftChangeRequestRoutes";
import WorkplaceRoutes from "app/views/Workplace/WorkplaceRoutes";
import PositionRouter from "./views/Position/PositionRouter";
import ConfirmStaffWorkScheduleRoutes from "./views/ConfirmStaffWorkSchedule/ConfirmStaffWorkScheduleRoutes";
import HistoryTimeSheetDetailRoutes from "./views/HistoryTimeSheetDetail/HistoryTimeSheetDetailRoutes";
import EvaluationTemplateRoutes
  from "./views/System/SystemParam/Evaluation/EvaluationTemplate/EvaluationTemplateRoutes";
import SystemConfigRoutes from "./views/SystemConfig/SystemConfigRoutes";
import InsurancePackageRoutes from "./views/InsurancePackage/InsurancePackageRoutes";
import StaffLabourManagementBookRoutes from "./views/StaffLabourManagementBook/StaffLabourManagementBookRoutes";
import StaffLabourUtilReportRoutes from "./views/StaffLabourUtilReport/StaffLabourUtilReportRoutes";
import ContentTemplateRoutes from "./views/System/SystemParam/ContentTemplate/ContentTemplateRoutes";
import RRequestReportRoutes from "./views/Recruitment/RRequestReport/RRequestReportRoutes";
import HrResourcePlanReportRoutes from "./views/HrResourcePlanReport/HrResourcePlanReportRoutes";
import publicRouter from "./views/PublicComponent/PublicRouter";
import LocalConstants from "./LocalConstants";


const systemRole = LocalConstants.SystemRole;

const ROLE_ADMIN = systemRole?.ROLE_ADMIN?.value;
const HR_MANAGER = systemRole?.HR_MANAGER?.value;
const HR_USER = systemRole?.HR_USER?.value;
const HR_ASSIGNMENT_ROLE = systemRole?.HR_ASSIGNMENT_ROLE?.value;
const HR_STAFF_VIEW = systemRole?.HR_STAFF_VIEW?.value;
const HR_RECRUITMENT = systemRole?.HR_RECRUITMENT?.value;
const HR_COMPENSATION_BENEFIT = systemRole?.HR_COMPENSATION_BENEFIT?.value;
const HR_APPROVAL_RECRUITMENT_REQUEST = systemRole?.HR_APPROVAL_RECRUITMENT_REQUEST?.value;
const HR_CREATE_RECRUITMENT_REQUEST = systemRole?.HR_CREATE_RECRUITMENT_REQUEST?.value;
const HR_VIEW_RECRUITMENT_REQUEST = systemRole?.HR_VIEW_RECRUITMENT_REQUEST?.value;
const IS_POSITION_MANAGER = systemRole?.IS_POSITION_MANAGER?.value;
const HR_LEGISLATION = systemRole?.HR_LEGISLATION?.value;

const redirectRoute = [
  {
    path:ConstantList.ROOT_PATH,
    exact:true,
    component:() => <Redirect to={ConstantList.HOME_PAGE}/>, //Luôn trỏ về HomePage được khai báo trong appConfig
  },
];

const errorRoute = [
  {
    component:() => (
        <Redirect to={ConstantList.ROOT_PATH + "profile"}/>
    ),
  },
];

const setting = {
  auth:[ROLE_ADMIN, HR_MANAGER],
  path:ConstantList.ROOT_PATH + "setting",
  children:[
    ... userRoutes,
    ... roleRoutes,
    ... SystemConfigRoutes
  ],
};

const accountantRole = {
  auth:[ROLE_ADMIN],
  path:ConstantList.ROOT_PATH + "category",
  children:[
    ... VoucherRoutes,
  ]
}

const categoryRoutes = {
  auth:[ROLE_ADMIN],
  path:ConstantList.ROOT_PATH + "category",
  children:[
    ... BankRoutes,
    ... BudgetRoutes,
    ... academicRoutes,
    ... ethnicsRoutes,
    ... religionRoutes,
    ... countryRoutes,
    ... positionTitleRoutes,
    ... certificateRoutes,
    ... DisciplineRoutes,
    ... civilServantTypeRoutes,
    ... rewardRoutes,
    ... employeeStatusRoutes,
    ... locationRoutes,
    ... professionRoutes,
    ... civilServantCategoryRoutes,
    ... educationDegreeRoutes,
    ... educationTypeRoutes,
    ... evaluationItemRoutes,
    ... gradeRoutes,
    ... specialityRoutes,
    ... trainingBaseRoutes,
    ... professionalDegreeRoutes,
    ... informaticDegreeRoutes,
    ... politicaltheoryLevelRoutes,
    ... stateManagementLevelRoutes,
    ... educationalManagementLevelRoutes,
    ... otherLanguageRoutes,
    ... titleConferredRoutes,
    ... familyRelationship,
    ... staffAllowanceRoutes,
    ... allowancePolicyRoutes,
    ... allowanceRoutes,
    ... allowanceTypeRoutes,
    ... administrativeUnitRoutes,
    ... workingStatusRoutes,
    ... AssetManagementRoutes,
    ... PersonnelRoutes,
    ... InsuranceRoutes,
    ... categoryListRoutes,
    ... WorkplaceRoutes,
    ... EvaluationTemplateRoutes,
    ... ContentTemplateRoutes
  ],
};

const organizationNavRoutes = {
  auth:[ROLE_ADMIN, HR_MANAGER, IS_POSITION_MANAGER],
  path:ConstantList.ROOT_PATH + "organization",
  children:[
    ... OrganizationRoutes,
    ... CurrentOrgInfoRoutes,
    ... DepartmentRoutes,
    ... DepartmentV2Routes,
    ... DepartmentTypeRoutes,
    ... OrganizationalChartRoutes,
    ... OrganizationBranchRoutes,
    ... RankTitleRoutes,
    ... DepartmentGroupRoutes,
    ... PositionRoleRoutes,
    ... HrDepartmentItemRoutes,
    ... PositionTitleV2Routes,
    ... HrResourcePlanReportRoutes,
    // ... HrDepartmentIpRoutes ,
    ... StaffIpKeepingRoutes,
    // ... LeaveTypeRoutes ,
    ... PositionRouter,
    ... OrganizationDiagramRoutes,
    // ... HrResourcePlanRoutes,
    ... InsurancePackageRoutes
  ],
};

const salaryRoutes = {
  // auth: [ROLE_ADMIN, HR_MANAGER, "HR_COMPENSATION_BENEFIT, HR_USER"],
  path:ConstantList.ROOT_PATH + "salary",
  children:[
    // Thàn phần lương
    ... SalaryItemRoutes,
    // Cấu hình thành phần lương
    ... SalaryAutoMapRoutes,
    // Mẫu bảng lương
    ... SalaryTemplateRoutes,
    // Mẫu bảng lương nhân viên
    ... StaffSalaryTemplateRoutes,
    // Màn hình Thêm/Sửa mẫu bảng lương
    ... SalaryTemplateCURoutes,
    // Vùng lương
    ... SalaryAreaRoutes,
    // Kỳ lương
    ... SalaryPeriodRoutes,
    // Tạm ứng
    ... StaffAdvancePaymentRoutes,
    // Bảng lương
    ... SalaryResultRoutes,
    // Chi tiết bảng lương
    ... SalaryResultDetailRoutes,
    // Cấu hình bảng lương
    ... SalaryResultConfigRoutes,
    // Kết quả tính lương
    ... SalaryOutcomeRoutes,
    // Phiếu lương
    ... SalaryStaffPayslipRoutes,

    ... KPIRoutes,
    ... KPIResultRoutes,

    ... SalaryIncrementRoutes,
    ... SalaryUnitRoutes,
    ... SalaryTypeRoutes,
    ... SalaryConfigRoutes,
    ... StaffSalaryItemValueRoutes,
    ... StaffPersonalSalaryItemValueRoutes
  ],
};

const legalRoutes = {
  auth:[ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_LEGISLATION],
  path:ConstantList.ROOT_PATH + "salary",
  children:[
    // Sổ quản lý lao động
    ... StaffLabourManagementBookRoutes,
    // Báo cáo tình hình sử dụng lao động
    ... StaffLabourUtilReportRoutes
  ]
};

const socialInsuranceRoutes = {
  auth:[ROLE_ADMIN, HR_MANAGER, HR_USER, HR_COMPENSATION_BENEFIT],
  path:ConstantList.ROOT_PATH + "insurance",
  children:[
    // Đóng bảo hiểm nhân viên
    ... StaffSocialInsuranceRoutes,
    ... StaffHasSocialInsuranceRoutes,
  ],
};

const recruitmentRoutes = {
  auth:[ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_APPROVAL_RECRUITMENT_REQUEST, HR_CREATE_RECRUITMENT_REQUEST, HR_VIEW_RECRUITMENT_REQUEST],
  path:ConstantList.ROOT_PATH + "recruitment",
  children:[
    // Yêu cầu tuyển dụng V2
    ... RecruitmentRequestV2Routes,
    // Kế hoạch tuyển dụng V2
    ... RecruitmentPlanV2Routes,
    // Đợt tuyển dụng
    ... RecruitmentRoutes,
    // Hồ sơ ứng viên
    ... CandidateRoutes,
    // Ứng viên tham gia kiểm tra/phỏng vấn
    ... ExamCandidateRoutes,
    // Ứng viên đã Pass bài kiểm tra/phỏng vấn
    ... InterviewScheduleRoutes,
    ... PassedCandidateRoutes,
    // Ứng viên chờ nhận việc
    ... WaitingJobCandidateRoutes,
    // Ứng viên không đến nhận việc
    ... NotComeCandidateRoutes,
    // Ứng viên đã tới nhận việc
    ... OnboaredCandidateRoutes,
    // Xuất báo cáo ứng viên được phân nhận việc
    ... ExportCandidateReportRoutes,
    // Loại bài kiểm tra
    ... RecruitmentExamTypeRoutes,
    // Báo cáo kết quả theo yêu cầu tuyển dụng
    ... RRequestReportRoutes,
  ],
};

const timekeepingRoutes = {
  // auth: [],
  path:ConstantList.ROOT_PATH + "time-keeping",
  children:[
    ... timeKeepingRoutes,
    ... timesheetDetailsRoutes,

    // Lịch sử chấm công
    ... timesheetDetailRoutes,
    // Nhân viên đăng ký ca làm việc
    ... ShiftRegistrationRoutes,
    // Phân lịch làm việc cho nhân viên
    ... StaffWorkScheduleRoutes,
    // Lịch làm việc của danh sách nhân viên
    ... StaffWorkScheduleCalendarRoutes,
    // Ca làm việc
    ... shiftWorkRoutes,
    // Lịch làm việc theo tháng của nhân viên
    ... StaffMonthScheduleRoutes,
    // Bảng chấm công của danh sách nhân viên
    ... TimekeepingReportRoutes,
    //Chi tiết lịch sửa chấm công
    ... HistoryTimeSheetDetailRoutes,
    // Ngày nghỉ làm việc
    ... PublicHolidayDateRoutes,

    ... HrDepartmentIpRoutes,

    ... LeaveTypeRoutes,
    // Nhân viên yêu cầu nghỉ phép
    ... LeaveRequestRoutes,
    // Nhân viên yc đổi ca
    ... ShiftChangeRequestRoutes,
    // Xác nhận làm thêm giờ
    ... ConfirmOvertimeRoutes,
    // Yêu cầu duyệt giờ làm thêm
    ... OvertimeRequestRoutes,
    //Xác nhận làm thêm giờ
    ... ConfirmStaffWorkScheduleRoutes
  ]
};

const routes = [
  ... globalPropertyRoutes,
  ... sessionRoutes,
  ... dashboardRoutes,
  ... redirectRoute,
  ... staffRoutes,
  // ...staffWorkingHistoryRotues,
  recruitmentRoutes,

  ... projectRoutes,
  ... calenderRoutes,
  ... UserProfileRoutes,
  timekeepingRoutes,
  salaryRoutes,
  legalRoutes,
  socialInsuranceRoutes,
  categoryRoutes,
  accountantRole,
  organizationNavRoutes,
  setting,
  ... publicRouter,
  ... TaskRoutes,
  ... errorRoute,

];

export default routes;
