import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./StaffIpKeepingIndex"),
});

const ViewComponent = RenderScreen;

const StaffIpKeepingRoutes = [
  {
    path: ConstantList.ROOT_PATH + "staff-ip-keeping",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
];

export default StaffIpKeepingRoutes;
