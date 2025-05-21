import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "timeSheet/period_setting",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./PeriodSetting/PeriodSettingIndex"),
      }),
  },
  {
    path: ConstantList.ROOT_PATH + "timeSheet/leave_list",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./LeaveList/LeaveListIndex"),
      }),
  },
  {
    path: ConstantList.ROOT_PATH + "timeSheet/schedule_manager",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./ScheduleManager/ScheduleManagerIndex"),
      }),
  },
  {
    path: ConstantList.ROOT_PATH + "timeSheet/maternity_leave",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./MaternityLeave/MaternityLeaveIndex"),
      }),
  },
  {
    path: ConstantList.ROOT_PATH + "timeSheet/analyze_data",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./AnalyzeData/AnalyzeDataIndex"),
      }),
  },
  {
    path: ConstantList.ROOT_PATH + "timeSheet/timesheet_table",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./TimesheetTable/TimesheetTableIndex"),
      }),
  },
  {
    path: ConstantList.ROOT_PATH + "timeSheet/leave_type",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./LeaveType/LeaveTypeIndex"),
      }),
  },
  {
    path: ConstantList.ROOT_PATH + "timeSheet/overtime_type",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./OvertimeType/OvertimeTypeIndex"),
      }),
  },
  {
    path: ConstantList.ROOT_PATH + "timeSheet/holiday_list",
    exact: true,
    component: EgretLoadable({
        loader: () => import("./HolidayList/HolidayListIndex"),
      }),
  },
];

export default Routes;
