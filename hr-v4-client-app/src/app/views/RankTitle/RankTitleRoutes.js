import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./RankTitleIndex"),
});

const ViewComponent = RenderScreen;

const RankTitleRoutes = [
  {
    path: ConstantList.ROOT_PATH + "organization/rank-title",
    exact: true,
    component: ViewComponent,
    auth: ["ROLE_ADMIN", "HR_MANAGER"],
  },
];

export default RankTitleRoutes;
