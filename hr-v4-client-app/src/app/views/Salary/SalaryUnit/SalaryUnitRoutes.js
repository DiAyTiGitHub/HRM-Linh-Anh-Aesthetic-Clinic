import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./SalaryUnitIndex"),
});

const ViewComponent = RenderScreen;

const SalaryUnitRoutes = [
  {
    path: ConstantList.ROOT_PATH + "salary/salary-unit",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
];

export default SalaryUnitRoutes;
