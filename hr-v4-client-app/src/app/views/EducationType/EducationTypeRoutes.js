import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const EducationTypeIndex = EgretLoadable({
  loader: () => import("./EducationTypeIndex"),
});
const ViewComponent = EducationTypeIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/education-type",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
