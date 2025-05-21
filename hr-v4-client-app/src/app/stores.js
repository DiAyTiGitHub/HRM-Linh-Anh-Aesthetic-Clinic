import { createContext, useContext } from "react";
import AcademicStore from "./views/Academic/AcademicStore";
import AdministrativeUnitStore from "./views/AdministrativeUnit/AdministrativeUnitStore";
import AllowanceStore from "./views/Allowance/AllowanceStore";
import AllowanceTypeStore from "./views/AllowanceType/AllowanceTypeStore";
import { AssetManagementStore } from "./views/AssetManagement/AssetManagementStore";
import CalendarStore from "./views/Calendar/CalendarStore";
import CandidateStore from "./views/Candidate/Candidate/CandidateStore";
import AddendumTypeStore from "./views/Category/Staff/AddendumType/AddendumTypeStore";
import ContractTypeStore from "./views/Category/Staff/ContractType/ContractTypeStore";
import DeferredTypeStore from "./views/Category/Staff/DeferredType/DeferredTypeStore";
import DisciplinaryReasonStore from "./views/Category/Staff/DisciplinaryReason/DisciplinaryReasonStore";
import LeavingJobReasonStore from "./views/Category/Staff/LeavingJobReason/LeavingJobReasonStore";
import RefusalReasonStore from "./views/Category/Staff/RefusalReason/RefusalReasonStore";
import StaffTypeStore from "./views/Category/Staff/StaffTypeV2/StaffTypeStore";
import TransferTypeStore from "./views/Category/Staff/TransferType/TransferTypeStore";
import CertificateStore from "./views/Certificate/CertificateStore";
import CivilServantCategoryStore from "./views/CivilServantCategory/CivilServantCategoryStore";
import CivilServantTypeStore from "./views/CivilServantType/CivilServantTypeStore";
import ColorsStore from "./views/Colors/ColorStore";
import CountryStore from "./views/Country/CountryStore";
import DepartmentStore from "./views/Department/DepartmentStore";
import DepartmentGroupStore from "./views/DepartmentGroup/DepartmentGroupStore";
import DepartmentTypeStore from "./views/DepartmentType/DepartmentTypeStore";
import DisciplineStore from "./views/Discipline/DisciplineStore";
import EducationDegreeStore from "./views/EducationDegree/EducationDegreeStore";
import EducationTypeStore from "./views/EducationType/EducationTypeStore";
import EducationalManagementLevel from "./views/EducationalManagementLevel/EducationalManagementLevelStore";
import EmployeeStatus from "./views/EmployeeStatus/EmployeeStatusStore";
import EthnicsStore from "./views/Ethnics/EthnicsStore";
import FamilyRelationship from "./views/FamilyRelationship/FamilyRelationshipStore";
import GlobalPropertyStore from "./views/GlobalProperty/GlobalPropertyStore";
import GradeStore from "./views/Grade/GradeStore";
import HrDepartmentIpStore from "./views/HrDepartmentIp/HrDepartmentIpStore";
import StaffStore from "./views/HumanResourcesInformation/StaffStore";
import InformaticDegreeStore from "./views/InformaticDegree/InformaticDegreeStore";
import LocationStore from "./views/Location/LocationStore";
import OrganizationStore from "./views/Organization/OrganizationStore";
import OrganizationBranchStore from "./views/OrganizationBranch/OrganizationBranchStore";
import OtherLanguageStore from "./views/OtherLanguage/OtherLanguageStore";
import PoliticaltheoryLevel from "./views/PoliticaltheoryLevel/PoliticaltheoryLevelStore";
import PositionRoleStore from "./views/PositionRole/PositionRoleStore";
import PositionTitleStore from "./views/PositionTitle/PositionTitleStore";
import PositionTitleV2Store from "./views/PositionTitleV2/PositionTitleV2Store";
import ProfessionStore from "./views/Profession/ProfessionStore";
import ProfessionalDegreeStore from "./views/ProfessionalDegree/ProfessionalDegreeStore";
import ProjectActivityStore from "./views/Project/ProjectActivity/ProjectActivityStore";
import ProjectStore from "./views/Project/ProjectStore";
import RankTitleStore from "./views/RankTitle/RankTitleStore";
import ReligionStore from "./views/Religion/ReligionStore";
import RewardStore from "./views/Reward/RewardStore";
import RoleStore from "./views/Role/RoleStore";
import ShiftWorkStore from "./views/ShiftWork/ShiftWorkStore";
import SpecialityStore from "./views/Speciality/SpecialityStore";
import StaffLabourAgreementStore from "./views/StaffLabourAgreement/StaffLabourAgreementStore";
import StateManagementLevel from "./views/StateManagementLevel/StateManagementLevelStore";
import TaskHistoryStore from "./views/Task/TaskDetail/TaskHistory/TaskHistoryStore";
import TaskStore from "./views/Task/TaskStore";
import TimeKeepStore from "./views/TimeKeeping/TimeKeepStore";
import TimeSheetStore from "./views/TimeSheet/TimeSheetStore";
import TimeSheetDetailsStore from "./views/TimeSheetDetails/TimeSheetDetailsStore";

import LeaveRequestStore from "./views/LeaveRequest/LeaveRequestStore";
import AllowancePolicyStore from "./views/AllowancePolicy/AllowancePolicyStore";
import BudgetStore from "./views/Budget/Budget/BudgetStore";
import BudgetCategoryStore from "./views/Budget/BudgetCategory/BudgetCategoryStore";
import ReportStore from "./views/Budget/Report/ReportStore";
import VoucherStore from "./views/Budget/Voucher/VoucherStore";
import CandidateRecruitmentRoundStore from "./views/Candidate/CandidateRecruitmentRound/CandidateRecruitmentRoundStore";
import ExamCandidateStore from "./views/Candidate/ExamCandidate/ExamCandidateStore";
import ExportCandidateStore from "./views/Candidate/ExportCandidates/ExportCandidateStore";
import NotComeCandidateStore from "./views/Candidate/NotComeCandidate/NotComeCandidateStore";
import OnboaredCandidateStore from "./views/Candidate/OnboaredCandidate/OnboaredCandidateStore";
import PassedCandidateStore from "./views/Candidate/PassedCandidate/PassedCandidateStore";
import WaitingJobCandidateStore from "./views/Candidate/WaitingJobCandidate/WaitingJobCandidateStore";
import ConfirmOvertimeStore from "./views/ConfirmOvertime/ConfirmOvertimeStore";
import DepartmentV2Store from "./views/DepartmentV2/DepartmentV2Store";
import HRDocumentItemStore from "./views/HrDocumentItem/HRDocumentItemStore";
import HrIntroduceCostStore from "./views/HrIntroduceCost/HrIntroduceCostStore";
import HrResourcePlanStore from "./views/HrResourcePlan/HrResourcePlanStore";
import OrganizationalChartDataStore from "./views/OrganizationalChartData/OrganizationalChartDataStore";
import OvertimeRequestStore from "./views/OvertimeRequest/OvertimeRequestStore";
import PositionStore from "./views/Position/PositionStore";
import PublicHolidayDateStore from "./views/PublicHolidayDate/PublicHolidayDateStore";
import RecruitmentStore from "./views/Recruitment/Recruitment/RecruitmentStore";
import RecruitmentExamTypeStore from "./views/Recruitment/RecruitmentExamType/RecruitmentExamTypeStore";
import RecruitmentPlanStore from "./views/Recruitment/RecruitmentPlanV2/RecruitmentPlanV2Store";
import RecruitmentRequestStore from "./views/Recruitment/RecruitmentRequestV2/RecruitmentRequestV2Store";
import KPIStore from "./views/Salary/KPI/KPIStore";
import KPIResultStore from "./views/Salary/KPIResult/KPIResultStore";
import SalaryAreaStore from "./views/Salary/SalaryArea/SalaryAreaStore";
import SalaryAutoMapStore from "./views/Salary/SalaryAutoMap/SalaryAutoMapStore";
import SalaryConfigStore from "./views/Salary/SalaryConfig/SalaryConfigStore";
import SalaryIncrementStore from "./views/Salary/SalaryIncrement/SalaryIncrementStore";
import SalaryItemStore from "./views/Salary/SalaryItemV2/SalaryItemStore";
import SalaryOutcomeStore from "./views/Salary/SalaryOutcome/SalaryOutcomeStore";
import SalaryPeriodStore from "./views/Salary/SalaryPeriod/SalaryPeriodStore";
import SalaryResultStore from "./views/Salary/SalaryResult/SalaryResultStore";
import SalaryResultDetailStore from "./views/Salary/SalaryResultDetail/SalaryResultDetailStore";
import SalaryStaffPayslipStore from "./views/Salary/SalaryStaffPayslip/SalaryStaffPayslipStore";
import SalaryTemplateStore from "./views/Salary/SalaryTemplate/SalaryTemplateStore";
import PopupStaffSalaryTemplateStore
    from "./views/Salary/SalaryTemplateCU/SalaryTemplateCUTab/StaffSalaryTemplate/PopupStaffSalaryTemplateStore";
import PopupStaffStore from "./views/Salary/SalaryTemplateCU/SalaryTemplateCUTab/TabChooseStaff/PopupStaffStore";
import SalaryTemplateItemStore from "./views/Salary/SalaryTemplateCU/SalaryTemplateItem/SalaryTemplateItemStore";
import SalaryTypeStore from "./views/Salary/SalaryType/SalaryTypeStore";
import SalaryUnitStore from "./views/Salary/SalaryUnit/SalaryUnitStore";
import StaffSalaryItemValueStore from "./views/Salary/StaffSalaryItemValue/StaffSalaryItemValueStore";
import ShiftRegistrationStore from "./views/ShiftRegistration/ShiftRegistrationStore";
import StaffAdvancePaymentStore from "./views/StaffAdvancePayment/StaffAdvancePaymentStore";
import StaffAllowanceStore from "./views/StaffAllowance/StaffAllowanceStore";
import StaffDocumentItemStore from "./views/StaffDocumentItem/StaffDocumentItemStore";
import StaffHasSocialInsuranceStore from "./views/StaffHasSocialInsurance/StaffHasSocialInsuranceStore";
import StaffIpKeepingStore from "./views/StaffIpKeeping/StaffIpKeepingStore";
import StaffPositionStore from "./views/StaffPosition/StaffPositionStore";
import StaffSalaryTemplateStore from "./views/StaffSalaryTemplate/StaffSalaryTemplateStore";
import StaffSocialInsuranceStore from "./views/StaffSocialInsurance/StaffSocialInsuranceStore";
import StaffWorkScheduleCalendarStore from "./views/StaffWorkScheduleCalendar/StaffWorkScheduleCalendarStore";
import StaffWorkScheduleStore from "./views/StaffWorkScheduleV2/StaffWorkScheduleStore";
import StaffWorkingHistoryStore from "./views/StaffWorkingHistory/StaffWorkingHistoryStore";
import TimeSheetDetailStore from "./views/TimeSheetDetail/TimeSheetDetailStore";
import TimekeepingReportStore from "./views/TimekeepingReport/TimekeepingReportStore";
import TitleConferredStore from "./views/TitleConferred/TitleConferredStore";
import TrainingBaseStore from "./views/TrainingBase/TrainingBaseStore";
import UserStore from "./views/User/UserStore";
import WorkingStatusStore from "./views/WorkingStatus/WorkingStatusStore";
import DashboardStore from "./views/dashboard/DashboardStore";
import ProfileStore from "./views/profile/ProfileStore";
import StaffEducationHistoryStore from "./views/StaffEducationHistory/StaffEducationHistoryStore";
import HrDocumentTemplateStore from "./views/HrDocumentTemplate/HrDocumentTemplateStore";
import PersonBankAccountStore from "./views/PersonBankAccount/PersonBankAccountStore";
import BankStore from "./views/Bank/BankStore";
import StaffSignatureStore from "./views/HumanResourcesInformation/StaffSignatureStore";
import StaffMaternityHistoryStore from "./views/HumanResourcesInformation/StaffMaternityHistoryStore";
import StaffWorkingLocationStore from "./views/StaffWorkingLocation/StaffWorkingLocationStore";
import LeaveTypeStore from "./views/LeaveType/LeaveTypeStore";
import PersonCertificateStore from "./views/HumanResourcesInformation/PersonCertificate/PersonCertificateStore";
import ShiftChangeRequestStore from "./views/ShiftChangeRequest/ShiftChangeRequestStore";
import StaffFamilyRelationshipStore
    from "./views/HumanResourcesInformation/StaffFamilyRelationship/StaffFamilyRelationshipStore";
import StaffSalaryHistoryStore from "./views/profile/StaffSalaryHistory/StaffSalaryHistoryStore";
import HrRoleUtilsStore from "./views/HrRoleUtils/HrRoleUtilsStore";
import StaffRewardHistoryStore
    from "./views/HumanResourcesInformation/TabContainer/StaffRewardHistory/StaffRewardHistoryStore";
import StaffDisciplineHistoryStore
    from "./views/HumanResourcesInformation/TabContainer/StaffDisciplineHistory/StaffDisciplineHistoryStore";
import EvaluationTicketStore from "./views/Staff/EvaluationTicket/evaluationTicketStore";
import EvaluationItemStore from "./views/System/SystemParam/Evaluation/EvaluationItem/EvaluationItemStore";
import WorkplaceStore from "app/views/Workplace/WorkplaceStore";
import ConfirmStaffWorkScheduleStore from "./views/ConfirmStaffWorkSchedule/ConfirmStaffWorkScheduleStore";
import PayrollStore from "./views/Salary/Payroll/PayrollStore";
import InterviewScheduleStore from "./views/InterviewSchedule/InterviewScheduleStore";
import EvaluationTemplateStore from "./views/System/SystemParam/Evaluation/EvaluationTemplate/EvaluationTemplateStore";
import StaffLeaveStore from "app/views/HumanResourcesInformation/TabContainer/StaffLeave/StaffLeaveStore";
import OtherIncomeStore from "./views/HumanResourcesInformation/TabContainer/OtherIncomeTab/OtherIncomeStore";

import SystemConfigStore from "./views/SystemConfig/SystemConfigStore";
import InsurancePackageStore from "./views/InsurancePackage/InsurancePackageStore";
import StaffLabourManagementBookStore from "./views/StaffLabourManagementBook/StaffLabourManagementBookStore";
import EvaluationCandidateRoundStore from "./views/Recruitment/RecruitmentCU/EvaluationCandidateRound/EvaluationCandidateRoundStore";
import StaffInsurancePackageStore from "app/views/StaffInsurancePackage/StaffInsurancePackageStore";
import StaffLabourUtilReportStore from "./views/StaffLabourUtilReport/StaffLabourUtilReportStore";
import ContentTemplateStore from "./views/System/SystemParam/ContentTemplate/ContentTemplateStore";
import RRequestReportStore from "./views/Recruitment/RRequestReport/RRequestReportStore";
import HrResourcePlanReportStore from "./views/HrResourcePlanReport/HrResourcePlanReportStore"
import StaffInsuranceHistoryStore from "./views/HumanResourcesInformation/TabContainer/StaffInsuranceHistory/StaffInsuranceHistoryStore";
import StaffCertificateStore from "./views/StaffCertificate/StaffCertificateStore";
import StaffAnnualLeaveHistoryStore from "./views/StaffAnnualLeaveHistory/StaffAnnualLeaveHistoryStore";
import RecruitmentRequestSummaryStore from "./views/Recruitment/Summary/RecruitmentRequestSummaryStore";

export const store = {
    colorsStore: new ColorsStore(),
    academicStore: new AcademicStore(),
    roleStore: new RoleStore(),
    systemConfigStore: new SystemConfigStore(),
    ethnicsStore: new EthnicsStore(),
    religionStore: new ReligionStore(),
    countryStore: new CountryStore(),
    staffStore: new StaffStore(),
    userStore: new UserStore(),
    positionTitleStore: new PositionTitleStore(),
    salaryIncrementStore: new SalaryIncrementStore(),
    certificateStore: new CertificateStore(),
    civilServantTypeStore: new CivilServantTypeStore(),
    rewardStore: new RewardStore(),
    contractTypeStore: new ContractTypeStore(),
    staffWorkingHistoryStore: new StaffWorkingHistoryStore(),
    employeeStatusStore: new EmployeeStatus(),
    locationStore: new LocationStore(),
    civilServantCategoryStore: new CivilServantCategoryStore(),
    professionStore: new ProfessionStore(),
    gradeStore: new GradeStore(),
    staffEducationHistoryStore: new StaffEducationHistoryStore(),
    educationDegreeStore: new EducationDegreeStore(),
    educationTypeStore: new EducationTypeStore(),
    specialityStore: new SpecialityStore(),
    trainingBaseStore: new TrainingBaseStore(),
    professionalDegreeStore: new ProfessionalDegreeStore(),
    informaticDegreeStore: new InformaticDegreeStore(),
    politicalTheoryLevelStore: new PoliticaltheoryLevel(),
    stateManagementLevelStore: new StateManagementLevel(),
    workplaceStore: new WorkplaceStore(),
    educationalManagementLevelStore: new EducationalManagementLevel(),
    otherLanguageStore: new OtherLanguageStore(),
    titleConferredStore: new TitleConferredStore(),
    familyRelationshipStore: new FamilyRelationship(),
    projectStore: new ProjectStore(),
    workingStatusStore: new WorkingStatusStore(),
    evaluationCandidateRoundStore: new EvaluationCandidateRoundStore(),
    timeSheetDetailsStore: new TimeSheetDetailsStore(),
    timeSheetDetailStore: new TimeSheetDetailStore(),
    ShiftRegistrationStore: new ShiftRegistrationStore(),
    dashboardStore: new DashboardStore(),
    shiftWorkStore: new ShiftWorkStore(),
    publicHolidayDateStore: new PublicHolidayDateStore(),
    profileStore: new ProfileStore(),
    disciplineStore: new DisciplineStore(),
    staffAllowanceStore: new StaffAllowanceStore(),
    allowancePolicyStore: new AllowancePolicyStore(),
    allowanceStore: new AllowanceStore(),
    allowanceTypeStore: new AllowanceTypeStore(),
    administrativeUnitStore: new AdministrativeUnitStore(),
    taskStore: new TaskStore(),
    evaluationItemStore: new EvaluationItemStore(),
    globalPropertyStore: new GlobalPropertyStore(),
    calendarStore: new CalendarStore(),
    staffSignatureStore: new StaffSignatureStore(),
    staffMaternityHistoryStore: new StaffMaternityHistoryStore(),
    assetManagementStore: AssetManagementStore,
    taskHistoryStore: new TaskHistoryStore(),
    projectActivityStore: new ProjectActivityStore(),
    bankStore: new BankStore(),
    staffLeaveStore: new StaffLeaveStore(),
    otherIncomeStore: new OtherIncomeStore(),

    disciplinaryReasonStore: new DisciplinaryReasonStore(),
    refusalReasonStore: new RefusalReasonStore(),
    deferredTypeStore: new DeferredTypeStore(),
    transferTypeStore: new TransferTypeStore(),
    staffTypeStore: new StaffTypeStore(),
    addendumTypeStore: new AddendumTypeStore(),
    leavingJobReasonStore: new LeavingJobReasonStore(),
    positionStore: new PositionStore(),
    hrDocumentItemStore: new HRDocumentItemStore(),
    hrDocumentTemplateStore: new HrDocumentTemplateStore(),
    insurancePackageStore: new InsurancePackageStore(),
    staffPositionStore: new StaffPositionStore(),
    personBankAccountStore: new PersonBankAccountStore(),
    personCertificateStore: new PersonCertificateStore(),
    staffSalaryHistoryStore: new StaffSalaryHistoryStore(),
    staffFamilyRelationshipStore: new StaffFamilyRelationshipStore(),
    hrRoleUtilsStore: new HrRoleUtilsStore(),
    interviewScheduleStore: new InterviewScheduleStore(),
    staffRewardHistoryStore: new StaffRewardHistoryStore(),
    staffDisciplineHistoryStore: new StaffDisciplineHistoryStore(),
    staffLabourManagementBookStore: new StaffLabourManagementBookStore(),
    staffInsurancePackageStore: new StaffInsurancePackageStore(),
    staffLabourUtilReportStore: new StaffLabourUtilReportStore(),
    staffCertificateStore: new StaffCertificateStore(),

    // organization structure
    organizationalChartDataStore: new OrganizationalChartDataStore(),
    organizationBranchStore: new OrganizationBranchStore(),
    organizationStore: new OrganizationStore(),
    departmentTypeStore: new DepartmentTypeStore(),
    departmentStore: new DepartmentStore(),
    departmentV2Store: new DepartmentV2Store(),
    departmentGroupStore: new DepartmentGroupStore(),
    positionRoleStore: new PositionRoleStore(),
    positionTitleV2Store: new PositionTitleV2Store(),
    hrDepartmentIpStore: new HrDepartmentIpStore(),
    leaveTypeStore: new LeaveTypeStore(),
    staffIpKeepingStore: new StaffIpKeepingStore(),
    staffLabourAgreementStore: new StaffLabourAgreementStore(),
    rankTitleStore: new RankTitleStore(),
    hrResourcePlanStore: new HrResourcePlanStore(),
    hrResourcePlanReportStore: new HrResourcePlanReportStore(),
    staffDocumentItemStore: new StaffDocumentItemStore(),
    staffWorkingLocationStore: new StaffWorkingLocationStore(),

    // timekeeping
    timeKeepStore: new TimeKeepStore(),
    timeSheetStore: new TimeSheetStore(),
    timekeepingReportStore: new TimekeepingReportStore(),
    staffWorkScheduleCalendarStore: new StaffWorkScheduleCalendarStore(),
    staffWorkScheduleStore: new StaffWorkScheduleStore(),
    confirmOvertimeStore: new ConfirmOvertimeStore(),
    leaveRequestStore: new LeaveRequestStore(),
    shiftChangeRequestStore: new ShiftChangeRequestStore(),
    overtimeRequestStore: new OvertimeRequestStore(),

    // recruitment stores
    recruitmentRequestStore: new RecruitmentRequestStore(),
    recruitmentRequestSummaryStore: new RecruitmentRequestSummaryStore(),
    recruitmentPlanStore: new RecruitmentPlanStore(),
    recruitmentStore: new RecruitmentStore(),
    recruitmentExamTypeStore: new RecruitmentExamTypeStore(),
    rRequestReportStore: new RRequestReportStore(),

    // candidate stores
    candidateStore: new CandidateStore(),
    examCandidateStore: new ExamCandidateStore(),
    passedCandidateStore: new PassedCandidateStore(),
    waitingJobCandidateStore: new WaitingJobCandidateStore(),
    notComeCandidateStore: new NotComeCandidateStore(),
    onboaredCandidateStore: new OnboaredCandidateStore(),
    exportCandidateStore: new ExportCandidateStore(),
    candidateRecruitmentRoundStore: new CandidateRecruitmentRoundStore(),

    //budget
    budgetStore: new BudgetStore(),
    budgetCategoryStore: new BudgetCategoryStore(),
    voucherStore: new VoucherStore(),
    reportStore: new ReportStore(),

    // salary stores
    salaryTypeStore: new SalaryTypeStore(),
    salaryConfigStore: new SalaryConfigStore(),
    salaryItemStore: new SalaryItemStore(),
    popupStaffStore: new PopupStaffStore(),
    salaryTemplateStore: new SalaryTemplateStore(),
    staffSalaryTemplateStore: new StaffSalaryTemplateStore(),
    popupStaffSalaryTemplateStore: new PopupStaffSalaryTemplateStore(),
    salaryUnitStore: new SalaryUnitStore(),
    salaryAreaStore: new SalaryAreaStore(),
    salaryPeriodStore: new SalaryPeriodStore(),
    salaryResultStore: new SalaryResultStore(),
    salaryResultDetailStore: new SalaryResultDetailStore(),
    salaryStaffPayslipStore: new SalaryStaffPayslipStore(),
    staffSalaryItemValueStore: new StaffSalaryItemValueStore(),
    salaryTemplateItemStore: new SalaryTemplateItemStore(),
    salaryAutoMapStore: new SalaryAutoMapStore(),
    payrollStore: new PayrollStore(),

    // KPI
    KPIStore: new KPIStore(),
    KPIResultStore: new KPIResultStore(),
    salaryOutcomeStore: new SalaryOutcomeStore(),

    // social insurance
    staffSocialInsuranceStore: new StaffSocialInsuranceStore(),
    staffAdvancePaymentStore: new StaffAdvancePaymentStore(),
    staffHasSocialInsuranceStore: new StaffHasSocialInsuranceStore(),
    staffInsuranceHistoryStore: new StaffInsuranceHistoryStore(),
    hrIntroduceCostStore: new HrIntroduceCostStore(),
    evaluationTicketStore: new EvaluationTicketStore(),
    confirmStaffWorkScheduleStore: new ConfirmStaffWorkScheduleStore(),
    evaluationTemplateStore: new EvaluationTemplateStore(),
    contentTemplateStore: new ContentTemplateStore(),
    staffAnnualLeaveHistoryStore: new StaffAnnualLeaveHistoryStore(),

};

export const StoreContext = createContext(store);

export function useStore() {
    return useContext(StoreContext);
}
