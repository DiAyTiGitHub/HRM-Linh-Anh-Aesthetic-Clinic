import { EgretLoadable } from "egret";
import Config from "../../appConfig";
const ViewComponent = EgretLoadable({ loader: () => import("./WorkingStatusIndex") });

const Routes = [
  {
    path: Config.ROOT_PATH + "category/working-status",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
