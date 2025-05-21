import { EgretLoadable } from "../../../../../../egret";
import ConstantList from "../../../../../appConfig";

const EvaluationItemIndex = EgretLoadable({
  loader: () => import("./EvaluationItemIndex"),
});
const ViewComponent = EvaluationItemIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/evaluation-item",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
