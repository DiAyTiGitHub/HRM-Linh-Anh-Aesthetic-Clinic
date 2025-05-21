import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./KPIResultIndex"),
});

const ViewComponent = RenderScreen;

const KPIResultRoutes = [
  {
    path: ConstantList.ROOT_PATH + "salary/kpi-result",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
];

export default KPIResultRoutes;
