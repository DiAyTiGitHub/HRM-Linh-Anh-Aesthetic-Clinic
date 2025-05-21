import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const ColorIndex = EgretLoadable({
  loader: () => import("./ColorIndex"),
});
const ViewComponent = ColorIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/colors",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
