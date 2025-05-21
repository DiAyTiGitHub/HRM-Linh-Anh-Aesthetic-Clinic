import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
const StateManagementLevelIndex = EgretLoadable({
  loader: () => import("./StateManagementLevelIndex"),
});
const ViewComponent = StateManagementLevelIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/stateManagementLevel",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
