import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./SalaryAreaIndex"),
});

const ViewComponent = RenderScreen;

const SalaryAreaRoutes = [
  {
    path: ConstantList.ROOT_PATH + "salary/salary-area",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
];

export default SalaryAreaRoutes;
