import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./OrganizationIndex"),
});

const ViewComponent = RenderScreen;

const OrganizationRoutes = [
  {
    path: ConstantList.ROOT_PATH + "category/organization",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER", "HR_STAFF_VIEW"],
  },
];

export default OrganizationRoutes;
