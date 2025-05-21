import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const StaffWorkScheduleCalendarIndex = EgretLoadable({
  loader: () => import("./StaffWorkScheduleCalendarIndex"),
});

const ViewComponent = StaffWorkScheduleCalendarIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "work-schedule-calendar",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;

