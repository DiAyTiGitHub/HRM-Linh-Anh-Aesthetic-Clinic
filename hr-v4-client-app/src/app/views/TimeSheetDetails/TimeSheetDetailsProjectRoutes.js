import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
const TimeSheetDetailsIndex = EgretLoadable({
  loader: () => import("./TimeSheetDetailsIndex"),
});
const ViewComponent = TimeSheetDetailsIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "timesheet/list?projectId=:id",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
