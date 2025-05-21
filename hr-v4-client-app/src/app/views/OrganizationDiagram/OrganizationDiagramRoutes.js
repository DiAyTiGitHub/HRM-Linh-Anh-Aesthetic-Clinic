import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./OrganizationDiagramIndex"),
});

const ViewComponent = RenderScreen;

const PositionScreen = EgretLoadable({
  loader: () => import("./PositionDiagramIndex"),
});

const PositionViewComponent = PositionScreen;

const TreeScreen = EgretLoadable({
  loader: () => import("./OrganizationTreeIndex"),
});

const TreeScreenViewComponent = TreeScreen;

const OrganizationDiagramRoutes = [
  {
    path: ConstantList.ROOT_PATH + "position/diagram",
    exact: true,
    component: PositionViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
    settings: { layout1Settings: { leftSidebar: { mode: "close" } } }
  },
  {
    path: ConstantList.ROOT_PATH + "organization/tree",
    exact: true,
    component: TreeScreenViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
    settings: { layout1Settings: { leftSidebar: { mode: "close" } } }
  },
  {
    path: ConstantList.ROOT_PATH + "organization/diagram",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
    settings: { layout1Settings: { leftSidebar: { mode: "close" } } }
  },
  {
    path: ConstantList.ROOT_PATH + "organization/diagram/:id",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
    settings: { layout1Settings: { leftSidebar: { mode: "close" } } }
  },
];

export default OrganizationDiagramRoutes;
