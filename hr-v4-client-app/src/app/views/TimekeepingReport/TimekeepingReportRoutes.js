import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const TimekeepingReportIndex = EgretLoadable({
  loader: () => import("./TimekeepingReportIndex"),
});

const ViewComponent = TimekeepingReportIndex;

const TimekeepingReportRoutes = [
  {
    path: ConstantList.ROOT_PATH + "time-keeping-report",
    exact: true,
    component: ViewComponent,
  },
];

export default TimekeepingReportRoutes;

