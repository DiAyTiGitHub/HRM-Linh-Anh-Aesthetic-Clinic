import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const PositionTitleIndex = EgretLoadable({
  loader: () => import("./PositionTitleIndex"),
});
const ViewComponent = PositionTitleIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/duty/:typeDuty",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
