import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";
const AcademicIndex = EgretLoadable({
  loader: () => import("./AcademicIndex"),
});
const ViewComponent = AcademicIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/academic",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
