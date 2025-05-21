import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const DashboardIndex = EgretLoadable({
  loader: () => import("./DashboardIndex"),
});


const TimeSheetSummaryIndex = EgretLoadable({
  loader: () => import("./TimesheetSummaryIndex"),
});

const DashboardProjectIndex = EgretLoadable({
  loader: () => import("./DashboardProjectIndex"),
});

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "dashboard",
    exact: true,
    component: DashboardIndex,
  },
  {
    path: ConstantList.ROOT_PATH + "dashboard-project",
    exact: true,
    component: DashboardProjectIndex, 
  },
  {
    path: ConstantList.ROOT_PATH + "dashboard-timesheet",
    exact: true,
    component: TimeSheetSummaryIndex,
  },
  {
    path: ConstantList.ROOT_PATH + "dashboard-timekeeping",
    exact: true,
    component: EgretLoadable({ loader: () => import("./TimekeepingSummary") })
  },
];

export default Routes;
