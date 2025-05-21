import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const HrResourcePlanIndex = EgretLoadable({
  loader: () => import("./HrResourcePlanIndex"),
});
const ViewComponent = HrResourcePlanIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "organization/hr-resource-plan",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
