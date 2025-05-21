import { EgretLoadable } from "egret";
import ConstantList from "app/appConfig";

const SalaryPeriodIndex = EgretLoadable({
  loader: () => import("./SalaryPeriodIndex"),
});
const ViewComponent = SalaryPeriodIndex;

const SalaryPeriodRoutes = [
  {
    path: ConstantList.ROOT_PATH + "salary/salary-period",
    exact: true,
    component: ViewComponent,
  },
];

export default SalaryPeriodRoutes;