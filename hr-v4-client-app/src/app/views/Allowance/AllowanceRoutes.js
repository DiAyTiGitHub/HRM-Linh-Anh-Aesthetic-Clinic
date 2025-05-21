import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const AllowanceIndex = EgretLoadable({
  loader: () => import("./AllowanceIndex"),
});
const ViewComponent = AllowanceIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "salary/allowance",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
