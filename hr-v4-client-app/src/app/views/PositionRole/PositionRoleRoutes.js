import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./PositionRolendex"),
});

const ViewComponent = RenderScreen;

const PositionRoleRoutes = [
  {
    path: ConstantList.ROOT_PATH + "organization/position-role",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
];

export default PositionRoleRoutes;
