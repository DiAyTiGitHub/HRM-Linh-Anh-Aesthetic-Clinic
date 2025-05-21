import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const DisciplineIndex = EgretLoadable({
  loader: () => import("./DisciplineIndex"),
});
const ViewComponent = DisciplineIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/discipline",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
