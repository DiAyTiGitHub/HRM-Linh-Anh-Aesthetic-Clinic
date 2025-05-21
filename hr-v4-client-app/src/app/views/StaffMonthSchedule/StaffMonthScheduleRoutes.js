import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
import { withTranslation } from "react-i18next";

const StaffMonthScheduleIndex = EgretLoadable({
  loader: () => import("./StaffMonthScheduleIndex"),
});

// const ViewComponentUser = StaffMonthScheduleIndex;
const ViewComponent = withTranslation()(StaffMonthScheduleIndex);

const StaffMonthScheduleRoutes = [
  {
    path: ConstantList.ROOT_PATH + "staff-month-schedule-calendar/:staffId",
    exact: true,
    component: ViewComponent,
  },
  {
    path: ConstantList.ROOT_PATH + "staff-month-schedule-calendar",
    exact: true,
    component: ViewComponent,
  },
];

export default StaffMonthScheduleRoutes;
