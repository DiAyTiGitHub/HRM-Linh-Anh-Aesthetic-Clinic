import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./KPIIndex"),
});

const ViewComponent = RenderScreen;

const KPIRoutes = [
  {
    path: ConstantList.ROOT_PATH + "salary/kpi",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
];

export default KPIRoutes;
