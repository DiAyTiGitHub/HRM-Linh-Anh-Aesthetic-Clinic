import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./OrganizationalChartDataIndex"),
});

const ViewComponent = RenderScreen;

const OrganizationalChartRoutes = [
  {
    path: ConstantList.ROOT_PATH + "organization/diagram-list",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  }
];

export default OrganizationalChartRoutes;
