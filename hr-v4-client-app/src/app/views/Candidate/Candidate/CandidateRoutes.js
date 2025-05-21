import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./CandidateIndex"),
});

const CandidateCUIndex = EgretLoadable({
  loader: () => import("../CandidateCU/CandidateCUIndexV2"),
});

const CandidatesInRecruitmentIndex = EgretLoadable({
  loader: () => import("../CandidatesInRecruitment/CandidatesInRecruitmentIndex"),
});

const ViewComponent = RenderScreen;
const CUComponent = CandidateCUIndex;
const CandidatesInRecruitmentComponent = CandidatesInRecruitmentIndex;

const CandidateRoutes = [
  {
    path: ConstantList.ROOT_PATH + "candidate",
    exact: true,
    component: ViewComponent
  },
  {
    path: ConstantList.ROOT_PATH + "candidate/:id",
    exact: true,
    component: CUComponent
  },
  {
    path: ConstantList.ROOT_PATH + "candidates-in-recruitment/:recruitmentId",
    exact: true,
    component: CandidatesInRecruitmentComponent,
  },
];

export default CandidateRoutes;
