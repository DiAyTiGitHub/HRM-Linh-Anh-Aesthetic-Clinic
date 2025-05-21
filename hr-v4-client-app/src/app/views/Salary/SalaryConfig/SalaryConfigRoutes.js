import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./SalaryConfigIndex"),
});

const ViewComponent = RenderScreen;

const SalaryConfigRoutes = [
  {
    path: ConstantList.ROOT_PATH + "salary/salary-config",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
];

export default SalaryConfigRoutes;
