import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const LocationIndex = EgretLoadable({
  loader: () => import("./LocationIndex"),
});
const ViewComponent = LocationIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/location",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
