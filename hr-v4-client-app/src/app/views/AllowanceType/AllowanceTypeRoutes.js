import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const AllowanceTypeIndex = EgretLoadable({
  loader: () => import("./AllowanceTypeIndex"),
});
const ViewComponent = AllowanceTypeIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "salary/allowance-type",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
