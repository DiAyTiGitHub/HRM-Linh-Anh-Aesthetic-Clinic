import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const SalaryItemIndex = EgretLoadable({
  loader: () => import("./SalaryItemV2Index"),
});
const ViewComponent = SalaryItemIndex;

const SalaryItemRoutes = [
  {
    path: ConstantList.ROOT_PATH + "salary/salary-item",
    exact: true,
    component: ViewComponent,
  },
];

export default SalaryItemRoutes;