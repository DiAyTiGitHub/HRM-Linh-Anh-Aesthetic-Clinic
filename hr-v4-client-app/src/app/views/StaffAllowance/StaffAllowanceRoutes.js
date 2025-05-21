import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const StaffAllowanceIndex = EgretLoadable({
  loader: () => import("./StaffAllowanceIndex"),
});
const ViewComponent = StaffAllowanceIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "salary/staff-allowance",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;