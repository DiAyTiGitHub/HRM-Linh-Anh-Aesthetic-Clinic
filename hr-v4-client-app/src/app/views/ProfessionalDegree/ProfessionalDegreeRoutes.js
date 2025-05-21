import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
const ProfessionalDegreeIndex = EgretLoadable({
  loader: () => import("./ProfessionalDegreeIndex"),
});
const ViewComponent = ProfessionalDegreeIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/professionaldegree",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
