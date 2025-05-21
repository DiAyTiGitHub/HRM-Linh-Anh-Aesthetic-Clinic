import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./ExamCandidateIndex"),
});

const ViewComponent = RenderScreen;

const ExamCandidateRoutes = [
  {
    path: ConstantList.ROOT_PATH + "exam-candidate",
    exact: true,
    component: ViewComponent
  },
];

export default ExamCandidateRoutes;
