import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./RecruitmentIndex"),
});

const RecruitmentCUIndex = EgretLoadable({
  loader: () => import("../RecruitmentCU/RecruitmentCUIndex"),
});

const RecruitmentProcessIndex = EgretLoadable({
  loader: () => import("../RecruitmentProcess/RecruitmentProcessIndex"),
});

const ViewComponent = RenderScreen;
const CUComponent = RecruitmentCUIndex;
const RecruitmentProcessComponent = RecruitmentProcessIndex;

const RecruitmentRoutes = [
 
  {
    path: ConstantList.ROOT_PATH + "recruitment",
    exact: true,
    component: ViewComponent
  },
  {
    path: ConstantList.ROOT_PATH + "recruitment/:id",
    exact: true,
    component: CUComponent
  },
  {
    path: ConstantList.ROOT_PATH + "recruitment-process/:recruitmentId",
    exact: true,
    component: RecruitmentProcessComponent
  },
];

export default RecruitmentRoutes;
