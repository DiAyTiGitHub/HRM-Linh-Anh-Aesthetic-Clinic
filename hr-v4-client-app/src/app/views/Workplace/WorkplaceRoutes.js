import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./WorkplaceIndex"),
});

const ViewComponent = RenderScreen;

const WorkplaceRoutes = [
  {
    path: ConstantList.ROOT_PATH + "category/workplace",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
];

export default WorkplaceRoutes;
