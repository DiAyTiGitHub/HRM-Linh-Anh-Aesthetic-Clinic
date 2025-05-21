import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const TimeSheetDetailsIndex = EgretLoadable({
  loader: () => import("./TimeSheetDetailsIndex"),
});

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "timesheetDetails/list/:id",
    component: TimeSheetDetailsIndex,
  },

  {
    path: ConstantList.ROOT_PATH + "timesheetDetails/list",
    component: TimeSheetDetailsIndex,
  }
];

export default Routes;
