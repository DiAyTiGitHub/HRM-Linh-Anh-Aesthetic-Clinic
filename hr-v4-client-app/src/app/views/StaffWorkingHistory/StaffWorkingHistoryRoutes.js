import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const StaffWorkingHistoryIndex = EgretLoadable({
  loader: () => import("./StaffWorkingHistoryIndex"),
});
const ViewComponent = StaffWorkingHistoryIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "working/staff-working-history",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;