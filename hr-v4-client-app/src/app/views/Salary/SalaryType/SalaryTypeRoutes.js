import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./SalaryTypeIndex"),
});

const ViewComponent = RenderScreen;

const SalaryTypeRoutes = [
  {
    path: ConstantList.ROOT_PATH + "salary/salary-type",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
];

export default SalaryTypeRoutes;
