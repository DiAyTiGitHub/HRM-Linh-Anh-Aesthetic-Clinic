import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./OnboaredCandidateIndex"),
});

const ViewComponent = RenderScreen;

const OnboaredCandidateRoutes = [
  {
    path: ConstantList.ROOT_PATH + "onboarded-candidate",
    exact: true,
    component: ViewComponent
  },
];

export default OnboaredCandidateRoutes;
