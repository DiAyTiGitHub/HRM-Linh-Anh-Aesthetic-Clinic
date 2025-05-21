import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const CivilServantTypeIndex = EgretLoadable({
  loader: () => import("./CivilServantTypeIndex"),
});
const ViewComponent = CivilServantTypeIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/civilservanttype",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
