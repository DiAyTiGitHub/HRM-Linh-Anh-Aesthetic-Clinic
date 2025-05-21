import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./RecruitmentPlanV2Index"),
});

const ViewComponent = RenderScreen;

const RecruitmentPlanV2Routes = [
  {
    path: ConstantList.ROOT_PATH + "recruitment-plan-v2",
    exact: true,
    component: ViewComponent
  },
];

export default RecruitmentPlanV2Routes;
