import { EgretLoadable } from "egret";
import Config from "../../appConfig";

const ViewComponent = EgretLoadable({ loader: () => import("./EmployeeStatusIndex") });

const Routes = [
  {
    path: Config.ROOT_PATH + "category/employee-status",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
