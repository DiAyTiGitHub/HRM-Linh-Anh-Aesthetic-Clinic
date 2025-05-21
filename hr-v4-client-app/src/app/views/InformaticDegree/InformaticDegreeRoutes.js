import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
const InformaticDegreeIndex = EgretLoadable({
  loader: () => import("./InformaticDegreeIndex"),
});
const ViewComponent = InformaticDegreeIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/informaticdegree",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
