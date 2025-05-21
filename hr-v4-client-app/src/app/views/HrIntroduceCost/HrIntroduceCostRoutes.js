import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const RenderScreen = EgretLoadable ({
  loader:() => import("./HrIntroduceCostIndex"),
});

const ViewComponent = RenderScreen;

const HrIntroduceCostRoutes = [
  {
    path:ConstantList.ROOT_PATH + "hr-introduce-cost",
    exact:true,
    component:ViewComponent,
    auth:["ROLE_ADMIN", "HR_MANAGER", "HR_COMPENSATION_BENEFIT"],
  },
];

export default HrIntroduceCostRoutes;
