import { EgretLoadable } from "../../../../../../egret";
import ConstantList from "../../../../../appConfig";

const EvaluationTemplateIndex = EgretLoadable({
  loader: () => import("./EvaluationTemplateIndex"),
});
const ViewComponent = EvaluationTemplateIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/evaluation-template",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
