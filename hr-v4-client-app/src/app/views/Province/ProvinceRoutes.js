import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";
const ProvinceIndex = EgretLoadable({
  loader: () => import("./ProvinceIndex"),
});
const ViewComponent = ProvinceIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/province",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
