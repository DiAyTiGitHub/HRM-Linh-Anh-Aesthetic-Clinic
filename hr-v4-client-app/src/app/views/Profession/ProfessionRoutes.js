import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
const ProfessionIndex = EgretLoadable({
  loader: () => import("./ProfessionIndex"),
});
const ViewComponent = ProfessionIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/profession",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
