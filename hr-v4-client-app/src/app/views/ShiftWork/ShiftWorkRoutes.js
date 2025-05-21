import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const ShiftWorkIndex = EgretLoadable({
  loader: () => import("./ShiftWorkIndex"),
});
const ViewComponent = ShiftWorkIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/shift-work",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
