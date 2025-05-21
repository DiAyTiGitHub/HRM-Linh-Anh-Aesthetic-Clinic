import { EgretLoadable } from "egret";
import ConstantList from "../../appConfig";

const SpecialityIndex = EgretLoadable({
  loader: () => import("./SpecialityIndex"),
});
const ViewComponent = SpecialityIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/speciality",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
