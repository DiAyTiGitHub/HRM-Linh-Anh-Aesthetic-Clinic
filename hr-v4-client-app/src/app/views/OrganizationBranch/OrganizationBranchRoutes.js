import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./OrganizationBranchIndex"),
});

const ViewComponent = RenderScreen;

const OrganizationBranchRoutes = [
  {
    path: ConstantList.ROOT_PATH + "organization/organization-branch",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
];

export default OrganizationBranchRoutes;
