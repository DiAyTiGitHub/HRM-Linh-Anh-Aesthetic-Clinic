import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const AllowancePolicyIndex = EgretLoadable({
  loader: () => import("./AllowancePolicyIndex"),
});
const ViewComponent = AllowancePolicyIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "salary/allowance-policy",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;