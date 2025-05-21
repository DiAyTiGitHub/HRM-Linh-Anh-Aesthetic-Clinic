import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./ExportCandidateIndex"),
});

const ViewComponent = RenderScreen;

const ExportCandidateReportRoutes = [
  {
    path: ConstantList.ROOT_PATH + "export-candidate-report",
    exact: true,
    component: ViewComponent
  },
];

export default ExportCandidateReportRoutes;
