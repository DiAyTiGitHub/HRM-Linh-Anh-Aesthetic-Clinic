import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const GradeIndex = EgretLoadable({
  loader: () => import("./GradeIndex"),
});
const ViewComponent = GradeIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/grade",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
