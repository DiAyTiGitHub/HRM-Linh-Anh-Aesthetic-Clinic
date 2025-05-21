import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";
const AdministrativeIndex = EgretLoadable({
  loader: () => import("./AdministrativeUnitIndex"),
});
const ViewComponent = AdministrativeIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "category/administrative-unit",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
