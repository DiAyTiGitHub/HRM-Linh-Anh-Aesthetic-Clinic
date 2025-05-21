import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./WaitingJobCandidateIndex"),
});

const ViewComponent = RenderScreen;

const WaitingJobCandidateRoutes = [
  {
    path: ConstantList.ROOT_PATH + "waiting-job-candidate",
    exact: true,
    component: ViewComponent
  },
];

export default WaitingJobCandidateRoutes;
