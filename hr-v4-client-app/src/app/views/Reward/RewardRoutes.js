import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const ViewComponent = EgretLoadable({ loader: () => import("./RewardIndex") });
const ViewFormComponent = EgretLoadable({ loader: () => import("./RewardForm") });

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/staff/reward",
    exact: true,
    component: ViewComponent,
  },
  {
    path: ConstantList.ROOT_PATH + "category/staff/reward-form",
    exact: true,
    component: ViewFormComponent,
  },
];

export default Routes;
