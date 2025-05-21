import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const EducationDegreeIndex = EgretLoadable({
  loader: () => import("./EducationDegreeIndex"),
});
const ViewComponent = EducationDegreeIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/educationdegree",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
