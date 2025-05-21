import ConstantList from "./appConfig";
import LocalConstants from "./LocalConstants";

const currentLocation = window.location.href;
console.log("currentLocation", currentLocation);
// const showWorkManagementMenu = currentLocation.includes("http://localhost:3000/");
const showWorkManagementMenu = currentLocation.includes("https://hr.smarthospital247.com");

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

export const navigations = [
  // // Tổng quan
  // {
  //     name: "navigation.dashboard.title",
  //     icon: "space_dashboard",
  //     isVisible: true,
  //     color: "#5898BF",
  //     children: [
  //         {
  //             name: "navigation.dashboard.statisticsGeneral",
  //             icon: "",
  //             path: ConstantList.ROOT_PATH + "dashboard",
  //             isVisible: true,
  //         },
  //         {
  //             name: "navigation.statisticsByProject",
  //             icon: "",
  //             path: ConstantList.ROOT_PATH + "dashboard-project",
  //             isVisible: true,
  //         },
  //         // {
  //         //   name: "navigation.statisticsByTimeSheet",
  //         //   icon: "",
  //         //   path: ConstantList.ROOT_PATH + "dashboard-timesheet",
  //         //   isVisible: true,
  //         // },
  //         // {
  //         //     name: "navigation.statisticsTimekeeping",
  //         //     icon: "",
  //         //     path: ConstantList.ROOT_PATH + "dashboard-timekeeping",
  //         //     auth: [ROLE_ADMIN, HR_MANAGER],
  //         //     isVisible: true,
  //         // },
  //     ],
  // },

  // Cơ cấu tổ chức
  {
    name: "navigation.organization.title",
    // icon: "supervisor_account",
    color: "#007fff",
    icon: "account_tree",
    auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, IS_POSITION_MANAGER],
    isVisible: true,
    children: [
      {
        name: "navigation.organization.organizationalChart",
        icon: "",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW],
        path: ConstantList.ROOT_PATH + "organization/diagram",
        isVisible: true,
      },
      {
        name: "Cây tổ chức",
        icon: "",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW],
        path: ConstantList.ROOT_PATH + "organization/tree",
        isVisible: true,
      },
      {
        name: "navigation.category.staff.listPositions",
        icon: "",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW],
        className: "btn-link-black",
        title: "Quản lý danh mục vị trí",
        path: ConstantList.ROOT_PATH + "category/staff/position",
        isVisible: true,
      },

      // {
      //   name:"Yêu cầu định biên",
      //   isVisible:true,
      //   auth:[ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, IS_POSITION_MANAGER],
      //   path:ConstantList.ROOT_PATH + "organization/hr-resource-plan",
      //   className:"btn-link-black",
      //   title:"Yêu cầu định biên",
      // },

      {
        //name: "navigation.organization.HrResourcePlan",
        name: "navigation.hrResourcePlan.title",
        isVisible: true,
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, IS_POSITION_MANAGER],
        path: ConstantList.ROOT_PATH + "organization/hr-resource-plan-report",
        className: "btn-link-black",
        title: "Định biên nhân viên",
      },
      {
        name: "navigation.organizationalDirectory.title",
        //name: "Danh mục tổ chức",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW],
        isVisible: true,
        children: [
          {
            name: "navigation.administration.organization",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/organization",
            className: "btn-link-black",
            // icon: <BusinessIcon />, // Added icon
            title: "Quản lý các thông tin của các công ty, tổ chức",
            // auth: [ROLE_ADMIN],
          },
          {
            name: "navigation.category.staff.departments",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "hr-department",
            className: "btn-link-black",
            title: "Quản lý danh mục phòng ban",
          },
          {
            name: "Nhóm ngạch",
            icon: "",
            path: ConstantList.ROOT_PATH + "organization/group-position-title",
            isVisible: true,
            title: "Nhóm ngạch",
          },
          {
            name: "navigation.organization.rankTitle",
            icon: "",
            path: ConstantList.ROOT_PATH + "organization/rank-title",
            isVisible: true,
          },
          {
            name: "navigation.organization.positionTitleV2",
            icon: "",
            path: ConstantList.ROOT_PATH + "organization/position-title",
            isVisible: true,
          },

          {
            name: "navigation.organization.diagramList",
            icon: "",
            path: ConstantList.ROOT_PATH + "organization/diagram-list",
            isVisible: true,
          },
          {
            name: "navigation.organization.departmentType",
            icon: "",
            path: ConstantList.ROOT_PATH + "organization/department-type",
            isVisible: true,
          },
          // {
          //     name: "navigation.category.staff.departmentGroup",
          //     icon: "",
          //     path: ConstantList.ROOT_PATH + "category/staff/department-group",
          //     isVisible: true,
          // },
        ],
      },
    ],
  },

  // Tuyển dụng
  {
    name: "navigation.recruitment.title",
    color: "#D16958",
    icon: "vertical_split",
    auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_RECRUITMENT, HR_APPROVAL_RECRUITMENT_REQUEST, HR_CREATE_RECRUITMENT_REQUEST, HR_VIEW_RECRUITMENT_REQUEST],
    isVisible: true,
    children: [
      {
        name: "navigation.recruitment.request",
        icon: "",
        path: ConstantList.ROOT_PATH + "recruitment-request-v2",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_RECRUITMENT, HR_APPROVAL_RECRUITMENT_REQUEST, HR_CREATE_RECRUITMENT_REQUEST, HR_VIEW_RECRUITMENT_REQUEST],
        isVisible: true,
      },
      {
        name: "navigation.recruitment.plan",
        icon: "",
        path: ConstantList.ROOT_PATH + "recruitment-plan-v2",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_RECRUITMENT, HR_APPROVAL_RECRUITMENT_REQUEST],
        isVisible: true,
      },
      {
        name: "navigation.staff.applicantProfile",
        icon: "",
        path: ConstantList.ROOT_PATH + "candidate",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_RECRUITMENT, HR_APPROVAL_RECRUITMENT_REQUEST],
        isVisible: true,
      },
      // {
      //     name: "navigation.recruitment.exam",
      //     icon: "",
      //     path: ConstantList.ROOT_PATH + "exam-candidate",
      //     auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_RECRUITMENT, HR_APPROVAL_RECRUITMENT_REQUEST],
      //     isVisible: true,
      // },
      // {
      //     name: "navigation.recruitment.successfulProfile",
      //     icon: "",
      //     path: ConstantList.ROOT_PATH + "passed-candidate",
      //     auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_RECRUITMENT, HR_APPROVAL_RECRUITMENT_REQUEST],
      //     isVisible: true,
      // },
      // {
      //     name: "Lịch phỏng vấn",
      //     icon: "",
      //     path: ConstantList.ROOT_PATH + "interview-schedule",
      //     auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_RECRUITMENT],
      //     isVisible: true,
      // },

      {
        name: "navigation.personnel.employeeOnboarding",
        icon: "",
        isVisible: true,
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_RECRUITMENT],
        children: [
          {
            name: "navigation.personnel.pending",
            icon: "",
            path: ConstantList.ROOT_PATH + "waiting-job-candidate",
            isVisible: true,
          },
          {
            name: "navigation.personnel.failingWord",
            icon: "",
            path: ConstantList.ROOT_PATH + "not-come-candidate",
            isVisible: true,
          },
          {
            name: "navigation.personnel.candidateWorking",
            icon: "",
            path: ConstantList.ROOT_PATH + "onboarded-candidate",
            isVisible: true,
          },
          {
            name: "navigation.personnel.employeesReporting",
            icon: "",
            path: ConstantList.ROOT_PATH + "export-candidate-report",
            isVisible: true,
          },
        ],
      },

      {
        name: "Báo cáo tuyển dụng",
        isVisible: true,
        children: [
          {
            name: "BC theo yêu cầu tuyển dụng",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "recruitment-request-report",
            className: "btn-link-black",
            // icon: <FlagSharpIcon />, // Added icon
            title: "Quản lý danh mục loại kiểm tra",
          },
          {
            name: "BC ứng viên theo Y/C tuyển dụng",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "recruitment-request-summary",
            className: "btn-link-black",
          },
        ],
      },

      {
        name: "navigation.recruitment.category",
        isVisible: true,
        children: [
          {
            name: "Loại kiểm tra",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/exam-category",
            className: "btn-link-black",
            // icon: <FlagSharpIcon />, // Added icon
            title: "Quản lý danh mục loại kiểm tra",
          },
        ],
      },
    ],
  },

  // Quản lí nhân viên
  {
    name: "navigation.staff.title",
    icon: "supervisor_account",
    color: "#D16958",
    auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW],
    isVisible: true,
    children: [
      {
        name: "navigation.staff.staffManagement",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW],
        icon: "",
        path: ConstantList.ROOT_PATH + "staff/all",
        isVisible: true,
      },
      {
        name: "navigation.staff.evaluationForm",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_USER],
        icon: "",
        path: ConstantList.ROOT_PATH + "staff-evaluation-ticket",
        isVisible: true,
      },
      // {
      //     name: "navigation.staff.position",
      //     auth: [ROLE_ADMIN, HR_MANAGER],
      //     icon: "",
      //     path: ConstantList.ROOT_PATH + "position-staff",
      //     isVisible: true,
      // },

      {
        name: "navigation.staff.staffLabourAgreement",
        icon: "",
        path: ConstantList.ROOT_PATH + "staff-labour-agreement",
        isVisible: true,
      },
      {
        name: "navigation.staff.introduceCost",
        icon: "",
        path: ConstantList.ROOT_PATH + "hr-introduce-cost",
        isVisible: true,
      },
      {
        name: "navigation.staff.staffDocumentItem",
        icon: "",
        path: ConstantList.ROOT_PATH + "staff-document-item",
        isVisible: true,
      },
      {
        name: "Công cụ/Dụng cụ",
        icon: "",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW],
        path: ConstantList.ROOT_PATH + "category/asset",
        isVisible: true,
      },

      {
        name: "Chứng chỉ nhân viên",
        icon: "",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW],
        path: ConstantList.ROOT_PATH + "staff-certificate",
        isVisible: true,
      },

      {
        name: "Ngày nghỉ phép nhân viên",
        icon: "",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW],
        path: ConstantList.ROOT_PATH + "staff-annual-leave-history",
        isVisible: true,
      },

      {
        name: "navigation.category.staff.title",
        isVisible: true,
        children: [
          // {
          //     name: "navigation.organization.positionRole",
          //     icon: "",
          //     path: ConstantList.ROOT_PATH + "organization/position-role",
          //     isVisible: true,
          // },
          // {
          //   name: "navigation.category.staff.listTitles",
          //   isVisible: true,
          //   path: ConstantList.ROOT_PATH + "category/staff/list_titles",
          //   className: "btn-link-black",
          //   title: "Quản lý danh mục chức danh"
          // },

          {
            name: "navigation.category.staff.employeesType",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/staff/staff-type",
            className: "btn-link-black",
            title: "Quản lý danh mục loại nhân viên",
          },
          {
            name: "navigation.category.staff.awardType",
            isVisible: true,
            className: "btn-link-green",
            path: ConstantList.ROOT_PATH + "category/staff/reward",
            title: "Quản lý danh mục loại khen thưởng, hình thức khen thưởng dành cho nhân viên",
            // icon: <StyleIcon />, // Added icon
          },
          {
            name: "navigation.category.staff.leaveReasons",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/staff/leaving-job-reason",
            className: "btn-link-black",
            title: "Quản lý danh mục lý do nghỉ việc",
          },
          // {
          //     name: "navigation.category.staff.suspendedType",
          //     isVisible: true,
          //     path: ConstantList.ROOT_PATH + "category/staff/deferred-type",
          //     className: "btn-link-black",
          //     title: "Quản lý danh mục loại tạm hoãn",
          // },
          {
            name: "navigation.category.staff.transferType",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/staff/transfer_type",
            className: "btn-link-black",
            title: "Quản lý danh mục loại điều chuyển",
          },
          {
            name: "navigation.category.staff.typeContract",
            icon: "",
            path: ConstantList.ROOT_PATH + "category/staff/contract-type",
            isVisible: true,
            title: "Quản lý danh mục loại hợp đồng",
            className: "btn-link-blue",
            // icon: <InsertDriveFileSharpIcon />, // Added icon
          },
          // {
          //     name: "navigation.category.staff.typeAddendum",
          //     isVisible: true,
          //     path: ConstantList.ROOT_PATH + "category/staff/addendum-type",
          //     className: "btn-link-black",
          //     title: "Quản lý danh mục loại phụ lục",
          // },
          // {
          //     name: "navigation.category.staff.reasonRefusal",
          //     isVisible: true,
          //     path: ConstantList.ROOT_PATH + "category/staff/refusal-reason",
          //     className: "btn-link-black",
          //     title: "Quản lý danh mục lý do từ chối",
          // },
          {
            name: "discipline.title",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/discipline",
            className: "btn-link-black",
            // icon: <FlagSharpIcon />, // Added icon
            title: "Quản lý danh mục loại kỷ luật",
          },
          // {
          //     name: "navigation.category.staff.disciplinaryReason",
          //     isVisible: true,
          //     path: ConstantList.ROOT_PATH + "category/staff/disciplinary_reason",
          //     className: "btn-link-black",
          //     title: "Quản lý danh mục lý do kỷ luật",
          // },
          {
            name: "humanResourcesInformation.familyRelationships",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/familyRelationship",
            className: "btn-link-black",
            // icon: <PersonAddSharpIcon />, // Added icon
            title: "Quản lý danh mục loại quan hệ của nhân viên",
          },

          {
            name: "navigation.administration.employeeStatus",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/employee-status",
            className: "btn-link-purple",
            // icon: <FormatListBulletedIcon />, // Added icon
            title: "Quản lý danh sách tình trạng nhân viên",
          },

          {
            name: "navigation.staff.hr-document-template",
            icon: "",
            path: ConstantList.ROOT_PATH + "hr-document-template",
            isVisible: true,
          },

          {
            name: "Tài liệu/Hồ sơ",
            icon: "",
            path: ConstantList.ROOT_PATH + "hr-document-item",
            isVisible: true,
          },
          {
            name: "Gói bảo hiểm",
            icon: "",
            path: ConstantList.ROOT_PATH + "insurance-package",
            isVisible: true,
          },
        ],
      },

      // {
      //   name: "navigation.staff.staffManagement",
      //   icon: "",
      //   isVisible: true,
      //   children: [
      //     {
      //       name: "navigation.staff.all",
      //       icon: "",
      //       path: ConstantList.ROOT_PATH + "staff/all",
      //       isVisible: true,
      //     },
      //     {
      //       name: "navigation.staff.officialMembers",
      //       icon: "",
      //       path: ConstantList.ROOT_PATH + "staff/officialMembers",
      //       isVisible: true,
      //     },
      //     {
      //       name: "navigation.staff.expertAdvice",
      //       icon: "",
      //       path: ConstantList.ROOT_PATH + "staff/expertAdvice",
      //       isVisible: true,
      //     },
      //     {
      //       name: "navigation.staff.intern",
      //       icon: "",
      //       path: ConstantList.ROOT_PATH + "staff/studentInternships",
      //       isVisible: true,
      //     },
      //   ]
      // },

      // {
      //   name: "navigation.staff.transfer.title",
      //   isVisible: true,
      //   children: [
      //     {
      //       name: "navigation.staff.transfer.workingProcess",
      //       isVisible: true,
      //       path: ConstantList.ROOT_PATH + "staff/working-process"
      //     }
      //   ]
      // },
      // {
      //   name: "navigation.staff.contractManagement.title",
      //   isVisible: true,
      //   children: [
      //     {
      //       name: "navigation.staff.staffLabourAgreement",
      //       icon: "",
      //       path: ConstantList.ROOT_PATH + "staff-labour-agreement",
      //       isVisible: true,
      //     },
      //     {
      //       name: "navigation.staff.contractManagement.listOfContract",
      //       isVisible: true,
      //       path: ConstantList.ROOT_PATH + "personnel/list_contract"
      //     },
      //     {
      //       name: "navigation.staff.contractManagement.contractAddendum",
      //       isVisible: true,
      //       path: ConstantList.ROOT_PATH + "personnel/addendum_contract"
      //     },
      //     {
      //       name: "navigation.staff.contractManagement.listOfExpiredContract",
      //       isVisible: true,
      //       path: ConstantList.ROOT_PATH + "staff/contract_expire"
      //     }
      //   ]
      // },
      // {
      //   name: "navigation.staff.disciplinaryManagement.title",
      //   isVisible: true,
      //   children: [
      //     {
      //       name: "navigation.staff.disciplinaryManagement.listOfDiscipline",
      //       isVisible: true,
      //       path: ConstantList.ROOT_PATH + "staff/list_discipline"
      //     },
      //     {
      //       name: "navigation.staff.disciplinaryManagement.reportingDiscipline",
      //       isVisible: true,
      //       path: ConstantList.ROOT_PATH + "staff/reporting_discipline"
      //     }
      //   ]
      // },
      // {
      //   name: "navigation.staff.rewardManagement.title",
      //   isVisible: true,
      //   children: [
      //     {
      //       name: "navigation.staff.rewardManagement.listAward",
      //       isVisible: true,
      //       path: ConstantList.ROOT_PATH + "staff/list_award"
      //     },
      //     {
      //       name: "navigation.staff.rewardManagement.reportingAward",
      //       isVisible: true,
      //       path: ConstantList.ROOT_PATH + "staff/reporting_award"
      //     }
      //   ]
      // },
      // {
      //   name: "navigation.staff.resignationManagement.title",
      //   isVisible: true,
      //   children: [
      //     {
      //       name: "navigation.staff.resignationManagement.registerResignation",
      //       isVisible: true,
      //       path: ConstantList.ROOT_PATH + "staff/register_resignation"
      //     },
      //     {
      //       name: "navigation.staff.resignationManagement.listOfLeavedEmployee",
      //       isVisible: true,
      //       path: ConstantList.ROOT_PATH + "staff/employee_leave"
      //     },
      //     {
      //       name: "navigation.staff.resignationManagement.listWaitLeaveEmployee",
      //       isVisible: true,
      //       path: ConstantList.ROOT_PATH + "staff/list_wait_leave_employee"
      //     }
      //   ]
      // },
      // {
      //   name: "navigation.staff.suspensionManagement.title",
      //   isVisible: true,
      //   children: [
      //     {
      //       name: "navigation.staff.suspensionManagement.listOfRegistrationSuspended",
      //       isVisible: true,
      //       path: ConstantList.ROOT_PATH + "staff/registration_suspended"
      //     },
      //     {
      //       name: "navigation.staff.suspensionManagement.listOfRegistrationRework",
      //       isVisible: true,
      //       path: ConstantList.ROOT_PATH + "staff/registration_rework"
      //     },
      //     {
      //       name: "navigation.staff.suspensionManagement.reportingRework",
      //       isVisible: true,
      //       path: ConstantList.ROOT_PATH + "staff/reporting_rework"
      //     }
      //   ]
      // },
      // {
      //   name: "navigation.staff.relative.title",
      //   isVisible: true,
      //   children: [
      //     {
      //       name: "navigation.staff.relative.listOfRelative",
      //       isVisible: true,
      //       path: ConstantList.ROOT_PATH + "staff/relative"
      //     }
      //   ]
      // },
      // {
      //   name: "navigation.staff.dependent.title",
      //   isVisible: true,
      //   children: [
      //     {
      //       name: "navigation.staff.dependent.dependent",
      //       isVisible: true,
      //       path: ConstantList.ROOT_PATH + "staff/dependent"
      //     },
      //     {
      //       name: "navigation.staff.dependent.totalDependents",
      //       isVisible: true,
      //       path: ConstantList.ROOT_PATH + "staff/total_dependents"
      //     }
      //   ]
      // }
    ],
  },

  // Chấm công
  {
    name: "navigation.timeKeeping.title",
    icon: "token",
    color: "#E2A845",
    auth: [ROLE_ADMIN, HR_MANAGER, HR_USER, HR_ASSIGNMENT_ROLE, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
    // path: ConstantList.ROOT_PATH + "timeKeeping",
    isVisible: true,
    children: [
      {
        name: "navigation.timeSheet.shiftWork",
        isVisible: true,
        path: ConstantList.ROOT_PATH + "category/shift-work",
        className: "btn-link-pink",
        // icon: <QueryBuilderRoundedIcon />, // Added icon
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
        title: "Quản lý ca, kíp làm việc",
      },

      {
        name: "Yêu cầu nghỉ phép",
        isVisible: true,
        path: ConstantList.ROOT_PATH + "category/leave-request",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_ASSIGNMENT_ROLE, HR_STAFF_VIEW, HR_USER, HR_COMPENSATION_BENEFIT],
        className: "btn-link-pink",
      },
      // {
      //     name: "navigation.shiftRegistration.title",
      //     icon: "",
      //     path: ConstantList.ROOT_PATH + "shift-registration",
      //     auth: [ROLE_ADMIN, HR_MANAGER, HR_ASSIGNMENT_ROLE, HR_USER, HR_STAFF_VIEW],
      //     isVisible: true,
      // },
      {
        name: "navigation.timeSheet.staffWorkSchedule",
        isVisible: true,
        path: ConstantList.ROOT_PATH + "staff-work-schedule",
        className: "btn-link-pink",
        // icon: <QueryBuilderRoundedIcon />, // Added icon
        auth: [ROLE_ADMIN, HR_MANAGER, HR_ASSIGNMENT_ROLE, HR_USER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
        title: "Phân công lịch làm việc cho nhân viên",
      },
      {
        name: "Yêu cầu đổi ca làm",
        isVisible: true,
        path: ConstantList.ROOT_PATH + "shift-change-request",
        className: "btn-link-pink",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_USER, HR_ASSIGNMENT_ROLE, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
      },
      {
        name: "navigation.timeSheet.workScheduleCalendar",
        isVisible: true,
        path: ConstantList.ROOT_PATH + "work-schedule-calendar",
        className: "btn-link-pink",
        // icon: <QueryBuilderRoundedIcon />, // Added icon
        auth: [ROLE_ADMIN, HR_MANAGER, HR_USER, HR_ASSIGNMENT_ROLE, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
        title: "Bảng phân ca làm việc",
      },
      // {
      //   name: "navigation.timeKeeping.title",
      //   icon: "",
      //   path: ConstantList.ROOT_PATH + "timeKeeping",
      //   isVisible: true,
      //   auth: [HR_MANAGER, HR_USER],
      // },

      {
        name: "navigation.timeSheetDetail.title",
        icon: "",
        path: ConstantList.ROOT_PATH + "time-sheet-detail",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_USER, HR_ASSIGNMENT_ROLE, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
        isVisible: true,
      },

      {
        name: "navigation.timekeepingReport.title",
        icon: "",
        path: ConstantList.ROOT_PATH + "time-keeping-report",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_USER, HR_ASSIGNMENT_ROLE, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
        isVisible: true,
      },
      {
        name: "navigation.historyTimeSheetDetail.title",
        icon: "",
        path: ConstantList.ROOT_PATH + "history-time-sheet-detail",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_USER, HR_ASSIGNMENT_ROLE, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
        isVisible: true,
      },
      {
        //name: "Xác nhận lịch làm việc",
        name: "navigation.confirmStaffworkSchedule.title",
        icon: "",
        path: ConstantList.ROOT_PATH + "category/confirm-staff-work-schedule",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_USER, HR_ASSIGNMENT_ROLE, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
        isVisible: true,
      },

      {
        //name: "Yêu cầu duyệt giờ làm thêm",
        name: "navigation.overtimeRequest.title",
        icon: "",
        path: ConstantList.ROOT_PATH + "category/overtime-request",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_USER, HR_ASSIGNMENT_ROLE, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
        isVisible: true,
      },
      {
        name: "navigation.confirmOvertime.title",
        icon: "",
        path: ConstantList.ROOT_PATH + "confirm-overtime",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_ASSIGNMENT_ROLE, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
        isVisible: true,
      },
      {
        name: "navigation.timeSheet.publicHolidayDate",
        isVisible: true,
        path: ConstantList.ROOT_PATH + "category/public-holiday-date",
        className: "btn-link-pink",
        // icon: <QueryBuilderRoundedIcon />, // Added icon
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
      },

      {
        name: "Danh mục chấm công",
        isVisible: true,
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
        children: [
          {
            name: "navigation.organization.hrDepartmentIp",
            icon: "",
            path: ConstantList.ROOT_PATH + "hr-department-ip",
            isVisible: true,
            auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
          },
          // {
          //     name: "navigation.organization.staffIpKeeping",
          //     icon: "",
          //     path: ConstantList.ROOT_PATH + "staff-ip-keeping",
          //     isVisible: true,
          //     auth: [ROLE_ADMIN, HR_MANAGER],
          // },
          {
            name: "navigation.timeSheet.leaveType",
            icon: "",
            path: ConstantList.ROOT_PATH + "leave-type",
            isVisible: true,
            auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
          },
        ],
      },
    ],
  },

  //Lương thưởng
  {
    name: "navigation.salary",
    icon: "point_of_sale",
    isVisible: true,
    color: "#70B672",
    auth: [ROLE_ADMIN, HR_MANAGER, HR_USER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
    children: [
      {
        name: "navigation.salaryitem",
        icon: "",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
        path: ConstantList.ROOT_PATH + "salary/salary-item",
        isVisible: true,
      },

      // {
      //     name: "navigation.salaryAutoMap",
      //     icon: "",
      //     auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW],
      //     path: ConstantList.ROOT_PATH + "salary/salary-auto-map",
      //     isVisible: true,
      // },

      {
        name: "navigation.salaryTemplate.title",
        icon: "",
        path: ConstantList.ROOT_PATH + "salary/salary-template",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
        isVisible: true,
      },
      {
        name: "Mẫu bảng lương nhân viên",
        icon: "",
        path: ConstantList.ROOT_PATH + "salary/staff-salary-template",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
        isVisible: true,
      },
      {
        name: "navigation.payroll.period",
        icon: "",
        path: ConstantList.ROOT_PATH + "salary/salary-period",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
        isVisible: true,
      },
      {
        name: "navigation.advancePayment.title",
        icon: "",
        path: ConstantList.ROOT_PATH + "salary/staff-advance-payment",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT, HR_USER],
        isVisible: true,
      },

      // {
      //     name: "navigation.salaryOutcome.title",
      //     icon: "",
      //     path: ConstantList.ROOT_PATH + "salary/salary-outcome",
      //     auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW],
      //     isVisible: true,
      // },

      {
        name: "navigation.salaryResult.title",
        icon: "",
        path: ConstantList.ROOT_PATH + "salary/salary-result",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
        isVisible: true,
      },

      // {
      //     name: "navigation.allowance.title",
      //     icon: "",
      //     path: ConstantList.ROOT_PATH + "salary/allowance",
      //     auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW],
      //     isVisible: true,
      // },
      // {
      //     name: "navigation.allowancePolicy.title",
      //     icon: "",
      //     path: ConstantList.ROOT_PATH + "salary/allowance-policy",
      //     auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW],
      //     isVisible: true,
      // },

      // {
      //     name: "navigation.staffAllowance.title",
      //     icon: "",
      //     path: ConstantList.ROOT_PATH + "salary/staff-allowance",
      //     auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW],
      //     isVisible: true,
      // },

      // {
      //     name: "navigation.staffSalaryItemValue.title",
      //     icon: "",
      //     path: ConstantList.ROOT_PATH + "salary/staff-salary-item-value",
      //     auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW],
      //     isVisible: true,
      // },
      // {
      //   name: "Tính lương cho từng nhân viên",
      //   icon: "",
      //   path: ConstantList.ROOT_PATH + "salary/staff-personal-salary-item-value",
      //   auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW],
      //   isVisible: true,
      // },

      {
        name: "navigation.salaryStaffPayslip.title",
        icon: "",
        path: ConstantList.ROOT_PATH + "salary/salary-staff-payslip",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_USER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
        isVisible: true,
      },

      // {
      //     name:"navigation.payroll.category" ,
      //     isVisible:true ,
      //     auth:[ROLE_ADMIN , HR_MANAGER , HR_STAFF_VIEW] ,
      //     children:[
      //         {
      //             name:"navigation.salaryIncrement" ,
      //             icon:"" ,
      //             path:ConstantList.ROOT_PATH + "salary/salary-increment" ,
      //             isVisible:true ,
      //         } ,
      //         {
      //             name:"navigation.salaryUnit.title" ,
      //             icon:"" ,
      //             path:ConstantList.ROOT_PATH + "salary/salary-unit" ,
      //             isVisible:true ,
      //         } ,
      //         {
      //             name:"navigation.salaryArea.title" ,
      //             icon:"" ,
      //             path:ConstantList.ROOT_PATH + "salary/salary-area" ,
      //             isVisible:true ,
      //         } ,
      //         {
      //             name: "navigation.salaryType.title",
      //             icon: "",
      //             path: ConstantList.ROOT_PATH + "salary/salary-type",
      //             isVisible: true,
      //         },
      //         {
      //             name:"allowanceType.title" ,
      //             icon:"" ,
      //             path:ConstantList.ROOT_PATH + "salary/allowance-type" ,
      //             isVisible:true ,
      //         } ,
      //         {
      //             name: "navigation.salaryConfig.title",
      //             icon: "",
      //             path: ConstantList.ROOT_PATH + "salary/salary-config",
      //             isVisible: true,
      //         },
      //     ] ,
      // } ,
    ],
  },

  // {
  //     name: "navigation.kpi",
  //     icon: "equalizer",
  //     auth: [ROLE_ADMIN, HR_MANAGER],
  //     isVisible: true,
  //     children: [
  //         {
  //             name: "navigation.kpi",
  //             icon: "",
  //             path: ConstantList.ROOT_PATH + "salary/kpi",
  //             auth: [ROLE_ADMIN, HR_MANAGER],
  //             isVisible: true,
  //         },
  //         {
  //             name: "navigation.kpi-result",
  //             icon: "",
  //             path: ConstantList.ROOT_PATH + "salary/kpi-result",
  //             auth: [ROLE_ADMIN, HR_MANAGER],
  //             isVisible: true,
  //         },
  //     ]
  // },

  // Bảo hiểm
  {
    name: "navigation.insurance.root",
    color: "#007fff",
    icon: "account_tree",
    auth: [ROLE_ADMIN, HR_MANAGER, HR_USER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
    isVisible: true,
    children: [
      {
        name: "navigation.insurance.staffHasInsurance",
        isVisible: true,
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
        path: ConstantList.ROOT_PATH + "insurance/staff-has-social-insurance",
        className: "btn-link-purple",
      },
      {
        name: "navigation.insurance.staffSocialInsurance",
        isVisible: true,
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT, HR_USER],
        path: ConstantList.ROOT_PATH + "insurance/staff-social-insurance",
        className: "btn-link-purple",
      },
    ],
  },

  // Pháp chế
  {
    name: "Pháp chế",
    color: "#007fff",
    icon: "gavel",
    auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT, HR_LEGISLATION],
    isVisible: true,
    children: [
      {
        name: "Báo cáo tình hình sử dụng lao động",
        isVisible: true,
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT, HR_LEGISLATION],
        path: ConstantList.ROOT_PATH + "staff-labour-util-report",
        className: "btn-link-purple",
      },

      {
        name: "Sổ quản lý lao động",
        isVisible: true,
        auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT, HR_LEGISLATION],
        path: ConstantList.ROOT_PATH + "staff-labour-management-book",
        className: "btn-link-purple",
      },

    ],
  },

  // Công việc
  {
    name: "navigation.work.title",
    icon: "computer",
    color: "#56AED0",
    auth: [ROLE_ADMIN, HR_MANAGER, HR_USER, HR_STAFF_VIEW],
    isVisible: showWorkManagementMenu,
    children: [
      {
        name: "navigation.work.project",
        icon: "",
        path: ConstantList.ROOT_PATH + "timesheet/project",
        isVisible: showWorkManagementMenu,
      },

      {
        name: "navigation.work.task",
        icon: "",
        path: ConstantList.ROOT_PATH + "task",
        isVisible: showWorkManagementMenu,
      },
      {
        name: "navigation.work.timesheetDetails",
        icon: "",
        path: ConstantList.ROOT_PATH + "timesheetDetails/list",
        isVisible: showWorkManagementMenu,
      },
    ],
  },

  // Lịch
  // {
  //   name: "navigation.calendar",
  //   icon: "event",
  //   color: "#8779B6",
  //   path: ConstantList.ROOT_PATH + "calendar",
  //   isVisible: true,
  // },

  // Hồ sơ cá nhân
  {
    name: "Cá nhân",
    // icon: "supervisor_account",
    icon: "assignment_ind",
    color: "#8779B6",
    auth: [ROLE_ADMIN, HR_MANAGER, HR_USER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
    path: ConstantList.ROOT_PATH + "profile",
    isVisible: true,

    children: [
      {
        name: "navigation.profile",
        isVisible: true,
        path: ConstantList.ROOT_PATH + "profile",
        auth: [ROLE_ADMIN, HR_MANAGER, HR_USER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
      },
      {
        name: "navigation.staff.evaluationForm",
        auth: [HR_USER],
        icon: "",
        path: ConstantList.ROOT_PATH + "user/staff-evaluation-ticket",
        isVisible: true,
      },
      {
        // name: "navigation.timeKeeping.title",
        name: "Lịch làm việc",
        icon: "",
        path: ConstantList.ROOT_PATH + "staff-month-schedule-calendar",
        isVisible: true,
        auth: [ROLE_ADMIN, HR_MANAGER, HR_USER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
      },

      {
        name: "calendar.title",
        // icon: "event",
        color: "#8779B6",
        path: ConstantList.ROOT_PATH + "calendar",
        isVisible: true,
        auth: [HR_MANAGER, HR_USER, HR_STAFF_VIEW, HR_COMPENSATION_BENEFIT],
      },
    ],
  },

  // {
  //     name: "Vị trí cá nhân",
  //     // icon: "supervisor_account",
  //     icon: "assignment_ind",
  //     color: "#8779B6",
  //     // auth: [ROLE_ADMIN, HR_MANAGER, HR_USER],
  //     path: ConstantList.ROOT_PATH + "position",
  //     isVisible: true,
  // },

  // Ngân sách
  // {
  //     name: "navigation.budget.title",
  //     isVisible: true,
  //     icon: "wallet",
  //     color: "#CB5C88",
  //     auth: [ROLE_ADMIN],
  //     children: [
  //         {
  //             name: "navigation.budget.budget",
  //             isVisible: true,
  //             path: ConstantList.ROOT_PATH + "budget/budget",
  //         },
  //         {
  //             name: "navigation.budget.budgetCategory",
  //             isVisible: true,
  //             path: ConstantList.ROOT_PATH + "budget/budget-category",
  //         },
  //         {
  //             name: "navigation.budget.voucher",
  //             // icon: "payments",
  //             color: "#8779B6",
  //             path: ConstantList.ROOT_PATH + "budget/voucher",
  //             isVisible: true,
  //             auth: [ROLE_ADMIN],
  //         },
  //         {
  //             name: "navigation.budget.report",
  //             isVisible: true,
  //             path: ConstantList.ROOT_PATH + "budget/report",
  //         },
  //     ],
  // },

  // Quản trị
  {
    name: "navigation.administration.title",
    isVisible: true,
    icon: "token",
    color: "#CB5C88",
    auth: [ROLE_ADMIN, HR_STAFF_VIEW],
    children: [
      {
        name: "navigation.administration.accounts",
        isVisible: true,
        path: ConstantList.ROOT_PATH + "administration/accounts",
      },
      {
        name: "navigation.administration.role",
        isVisible: true,
        path: ConstantList.ROOT_PATH + "administration/roles",
      },
      {
        name: "navigation.administration.generalCatalog",
        isVisible: true,
        children: [
          {
            name: "Cấu hình hệ thống",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/system-config",
            className: "btn-link-yellow",
            // icon: <BusinessCenterIcon />, // Added icon
            title: "Quản lý danh sách trạng thái làm việc của nhân viên",
          },

          {
            name: "navigation.administration.workStatus",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/working-status",
            className: "btn-link-yellow",
            // icon: <BusinessCenterIcon />, // Added icon
            title: "Quản lý danh sách trạng thái làm việc của nhân viên",
          },

          {
            name: "navigation.administration.assets",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/asset-management/product",
            className: "btn-link-green",
            // icon: <IconAsset />, // Added icon
            title: "Quản lý công cụ/ dụng cụ",
          },

          {
            name: "navigation.administration.civilServant",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/duty/grade",
            className: "btn-link-yellow",
            // icon: <DeviceHubIcon />, // Added icon
            title: "Quản lý các danh mục công chức, bậc công chức, mã ngạch và phân loại",
          },
          {
            name: "navigation.administration.certificate",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/certificate",
            className: "btn-link-purple",
            // icon: <CardMembershipIcon />, // Added icon
            title: "Quản lý các loại chứng chỉ",
          },
          {
            name: "navigation.administration.qualification",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/degree/professional",
            className: "btn-link-green",
            // icon: <GradeSharpIcon />, // Added icon
            title: "Quản lý danh mục trình độ như: trình độ chuyển môn, lý luận chính trị,...",
          },
          {
            name: "navigation.administration.workplace",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/workplace",
            className: "btn-link-pink",
            title: "Quản lý địa điểm làm việc",
          },
          {
            name: "navigation.administration.trainingInstitution",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/educationalInstitution",
            className: "btn-link-pink",
            // icon: <HomeWorkSharpIcon />, // Added icon
            title: "Quản lý cơ sở đạo tạo",
          },
          {
            name: "navigation.administration.trainingType",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/education-type",
            className: "btn-link-pink",
            // icon: <HomeWorkSharpIcon />, // Added icon
            title: "Quản lý Loại hình đào tạo",
          },
          {
            name: "navigation.administration.academicTitle",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/academic",
            className: "btn-link-blue",
            // icon: <SchoolIcon />, // Added icon
            title: "Quản lý danh mục học hàm",
          },
          {
            name: "navigation.administration.fieldOfStudy",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/speciality",
            className: "btn-link-darkolivegreen",
            // icon: <StarOutlineSharpIcon />, // Added icon
            title: "Quản lý danh mục các chuyên ngành",
          },
          // {
          //     name: "navigation.administration.honoraryTitle",
          //     isVisible: true,
          //     path: ConstantList.ROOT_PATH + "category/titleConferred",
          //     className: "btn-link-black",
          //     // icon: <AccountBoxIcon />, // Added icon
          //     title: "Quản lý các danh hiệu được phong",
          // },
          {
            name: "navigation.administration.administrativeUnit",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/administrative-unit",
            className: "btn-link-purple",
            // icon: <ListAltIcon />, // Added icon
            title: "Quản lý danh mục đơn vị hành chính Việt Nam",
          },
          {
            name: "navigation.administration.country",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/country",
            className: "btn-link-green",
            // icon: <PublicIcon />, // Added icon
            title: "Quản lý danh mục quốc gia",
          },
          {
            name: "navigation.administration.ethnicity",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/ethnics",
            className: "btn-link-pink",
            // icon: <SupervisedUserCircleIcon />, // Added icon
            title: "Quản lý danh mục dân tộc Việt Nam",
          },
          {
            name: "navigation.administration.religion",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/religion",
            className: "btn-link-blue",
            // icon: <PublicIcon />, // Added icon
            title: "Quản lý danh mục các tôn giáo",
          },
          {
            name: "navigation.administration.profession",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/profession",
            className: "btn-link-darkolivegreen",
            // icon: <WorkSharpIcon />, // Added icon
            title: "Quản lý danh mục nghề nghiệp",
          },
          {
            name: "navigation.administration.bank",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/bank",
            className: "btn-link-darkolivegreen",
            // icon: <WorkSharpIcon />, // Added icon
            title: "Ngân hàng",
          },
          {
            name: "navigation.administration.evaluation_criteria",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/evaluation-item",
            className: "btn-link-darkolivegreen",
            // icon: <WorkSharpIcon />, // Added icon
            title: "Ngân hàng",
          },
          {
            name: "navigation.administration.evaluation_template",
            isVisible: true,
            path: ConstantList.ROOT_PATH + "category/evaluation-template",
            className: "btn-link-darkolivegreen",
            // icon: <WorkSharpIcon />, // Added icon
            title: "Ngân hàng",
          },
          {
            name: "navigation.administration.content_template",
            auth: [ROLE_ADMIN, HR_MANAGER, HR_STAFF_VIEW, HR_USER],
            icon: "",
            path: ConstantList.ROOT_PATH + "category/content-template",
            isVisible: true,
          },
        ],
      },
    ],
  },
];
