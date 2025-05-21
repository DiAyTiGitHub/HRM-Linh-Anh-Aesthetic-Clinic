import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const CivilServantCategoryIndex = EgretLoadable({
  loader: () => import("./CivilServantCategoryIndex"),
});
const ViewComponent = CivilServantCategoryIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/civilservantcategory",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
