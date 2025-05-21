import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const TimeSheetDetailIndex = EgretLoadable({
  loader: () => import("./TimeSheetDetailIndex"),
});
const ViewComponent = TimeSheetDetailIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "time-sheet-detail/:id",
    exact: true,
    component: ViewComponent,
  },
  {
    path: ConstantList.ROOT_PATH + "time-sheet-detail",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;