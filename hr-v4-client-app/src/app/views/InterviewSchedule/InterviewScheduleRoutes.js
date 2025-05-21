import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const EvaluationItemIndex = EgretLoadable({
  loader: () => import("./InterviewScheduleIndex"),
});
const ViewComponent = EvaluationItemIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "interview-schedule",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
