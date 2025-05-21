import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./SalaryResultIndex"),
});

const ViewComponent = RenderScreen;

const SalaryResultRoutes = [
  {
    path: ConstantList.ROOT_PATH + "salary/salary-result",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER", "HR_COMPENSATION_BENEFIT"],
  },
];

export default SalaryResultRoutes;
