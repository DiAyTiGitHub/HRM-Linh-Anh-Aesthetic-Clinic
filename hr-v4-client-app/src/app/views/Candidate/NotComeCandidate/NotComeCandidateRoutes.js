import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./NotComeCandidateIndex"),
});

const ViewComponent = RenderScreen;

const NotComeCandidateRoutes = [
  {
    path: ConstantList.ROOT_PATH + "not-come-candidate",
    exact: true,
    component: ViewComponent
  },
];

export default NotComeCandidateRoutes;
