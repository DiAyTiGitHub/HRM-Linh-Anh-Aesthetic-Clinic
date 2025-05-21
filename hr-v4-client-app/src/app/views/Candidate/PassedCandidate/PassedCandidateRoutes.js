import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./PassedCandidateIndex"),
});

const ViewComponent = RenderScreen;

const PassedCandidateRoutes = [
  {
    path: ConstantList.ROOT_PATH + "passed-candidate",
    exact: true,
    component: ViewComponent
  },
];

export default PassedCandidateRoutes;
