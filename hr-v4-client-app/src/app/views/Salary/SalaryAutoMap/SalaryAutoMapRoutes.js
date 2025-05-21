import { EgretLoadable } from "egret";
import ConstantList from "../../../appConfig";

const SalaryAutoMapIndex = EgretLoadable({
  loader: () => import("./SalaryAutoMapIndex"),
});
const ViewComponent = SalaryAutoMapIndex;

const Routes = [
  {
    path: ConstantList.ROOT_PATH + "salary/salary-auto-map",
    exact: true,
    component: ViewComponent,
  },
];

export default Routes;
