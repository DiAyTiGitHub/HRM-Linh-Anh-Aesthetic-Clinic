import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const RenderScreen = EgretLoadable({
  loader: () => import("./RecruitmentRequestV2Index"),
});

const ViewComponent = RenderScreen;

const RecruitmentRequestV2Routes = [
  {
    path: ConstantList.ROOT_PATH + "recruitment-request-v2",
    exact: true,
    component: ViewComponent
  },
];

export default RecruitmentRequestV2Routes;
